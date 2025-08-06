package io.github.honey.resource;

import io.github.honey.either.Either;
import io.github.honey.rest.RestContainer;
import io.github.honey.rest.RestResponse;
import io.github.honey.rest.RestRoute;
import io.javalin.community.routing.Route;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
final class ResourceController extends RestContainer {

  private final ResourceResolver resourceResolver;

  @Autowired
  ResourceController(final ResourceResolver resourceResolver) {
    this.resourceResolver = resourceResolver;
  }

  @PostConstruct
  private void registerRoutes() {
    final Set<String> resourcePaths = listResources("static");

    final Set<String> registeredPaths = new HashSet<>();
    final Set<RestRoute> controllers = new HashSet<>();

    final RestRoute index = indexHandler();
    controllers.add(index);
    registeredPaths.add(index.path());

    for (final String resourcePath : resourcePaths) {
      final RestRoute controller;
      if (resourcePath.endsWith("/")) {
        controller = directoryHandler(resourcePath);
      } else {
        controller = rootFileHandler(resourcePath);
      }

      if (registeredPaths.add(controller.path())) {
        controllers.add(controller);
      }
    }

    registerRoute(controllers.toArray(RestRoute[]::new));
  }

  private RestRoute rootFileHandler(final String resourcePath) {
    final String webPath = fixPath("/" + resourcePath);

    final String stripLeadingStatic;
    if (resourcePath.startsWith("static/")) {
      stripLeadingStatic = resourcePath.substring("static/".length());
    } else {
      stripLeadingStatic = resourcePath;
    }

    return new RestRoute(
        webPath,
        respondEither(context -> respondWithBundledResource(context, stripLeadingStatic)),
        Route.GET);
  }

  private String fixPath(final String path) {
    return path.startsWith("/static/") ? path.substring("/static".length()) : path;
  }

  private RestRoute directoryHandler(final String directoryPath) {
    String base = fixPath("/" + directoryPath);
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }

    final String routePattern = base + "/{path}";
    return new RestRoute(
        routePattern,
        respondEither(
            context -> {
              final String fileName = context.pathParam("path");
              final String fullPath = directoryPath + fileName;

              final String stripLeadingStatic;
              if (fullPath.startsWith("static/")) {
                stripLeadingStatic = fullPath.substring("static/".length());
              } else {
                stripLeadingStatic = fullPath;
              }

              return respondWithBundledResource(context, stripLeadingStatic);
            }),
        Route.GET);
  }

  private RestRoute indexHandler() {
    return new RestRoute(
        "/",
        respondEither(context -> respondWithBundledResource(context, "index.html")),
        Route.GET);
  }

  private Either<RestResponse, InputStream> respondWithBundledResource(
      final Context ctx, final String uri) {
    return respondWithResource(
        ctx, uri, () -> getClass().getClassLoader().getResourceAsStream("static/" + uri));
  }

  private Either<RestResponse, InputStream> respondWithResource(
      final Context context, final String uri, final Supplier<InputStream> resourceSupplier) {

    final ContentType contentType = ContentType.getContentTypeByExtension(getExtension(uri));
    context.contentType(contentType != null ? contentType.getMimeType() : ContentType.OCTET_STREAM);

    if (uri.endsWith(".html") || uri.endsWith(".js")) {
      return respondWithProcessedResource(context, uri, resourceSupplier);
    } else {
      return respondWithRawResource(resourceSupplier);
    }
  }

  private Either<RestResponse, InputStream> respondWithProcessedResource(
      final Context context, final String uri, final Supplier<InputStream> resourceSupplier) {
    context.res().setCharacterEncoding("UTF-8");

    Either<IOException, InputStream> resolvedResource = null;

    final Supplier<Either<IOException, InputStream>> resolve =
        resourceResolver.resolve(uri, resourceSupplier);
    if (resolve != null) {
      resolvedResource = resolve.get();
    }

    if (resolvedResource == null) {
      return RestResponse.notFound(uri).either();
    }

    if (resolvedResource.isLeft()) {
      return RestResponse.internalServer("Cannot supply resource: %s".formatted(uri)).either();
    }

    return Either.right(resolvedResource.right());
  }

  private Either<RestResponse, InputStream> respondWithRawResource(
      final Supplier<InputStream> inputStreamSupplier) {
    final InputStream inputStream = inputStreamSupplier.get();
    return inputStream != null
        ? Either.right(inputStream)
        : RestResponse.notFound("Resource not found").either();
  }

  private String getExtension(final String uri) {
    final int lastDot = uri.lastIndexOf('.');
    return lastDot == -1 ? "" : uri.substring(lastDot + 1);
  }

  @SuppressWarnings("SameParameterValue")
  private Set<String> listResources(final String path) {
    final URL resourceByPath = getClass().getClassLoader().getResource(path);
    if (resourceByPath == null) {
      return Set.of();
    }

    final String jarPath = resourceByPath.getPath();
    final String jarFilePath = jarPath.substring(5, jarPath.indexOf("!"));

    final Set<String> resources = new HashSet<>();
    try (final JarFile jar = new JarFile(jarFilePath)) {
      final Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        final String name = entries.nextElement().getName();
        if (name.startsWith(path)) {
          resources.add(name);
        }
      }
    } catch (final Exception exception) {
      throw new ResourceException("Failed to iterate through jar file resources", exception);
    }

    return resources;
  }
}
