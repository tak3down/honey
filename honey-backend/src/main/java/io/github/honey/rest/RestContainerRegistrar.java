package io.github.honey.rest;

import io.javalin.Javalin;
import io.javalin.community.routing.dsl.DslRoute;
import io.javalin.http.Context;
import java.util.Collection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
final class RestContainerRegistrar implements ApplicationContextAware {

  private final Javalin javalin;

  @Autowired
  RestContainerRegistrar(final Javalin javalin) {
    this.javalin = javalin;
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    applicationContext.getBeansOfType(RestContainer.class).values().stream()
        .map(RestContainer::routes)
        .flatMap(Collection::stream)
        .forEach(this::registerControllerRoute);
  }

  private void registerControllerRoute(final DslRoute<Context, Object> route) {
    switch (route.getMethod()) {
      case HEAD -> javalin.head(route.getPath(), route.getHandler()::invoke);
      case PATCH -> javalin.patch(route.getPath(), route.getHandler()::invoke);
      case OPTIONS -> javalin.options(route.getPath(), route.getHandler()::invoke);
      case GET -> javalin.get(route.getPath(), route.getHandler()::invoke);
      case PUT -> javalin.put(route.getPath(), route.getHandler()::invoke);
      case POST -> javalin.post(route.getPath(), route.getHandler()::invoke);
      case DELETE -> javalin.delete(route.getPath(), route.getHandler()::invoke);
      case AFTER -> javalin.after(route.getPath(), route.getHandler()::invoke);
      case AFTER_MATCHED -> javalin.afterMatched(route.getPath(), route.getHandler()::invoke);
      case BEFORE -> javalin.before(route.getPath(), route.getHandler()::invoke);
      case BEFORE_MATCHED -> javalin.beforeMatched(route.getPath(), route.getHandler()::invoke);
    }
  }
}
