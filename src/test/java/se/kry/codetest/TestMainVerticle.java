package se.kry.codetest;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Start a web server on localhost responding to path /service on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_server(Vertx vertx, VertxTestContext testContext) {
    WebClient.create(vertx)
        .get(8080, "::1", "/service")
        .send(response -> testContext.verify(() -> {
          assertEquals(HttpResponseStatus.OK.code(), response.result().statusCode());
          JsonArray body = response.result().bodyAsJsonArray();
          assertEquals(1, body.size());

          JsonObject firstService = body.getJsonObject(0);

          assertEquals("https://www.kry.se", firstService.getString("name"));
          assertEquals("UNKNOWN", firstService.getString("status"));

          testContext.completeNow();
        }));
  }

  @Test
  @DisplayName("Save a new service")
  void save_service_entry(Vertx vertx, VertxTestContext testContext) {
    JsonObject newServiceJsonObject = new JsonObject().put("url", "https://www.google.com");

    WebClient webClient = WebClient.create(vertx);

    webClient
        .post(8080, "::1", "/service")
        .sendJsonObject(newServiceJsonObject, asyncResult -> {
          testContext.verify(() -> assertEquals(HttpResponseStatus.OK.code(), asyncResult.result().statusCode()));

          webClient
              .get(8080, "::1", "/service")
              .send(response -> testContext.verify(() -> {
                assertEquals(HttpResponseStatus.OK.code(), response.result().statusCode());
                JsonArray body = response.result().bodyAsJsonArray();

                assertEquals(2, body.size());

                JsonObject secondService = body.getJsonObject(1);

                assertEquals("https://www.google.com", secondService.getString("name"));
                assertEquals("UNKNOWN", secondService.getString("status"));

                testContext.completeNow();
              }));
        });
  }

}
