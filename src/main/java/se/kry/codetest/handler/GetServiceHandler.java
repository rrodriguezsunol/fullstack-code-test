package se.kry.codetest.handler;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.persistence.ServiceRepository;

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
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(new JsonArray(jsonServices).encode());
      } else {
        logErrorAndSendInternalErrorResponse(routingContext, asyncResult);
      }
    });
  }
}
