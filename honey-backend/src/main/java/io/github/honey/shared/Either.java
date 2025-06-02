package io.github.honey.shared;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public sealed interface Either<L, R> permits Either.Left, Either.Right {

  static <L, R> Either<L, R> left(final L value) {
    return new Left<>(value);
  }

  static <L, R> Either<L, R> right(final R value) {
    return new Right<>(value);
  }

  static <T extends Throwable, R> Either<T, R> throwing(
      final Class<T> throwableType, final ThrowingSupplier<T, R> supplier) {
    try {
      return right(supplier.get());
    } catch (final Throwable throwable) {
      if (throwableType.isInstance(throwable)) {
        return left(throwableType.cast(throwable));
      } else {
        throw new IllegalStateException(throwable);
      }
    }
  }

  default L left() {
    throw new NoSuchElementException("Either does not contain left.");
  }

  default R right() {
    throw new NoSuchElementException("Either does not contain right.");
  }

  default Either<L, R> peekLeft(final Consumer<L> consumer) {
    if (isLeft()) {
      consumer.accept(left());
    }
    return this;
  }

  default Either<L, R> peekRight(final Consumer<R> consumer) {
    if (isRight()) {
      consumer.accept(right());
    }
    return this;
  }

  default Either<L, R> peek(final Consumer<L> leftConsumer, final Consumer<R> rightConsumer) {
    peekLeft(leftConsumer);
    peekRight(rightConsumer);
    return this;
  }

  record Left<L, R>(L value) implements Either<L, R> {
    @Override
    public L left() {
      return value;
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public boolean isRight() {
      return false;
    }
  }

  record Right<L, R>(R value) implements Either<L, R> {
    @Override
    public R right() {
      return value;
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public boolean isRight() {
      return true;
    }
  }

  boolean isLeft();

  boolean isRight();
}
