package se.kry.codetest.handler;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
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
        new Service(jsonBody.getString(NAME_ATTRIBUTE_KEY), jsonBody.getString(URL_ATTRIBUTE_KEY)));

    serviceFuture.setHandler(asyncResult -> {
      if (asyncResult.succeeded()) {
        routingContext.response()
            .setStatusCode(HttpResponseStatus.CREATED.code())
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(toJsonObject(asyncResult.result()).encode());
      } else {
        logErrorAndSendInternalErrorResponse(routingContext, asyncResult);
      }
    });
  }
}
