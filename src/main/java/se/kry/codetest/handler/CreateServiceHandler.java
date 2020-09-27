package se.kry.codetest.handler;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.core.Service;
import se.kry.codetest.persistence.ServiceRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class CreateServiceHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = Logger.getLogger(CreateServiceHandler.class.getName());

  private final ServiceRepository serviceRepository;

  public CreateServiceHandler(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    JsonObject jsonBody = routingContext.getBodyAsJson();

    Future<Service> serviceFuture = serviceRepository.save(
        new Service(jsonBody.getString("name"), jsonBody.getString("url"), "UNKNOWN"));

    serviceFuture.setHandler(asyncResult -> {
      if (asyncResult.succeeded()) {
        routingContext.response()
            .putHeader("content-type", "text/plain")
            .end("OK");
      } else {
        LOGGER.log(Level.SEVERE, "Unexpected internal error:", asyncResult.cause());
        routingContext.response().setStatusCode(500).end();
      }
    });
  }
}
