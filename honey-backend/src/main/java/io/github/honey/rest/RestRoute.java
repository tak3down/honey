package io.github.honey.rest;

import io.javalin.community.routing.Route;
import io.javalin.community.routing.dsl.DefaultDslRoute;
import io.javalin.community.routing.dsl.DslRoute;
import io.javalin.http.Context;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record RestRoute(String path, Route[] methods, Consumer<Context> handler) {

  public RestRoute(final String path, final Consumer<Context> handler, final Route... methods) {
    this(path, methods, handler);
  }

  public <T> Set<DslRoute<Context, T>> routes() {
    return Arrays.stream(methods)
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
        .collect(Collectors.toSet());
  }
}
