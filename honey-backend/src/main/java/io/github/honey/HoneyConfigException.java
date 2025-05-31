package io.github.honey;

public final class HoneyConfigException extends RuntimeException {

  public HoneyConfigException(String message) {
    super(message);
  }

  public HoneyConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
