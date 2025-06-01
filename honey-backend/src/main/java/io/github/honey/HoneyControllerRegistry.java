package io.github.honey;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import io.javalin.community.routing.dsl.DslContainer;
import io.javalin.community.routing.dsl.DslRoute;
import io.javalin.http.Context;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public abstract class HoneyControllerRegistry
    implements DslContainer<DslRoute<Context, Object>, Context, Object> {

  private final Set<HoneyController> bunchOfRoutes = new HashSet<>();

  public void route(final HoneyController route) {
    bunchOfRoutes.add(route);
  }

  public void routes(final HoneyController... routes) {
    stream(routes).forEach(this::route);
  }

  @Override
  public @NotNull Collection<DslRoute<Context, Object>> routes() {
    return bunchOfRoutes.stream()
        .map(HoneyController::toDslRoutes)
        .flatMap(Collection::stream)
        .collect(toSet());
  }
}
