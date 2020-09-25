package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import se.kry.codetest.core.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  private Collection<Service> serviceCollection = new ArrayList<>();

  //TODO use this
  private DBConnector connector;

  private BackgroundPoller poller = new BackgroundPoller();


  @Override
  public void start(Future<Void> startFuture) {
    connector = new DBConnector(vertx);

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    serviceCollection.add(new Service("https://www.kry.se", "UNKNOWN"));

    vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(serviceCollection));

    setRoutes(router);

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            System.out.println("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void setRoutes(Router router) {
    router.route("/*").handler(StaticHandler.create());

    router.get("/service").handler(req -> {
      List<JsonObject> jsonServices = serviceCollection.stream().map(JsonObject::mapFrom).collect(Collectors.toList());

      req.response()
          .putHeader("content-type", "application/json")
          .end(new JsonArray(jsonServices).encode());
    });

    router.post("/service").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();

      serviceCollection.add(new Service(jsonBody.getString("url"), "UNKNOWN"));

      req.response()
          .putHeader("content-type", "text/plain")
          .end("OK");
    }).failureHandler(routingContext -> {
      System.out.println("Server Error: " + routingContext.failure());
      routingContext.response().setStatusCode(500).end();
    });
  }

}



