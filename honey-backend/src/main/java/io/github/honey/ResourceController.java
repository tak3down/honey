package io.github.honey;

import static io.github.honey.ApiResponse.internalServerError;
import static io.github.honey.ApiResponse.notFoundError;
import static io.github.honey.Either.right;
import static io.github.honey.HoneyController.responseEither;
import static io.javalin.community.routing.Route.GET;
import static io.javalin.http.ContentType.OCTET_STREAM;
import static java.util.Optional.ofNullable;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
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
    final String webPath = fixPath("/" + resourcePath);

    final String stripLeadingStatic;
    if (resourcePath.startsWith("static/")) {
      stripLeadingStatic = resourcePath.substring("static/".length());
    } else {
      stripLeadingStatic = resourcePath;
    }

    return new HoneyController(
        webPath, responseEither(ctx -> respondWithBundledResource(ctx, stripLeadingStatic)), GET);
  }

  private String fixPath(final String path) {
    return path.startsWith("/static/") ? path.substring("/static".length()) : path;
  }

  private HoneyController directoryHandler(final String directoryPath) {
    String base = fixPath("/" + directoryPath);
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }

    final String routePattern = base + "/{path}";
    return new HoneyController(
        routePattern,
        responseEither(
            ctx -> {
              final String fileName = ctx.pathParam("path");
              final String fullPath = directoryPath + fileName;

              final String stripLeadingStatic;
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
      final Context context, final String uri, final Supplier<InputStream> resourceSource) {

    final ContentType contentType = ContentType.getContentTypeByExtension(getExtension(uri));
    context.contentType(contentType != null ? contentType.getMimeType() : OCTET_STREAM);

    if (uri.endsWith(".html") || uri.endsWith(".js")) {
      return respondWithProcessedResource(context, uri, resourceSource);
    } else {
      return respondWithRawResource(resourceSource);
    }
  }

  private Either<ApiResponse, InputStream> respondWithProcessedResource(
      final Context context, final String uri, final Supplier<InputStream> resourceSource) {
    context.res().setCharacterEncoding("UTF-8");

    final Either<IOException, InputStream> supplied =
        ofNullable(resourceResolver.resolve(uri, resourceSource))
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

  private Either<ApiResponse, InputStream> respondWithRawResource(
      final Supplier<InputStream> resourceSource) {
    final InputStream in = resourceSource.get();
    return in != null ? right(in) : notFoundError("Resource not found");
  }

  private String getExtension(final String uri) {
    final int lastDot = uri.lastIndexOf('.');
    return lastDot != -1 ? uri.substring(lastDot + 1) : "";
  }

  private Set<String> listResources() throws IOException {
    final Set<String> resources = new HashSet<>();
    final String path = "static";
    final String jarPath = getClass().getClassLoader().getResource(path).getPath();
    final String jarFilePath = jarPath.substring(5, jarPath.indexOf("!"));

    try (final JarFile jar = new JarFile(jarFilePath)) {
      final Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        final JarEntry entry = entries.nextElement();
        final String name =
            entry.getName(); // e.g. "static/index.html" or "static/_next/static/css/"
        if (name.startsWith(path)) {
          // Keep both files and directories. Directories already end with “/”
          resources.add(name);
        }
      }
    }
    return resources;
  }
}
