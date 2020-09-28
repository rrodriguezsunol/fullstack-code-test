package se.kry.codetest.handler;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.core.Service;
import se.kry.codetest.persistence.ServiceRepository;

public final class CreateServiceHandler extends AbstractServiceHandler {

  public CreateServiceHandler(ServiceRepository serviceRepository) {
    super(serviceRepository, CreateServiceHandler.class);
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
        logErrorAndSendInternalErrorResponse(routingContext, asyncResult);
      }
    });
  }
}
