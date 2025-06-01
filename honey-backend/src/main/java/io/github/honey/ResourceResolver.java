package io.github.honey;

import java.io.InputStream;
import org.slf4j.LoggerFactory;

final class ResourceResolver {

  ResourceSupplier resolve(final String uri, final Source source) {
    try {
      final InputStream input = source.get();
      if (input == null) {
        System.out.println("Resource not found: " + uri);
        return null;
      }

      return () -> Either.right(input);
    } catch (final Exception exception) {
      LoggerFactory.getLogger(ResourceResolver.class)
          .error("Failed to resolve resource", exception);
      return null;
    }
  }
}
