package io.github.honey;

public final class HoneyConfigException extends RuntimeException {

  public HoneyConfigException(final String message) {
    super(message);
  }

  public HoneyConfigException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
