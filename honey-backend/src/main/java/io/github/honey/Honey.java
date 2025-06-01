package io.github.honey;

import static io.javalin.util.ConcurrencyUtil.jettyThreadPool;
import static java.time.Duration.ofMinutes;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.community.routing.dsl.DslRoute;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.json.JavalinJackson;
import io.javalin.plugin.bundled.CorsPluginConfig.CorsRule;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;

public final class Honey {

  private Javalin javalin;

  public void start() {
    final HoneyConfig honeyConfig = new HoneyConfig();
    honeyConfig.load();

    final ObjectMapper jsonMapper =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    ServiceLoader.load(DeserializationProblemHandler.class).forEach(jsonMapper::addHandler);

    final Set<DslRoute<Context, Object>> dslRoutes = new HashSet<>();

    final ThreadPool webThreadPool = jettyThreadPool("Web Thread (5) -", 16, 16, false);
    this.javalin =
        Javalin.createAndStart(
            javalinConfig -> {
              javalinConfig.jetty.defaultHost = honeyConfig.host;
              javalinConfig.jetty.defaultPort = honeyConfig.port;

              javalinConfig.pvt.jetty.server = new Server(webThreadPool);

              javalinConfig.showJavalinBanner = false;
              javalinConfig.http.asyncTimeout = ofMinutes(10L).toMillis();
              javalinConfig.contextResolver.ip =
                  request ->
                      ofNullable(request.header(honeyConfig.forwardedIp))
                          .orElseGet(() -> request.req().getRemoteAddr());

              javalinConfig.jsonMapper(new JavalinJackson(jsonMapper, false));

              javalinConfig.bundledPlugins.enableCors(
                  corsPluginConfig -> corsPluginConfig.addRule(CorsRule::anyHost));

              javalinConfig.bundledPlugins.enableDevLogging();

              final NotFoundController notFoundController = new NotFoundController();
              javalinConfig.router.mount(
                  routing -> {
                    routing.exception(NotFoundResponse.class, notFoundController);
                    routing.error(404, notFoundController);
                  });
            });

    final UserDetailsService userDetailsService = new UserDetailsService();
    final GameService gameService = new GameService(honeyConfig);

    final GameController gameController = new GameController(gameService, userDetailsService);
    dslRoutes.addAll(gameController.routes());

    final AuthController authController = new AuthController(userDetailsService);
    dslRoutes.addAll(authController.routes());

    final ResourceResolver resourceResolver = new ResourceResolver(honeyConfig);

    final ResourceController resourceController = new ResourceController(resourceResolver);
    dslRoutes.addAll(resourceController.routes());

    registerRoutes(javalin, dslRoutes);
  }

  private void registerRoutes(
      final Javalin javalin, final Set<DslRoute<Context, Object>> dslRoutes) {
    for (final DslRoute<Context, Object> route : dslRoutes) {
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

  public void stop() {
    if (javalin != null) {
      javalin.stop();
    }
  }
}
