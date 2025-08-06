package io.github.honey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.honey.config.HoneyConfig;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.util.ConcurrencyUtil;
import java.time.Duration;
import org.eclipse.jetty.server.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HoneyConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    return new JsonMapper()
        .registerModule(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Bean
  public Javalin getJavalin(final HoneyConfig honeyConfig, final ObjectMapper objectMapper) {
    return Javalin.createAndStart(
        javalinConfig -> {
          javalinConfig.jetty.defaultHost = honeyConfig.host;
          javalinConfig.jetty.defaultPort = honeyConfig.port;

          javalinConfig.pvt.jetty.server =
              new Server(ConcurrencyUtil.jettyThreadPool("web thread", 4, 16, true));

          javalinConfig.showJavalinBanner = false;
          javalinConfig.http.asyncTimeout = Duration.ofMinutes(10L).toMillis();
          javalinConfig.contextResolver.ip =
              request -> {
                final String forwardedIp = request.header(honeyConfig.forwardedIp);
                if (forwardedIp != null) {
                  return forwardedIp;
                }

                return request.req().getRemoteAddr();
              };

          javalinConfig.jsonMapper(new JavalinJackson(objectMapper, true));

          javalinConfig.bundledPlugins.enableCors(
              corsPluginConfig -> corsPluginConfig.addRule(CorsPluginConfig.CorsRule::anyHost));

          javalinConfig.bundledPlugins.enableDevLogging();
        });
  }
}
