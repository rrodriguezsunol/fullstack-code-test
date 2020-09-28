package se.kry.codetest.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.core.Service;
import se.kry.codetest.persistence.ServiceRepository;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractServiceHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = Logger.getLogger(AbstractServiceHandler.class.getName());

  protected static final String NAME_ATTRIBUTE_KEY = "name";
  protected static final String URL_ATTRIBUTE_KEY = "url";

  protected final ServiceRepository serviceRepository;
  private final Class<?> subClass;

  public AbstractServiceHandler(ServiceRepository serviceRepository, Class<?> subClass) {
    this.serviceRepository = serviceRepository;
    this.subClass = subClass;
  }

  protected void logErrorAndSendInternalErrorResponse(RoutingContext routingContext, AsyncResult<?> asyncResult) {
    LOGGER.log(Level.SEVERE, "Unexpected internal error at " + subClass.getSimpleName(), asyncResult.cause());
    routingContext.response().setStatusCode(500).end();
  }

  protected JsonObject toJsonObject(Service service) {
    return new JsonObject()
        .put(NAME_ATTRIBUTE_KEY, service.getName())
        .put(URL_ATTRIBUTE_KEY, service.getUrl())
        .put("createdAt", service.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
        .put("status", service.getStatus());
  }
}
