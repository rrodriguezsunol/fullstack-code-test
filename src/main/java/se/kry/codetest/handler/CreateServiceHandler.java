package se.kry.codetest.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.core.Service;
import se.kry.codetest.persistence.ServiceRepository;

public final class CreateServiceHandler implements Handler<RoutingContext> {
  private final ServiceRepository serviceRepository;

  public CreateServiceHandler(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    JsonObject jsonBody = routingContext.getBodyAsJson();

    serviceRepository.save(new Service(jsonBody.getString("url"), "UNKNOWN"));

    routingContext.response()
        .putHeader("content-type", "text/plain")
        .end("OK");
  }
}
