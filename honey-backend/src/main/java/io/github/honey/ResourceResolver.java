package io.github.honey;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import org.slf4j.LoggerFactory;

final class ResourceResolver {

  private final HoneyConfig honeyConfig;

  ResourceResolver(final HoneyConfig honeyConfig) {
    this.honeyConfig = honeyConfig;
  }

  ResourceSupplier resolve(final String uri, final Supplier<InputStream> resourceSource) {
    try {
      final InputStream originalStream = resourceSource.get();
      if (originalStream == null) {
        return null;
      }

      if (uri.endsWith(".html") || uri.endsWith(".js")) {
        final byte[] rawBytes = toByteArray(originalStream);

        final byte[] replacedBytes = getReplacedBytes(rawBytes);
        return () -> Either.right(new ByteArrayInputStream(replacedBytes));
      }

      return () -> Either.right(originalStream);

    } catch (final Exception ex) {
      LoggerFactory.getLogger(ResourceResolver.class)
          .error("Failed to resolve resource {}", uri, ex);
      return null;
    }
  }

  private byte[] getReplacedBytes(final byte[] rawBytes) {
    String textContent = new String(rawBytes, UTF_8);

    textContent = textContent.replace("{{HONEY.PORT}}", String.valueOf(honeyConfig.port));
    textContent = textContent.replace("{{HONEY.HOST}}", String.valueOf(honeyConfig.host));
    textContent = textContent.replace("{{HONEY.API_BASE}}", String.valueOf(honeyConfig.apiBase));
    textContent = textContent.replace("{{HONEY.FORWARED_IP}}", honeyConfig.forwardedIp);

    return textContent.getBytes(UTF_8);
  }

  private byte[] toByteArray(final InputStream in) throws IOException {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final byte[] tmp = new byte[4 * 1024];
    int read;
    while ((read = in.read(tmp)) != -1) {
      buffer.write(tmp, 0, read);
    }
    return buffer.toByteArray();
  }
}
