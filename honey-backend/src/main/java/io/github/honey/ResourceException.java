package io.github.honey;

public final class ResourceException extends RuntimeException {

  ResourceException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
