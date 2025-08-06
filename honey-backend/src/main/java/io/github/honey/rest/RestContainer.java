package io.github.honey.rest;

import io.github.honey.either.Either;
import io.javalin.community.routing.dsl.DslContainer;
import io.javalin.community.routing.dsl.DslRoute;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public abstract class RestContainer
    implements DslContainer<DslRoute<Context, Object>, Context, Object> {

  private final Set<RestRoute> restRoutes;

  protected RestContainer() {
    this.restRoutes = new HashSet<>();
  }

  protected <R> Consumer<Context> respondEither(
      final Function<Context, Either<RestResponse, R>> handler) {
    return context -> {
      final Either<RestResponse, R> either = handler.apply(context);

      respond(
              it -> {
                if (either.isRight()) {
                  return either.right();
                } else {

                  final RestResponse response = either.left();
                  it.status(response.status()).result(response.message());
                  return null;
                }
              })
          .accept(context);
    };
  }

  protected <R> Consumer<Context> respond(final Function<Context, R> contextToResult) {
    return context -> {
      final R result = contextToResult.apply(context);

      context.res().setContentLength(-1);

      if (result == null) {
        context.status(HttpStatus.NOT_FOUND);
        return;
      }

      if (result instanceof final InputStream inputStream) {
        context.result(inputStream);
        return;
      }

      if (result instanceof final String string) {
        context.result(string);
        return;
      }

      if (result instanceof final RestResponse response) {
        context.status(response.status()).result(response.message());
        return;
      }

      context.json(result);
    };
  }

  protected void registerRoute(final RestRoute... routes) {
    restRoutes.addAll(List.of(routes));
  }

  @Override
  public @NotNull Collection<DslRoute<Context, Object>> routes() {
    return restRoutes.stream()
        .map(RestRoute::routes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }
}
