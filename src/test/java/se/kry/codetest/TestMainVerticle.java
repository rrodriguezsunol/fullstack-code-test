package se.kry.codetest;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import se.kry.codetest.persistence.DBConnector;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeAll
  static void clear_services_table(Vertx vertx, VertxTestContext testContext) {
    DBConnector dbConnector = new DBConnector(vertx);

    dbConnector.query("DELETE FROM service").setHandler(testContext.completing());
  }

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @BeforeEach
  void insert_kry_service(Vertx vertx, VertxTestContext testContext) {
    DBConnector dbConnector = new DBConnector(vertx);

    dbConnector.query("INSERT INTO service VALUES ('kry-service', 'https://www.kry.se', '2020-09-27T09:30:00', 'UNKNOWN')")
        .setHandler(testContext.completing());
  }

  @AfterEach
  void delete_all_services(Vertx vertx, VertxTestContext testContext) {
    DBConnector dbConnector = new DBConnector(vertx);

    dbConnector.query("DELETE FROM service").setHandler(testContext.completing());
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

          assertEquals("kry-service", firstService.getString("name"));
          assertEquals("https://www.kry.se", firstService.getString("url"));
          assertEquals(LocalDateTime.parse("2020-09-27T09:30:00"), LocalDateTime.parse(firstService.getString("createdAt")));
          assertEquals("UNKNOWN", firstService.getString("status"));

          testContext.completeNow();
        }));
  }

  @Test
  @DisplayName("Save a new service")
  void save_new_service(Vertx vertx, VertxTestContext testContext) {
    String googleMapsServiceName = "google-maps";
    String googleMapsUrl = "https://maps.google.com";
    JsonObject newServiceJsonObject = new JsonObject().put("name", googleMapsServiceName).put("url", googleMapsUrl);

    WebClient webClient = WebClient.create(vertx);

    webClient
        .post(8080, "::1", "/service")
        .sendJsonObject(newServiceJsonObject, asyncResultForPost -> {
          testContext.verify(() -> {
            assertEquals(HttpResponseStatus.CREATED.code(), asyncResultForPost.result().statusCode());

            assertEquals("application/json", asyncResultForPost.result().getHeader(HttpHeaderNames.CONTENT_TYPE.toString()));

            JsonObject responseJson = asyncResultForPost.result().bodyAsJsonObject();
            assertEquals(googleMapsServiceName, responseJson.getString("name"));
            assertEquals(googleMapsUrl, responseJson.getString("url"));
            assertNotNull(LocalDateTime.parse(responseJson.getString("createdAt")));
            assertEquals("UNKNOWN", responseJson.getString("status"));


            webClient
                .get(8080, "::1", "/service")
                .send(asyncResultForGet -> testContext.verify(() -> {
                  assertEquals(HttpResponseStatus.OK.code(), asyncResultForGet.result().statusCode());
                  JsonArray body = asyncResultForGet.result().bodyAsJsonArray();

                  assertEquals(2, body.size());

                  JsonObject secondService = body.getJsonObject(1);

                  assertEquals(googleMapsServiceName, secondService.getString("name"));
                  assertEquals(googleMapsUrl, secondService.getString("url"));
                  assertNotNull(LocalDateTime.parse(secondService.getString("createdAt")));
                  assertEquals("UNKNOWN", secondService.getString("status"));

                  testContext.completeNow();
                }));
          });
        });
  }

  @Test
  @DisplayName("Delete an existing service")
  void delete_existing_service(Vertx vertx, VertxTestContext testContext) {
    WebClient webClient = WebClient.create(vertx);

    webClient.delete(8080, "::1", "/service/kry-service")
        .send(asyncResultForDelete -> testContext.verify(() -> {
          assertEquals(HttpResponseStatus.NO_CONTENT.code(), asyncResultForDelete.result().statusCode());

          webClient
              .get(8080, "::1", "/service")
              .send(asyncResultForGet -> testContext.verify(() -> {
                JsonArray body = asyncResultForGet.result().bodyAsJsonArray();

                assertEquals(0, body.size());

                testContext.completeNow();
              }));
        }));
  }
}
