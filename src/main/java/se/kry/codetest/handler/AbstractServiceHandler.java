package se.kry.codetest.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import se.kry.codetest.persistence.ServiceRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractServiceHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = Logger.getLogger(AbstractServiceHandler.class.getName());

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
}
