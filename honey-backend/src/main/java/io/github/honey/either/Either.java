package io.github.honey.either;

import java.util.NoSuchElementException;

public sealed interface Either<L, R> permits Either.Left, Either.Right {

  static <L, R> Either<L, R> left(final L value) {
    return new Left<>(value);
  }

  static <L, R> Either<L, R> right(final R value) {
    return new Right<>(value);
  }

  default L left() {
    throw new NoSuchElementException("Either does not contain left.");
  }

  default R right() {
    throw new NoSuchElementException("Either does not contain right.");
  }

  boolean isLeft();

  boolean isRight();

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
}
