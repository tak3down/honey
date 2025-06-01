package io.github.honey;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

final class NotFoundController extends HoneyControllerRegistry
    implements ExceptionHandler<NotFoundResponse>, Handler {

  private final Consumer<Context> defaultNotFoundHandler;

  NotFoundController() {
    this.defaultNotFoundHandler =
        context -> {
          context.html(NotFoundTemplate.createNotFoundPage(context.req().getRequestURI()));
          context.status(HttpStatus.NOT_FOUND);
        };
  }

  @Override
  public void handle(@NotNull final NotFoundResponse exception, @NotNull final Context ctx) {
    defaultNotFoundHandler.accept(ctx);
  }

  @Override
  public void handle(@NotNull final Context ctx) {
    defaultNotFoundHandler.accept(ctx);
  }
}
