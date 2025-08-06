package io.github.honey.notfound;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import jakarta.annotation.PostConstruct;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;

@Controller
final class NotFoundController implements ExceptionHandler<NotFoundResponse>, Handler {

  private final Javalin javalin;
  private final Consumer<Context> defaultNotFoundHandler;

  NotFoundController(final Javalin javalin) {
    this.javalin = javalin;
    this.defaultNotFoundHandler =
        context -> {
          if (context.req().getRequestURI().contains("api/")) {
            return;
          }

          context.html(NotFoundPageRenderer.renderNotFoundPage(context.req().getRequestURI()));
          context.status(HttpStatus.NOT_FOUND);
        };
  }

  @PostConstruct
  private void registerNotFoundHandler() {
    javalin
        .unsafeConfig()
        .router
        .mount(
            javalinDefaultRouting -> {
              javalinDefaultRouting.exception(NotFoundResponse.class, this);
              javalinDefaultRouting.error(404, this);
            });
  }

  @Override
  public void handle(final @NotNull NotFoundResponse exception, final @NotNull Context context) {
    defaultNotFoundHandler.accept(context);
  }

  @Override
  public void handle(final @NotNull Context ctx) {
    defaultNotFoundHandler.accept(ctx);
  }
}
