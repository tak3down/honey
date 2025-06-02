package io.github.honey;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T extends Throwable, R> extends Supplier<R> {

  @Override
  default R get() {
    try {
      return getWithException();
    } catch (final Throwable throwable) {
      throw new IllegalStateException(throwable);
    }
  }

  R getWithException() throws T;
}
