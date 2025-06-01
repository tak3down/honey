package io.github.honey;

import static io.github.honey.ApiResponse.internalServerError;
import static io.github.honey.ApiResponse.notFoundError;
import static io.github.honey.Either.*;
import static io.github.honey.HoneyController.responseEither;
import static io.javalin.community.routing.Route.GET;
import static io.javalin.http.ContentType.OCTET_STREAM;
import static java.util.Optional.ofNullable;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class ResourceController extends HoneyControllerRegistry {

  private final ResourceResolver resourceResolver;

  ResourceController(final ResourceResolver resourceResolver) {
    this.resourceResolver = resourceResolver;
    registerRoutes();
  }

  private void registerRoutes() {
    try {

      final Set<String> resourcePaths = listResources();

      final Set<String> registeredPaths = new HashSet<>();
      final Set<HoneyController> controllers = new HashSet<>();

      final HoneyController index = indexHandler();
      controllers.add(index);
      registeredPaths.add(index.path());

      for (final String resourcePath : resourcePaths) {
        final HoneyController controller;
        if (resourcePath.endsWith("/")) {
          controller = directoryHandler(resourcePath);
        } else {
          controller = rootFileHandler(resourcePath);
        }

        if (registeredPaths.add(controller.path())) {
          controllers.add(controller);
        }
      }

      routes(controllers.toArray(HoneyController[]::new));

    } catch (final Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  private HoneyController rootFileHandler(final String resourcePath) {
    String webPath = fixPath("/" + resourcePath);

    String stripLeadingStatic;
    if (resourcePath.startsWith("static/")) {
      stripLeadingStatic = resourcePath.substring("static/".length());
    } else {
      stripLeadingStatic = resourcePath;
    }

    return new HoneyController(
        webPath, responseEither(ctx -> respondWithBundledResource(ctx, stripLeadingStatic)), GET);
  }

  private String fixPath(String path) {
    if (path.startsWith("/static/")) {
      return path.substring("/static".length());
    } else {
      return path;
    }
  }

  private HoneyController directoryHandler(final String directoryPath) {
    String base = fixPath("/" + directoryPath);
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }

    String routePattern = base + "/{path}";
    return new HoneyController(
        routePattern,
        responseEither(
            ctx -> {
              String fileName = ctx.pathParam("path");
              String fullPath = directoryPath + fileName;

              String stripLeadingStatic;
              if (fullPath.startsWith("static/")) {
                stripLeadingStatic = fullPath.substring("static/".length());
              } else {
                stripLeadingStatic = fullPath;
              }

              return respondWithBundledResource(ctx, stripLeadingStatic);
            }),
        GET);
  }

  private HoneyController indexHandler() {
    return new HoneyController(
        "/", responseEither(ctx -> respondWithBundledResource(ctx, "index.html")), GET);
  }

  private Either<ApiResponse, InputStream> respondWithBundledResource(
      final Context ctx, final String uri) {
    return respondWithResource(
        ctx, uri, () -> getClass().getClassLoader().getResourceAsStream("static/" + uri));
  }

  private Either<ApiResponse, InputStream> respondWithResource(
      final Context context, final String uri, final Source source) {

    ContentType contentType = ContentType.getContentTypeByExtension(getExtension(uri));
    context.contentType(contentType != null ? contentType.getMimeType() : OCTET_STREAM);

    if (uri.endsWith(".html") || uri.endsWith(".js")) {
      return respondWithProcessedResource(context, uri, source);
    } else {
      return respondWithRawResource(source);
    }
  }

  private Either<ApiResponse, InputStream> respondWithProcessedResource(
      final Context context, final String uri, final Source source) {
    context.res().setCharacterEncoding("UTF-8");

    Either<IOException, InputStream> supplied =
        ofNullable(resourceResolver.resolve(uri, source))
            .map(ResourceSupplier::supply)
            .orElse(null);

    if (supplied == null) {
      return notFoundError(uri);
    }

    if (supplied.isLeft()) {
      return internalServerError("Cannot supply resource: " + uri);
    }

    return right(supplied.right());
  }

  private Either<ApiResponse, InputStream> respondWithRawResource(final Source source) {
    InputStream in = source.get();
    return (in != null) ? right(in) : notFoundError("Resource not found");
  }

  private String getExtension(final String uri) {
    int lastDot = uri.lastIndexOf('.');
    return (lastDot != -1) ? uri.substring(lastDot + 1) : "";
  }

  private Set<String> listResources() throws IOException {
    Set<String> resources = new HashSet<>();
    final String path = "static";
    String jarPath = getClass().getClassLoader().getResource(path).getPath();
    String jarFilePath = jarPath.substring(5, jarPath.indexOf("!"));

    try (JarFile jar = new JarFile(jarFilePath)) {
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String name = entry.getName(); // e.g. "static/index.html" or "static/_next/static/css/"
        if (name.startsWith(path)) {
          // Keep both files and directories. Directories already end with “/”
          resources.add(name);
        }
      }
    }
    return resources;
  }
}
