package io.github.honey;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import io.github.honey.shared.ApiResponse;
import io.github.honey.shared.Either;
import io.github.honey.shared.HtmlResponse;
import io.javalin.community.routing.Route;
import io.javalin.community.routing.dsl.DefaultDslRoute;
import io.javalin.community.routing.dsl.DslRoute;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public record HoneyController(String path, Route[] methods, Consumer<Context> handler) {

  public HoneyController(
      final String path, final Consumer<Context> handler, final Route... methods) {
    this(path, methods, handler);
  }

  public static <R> Consumer<Context> responseEither(
      final Function<Context, Either<ApiResponse, R>> handler) {
    return context -> {
      final Either<ApiResponse, R> either = handler.apply(context);

      response(
              it -> {
                if (either.isRight()) {
                  return either.right();
                } else {

                  final ApiResponse apiResponse = either.left();
                  it.status(apiResponse.status()).result(apiResponse.message());
                  return null;
                }
              })
          .accept(context);
    };
  }

  public static <R> Consumer<Context> response(final Function<Context, R> handler) {
    return context -> {
      final R result = handler.apply(context);

      context.res().setContentLength(-1);

      if (result instanceof final InputStream inputStream) {
        context.result(inputStream);
      } else if (result instanceof final String string) {
        context.result(string);
      } else if (result instanceof final ApiResponse apiResponse) {
        context.status(apiResponse.status()).result(apiResponse.message());
      } else if (result instanceof final HtmlResponse htmlResponse) {
        context.html(htmlResponse.content());
      } else if (result != null) {
        context.json(result);
      } else {
        context.status(HttpStatus.NOT_FOUND);
      }
    };
  }

  public <T> Set<DslRoute<Context, T>> toDslRoutes() {
    return stream(methods)
        .map(
            method ->
                new DefaultDslRoute<Context, T>(
                    method,
                    path,
                    null,
                    context -> {
                      handler.accept(context);
                      return null;
                    }))
        .collect(toSet());
  }
}
