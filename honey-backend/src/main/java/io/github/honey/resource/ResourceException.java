package io.github.honey.resource;

final class ResourceException extends RuntimeException {

  ResourceException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
