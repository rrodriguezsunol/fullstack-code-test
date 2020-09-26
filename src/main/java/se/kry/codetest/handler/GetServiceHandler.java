package se.kry.codetest.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.persistence.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

public final class GetServiceHandler implements Handler<RoutingContext> {
  private final ServiceRepository serviceRepository;

  public GetServiceHandler(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    List<JsonObject> jsonServices = serviceRepository.findAll().stream().map(JsonObject::mapFrom).collect(Collectors.toList());

    routingContext.response()
        .putHeader("content-type", "application/json")
        .end(new JsonArray(jsonServices).encode());
  }
}
