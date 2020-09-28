package se.kry.codetest.handler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.core.Service;
import se.kry.codetest.persistence.ServiceRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public final class GetServiceHandler extends AbstractServiceHandler {

  public GetServiceHandler(ServiceRepository serviceRepository) {
    super(serviceRepository, GetServiceHandler.class);
  }

  @Override
  public void handle(RoutingContext routingContext) {
    serviceRepository.findAll().setHandler(asyncResult -> {
      List<JsonObject> jsonServices = asyncResult.result().stream().map(this::toJsonObject).collect(Collectors.toList());

      if (asyncResult.succeeded()) {
        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(new JsonArray(jsonServices).encode());
      } else {
        logErrorAndSendInternalErrorResponse(routingContext, asyncResult);
      }
    });
  }

  private JsonObject toJsonObject(Service service) {
    return new JsonObject()
        .put("name", service.getName())
        .put("url", service.getUrl())
        .put("createdAt", service.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
        .put("status", service.getStatus());
  }
}
