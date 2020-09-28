package se.kry.codetest.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.persistence.ServiceRepository;

public final class DeleteServiceHandler extends AbstractServiceHandler {
  private static final String SERVICE_NAME_PATH_PARAM_KEY = "serviceName";

  public DeleteServiceHandler(ServiceRepository serviceRepository) {
    super(serviceRepository, DeleteServiceHandler.class);
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String serviceName = routingContext.pathParam(SERVICE_NAME_PATH_PARAM_KEY);

    serviceRepository.delete(serviceName).setHandler(asyncResult -> {
      if (asyncResult.succeeded()) {
        routingContext.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
      } else {
        logErrorAndSendInternalErrorResponse(routingContext, asyncResult);
      }
    });
  }
}
