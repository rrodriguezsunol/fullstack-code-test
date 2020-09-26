package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import se.kry.codetest.core.Service;
import se.kry.codetest.handler.CreateServiceHandler;
import se.kry.codetest.handler.GetServiceHandler;
import se.kry.codetest.persistence.DBConnector;
import se.kry.codetest.persistence.ServiceRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

  private ServiceRepository serviceRepository;

  private BackgroundPoller poller;


  @Override
  public void start(Future<Void> startFuture) {
    serviceRepository = new ServiceRepository(new DBConnector(vertx));
    poller = new BackgroundPoller();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    serviceRepository.save(new Service("https://www.kry.se", "UNKNOWN"));

    vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(serviceRepository.findAll()));

    setRoutes(router);

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            LOGGER.info("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void setRoutes(Router router) {
    router.route("/*").handler(StaticHandler.create());

    router.get("/service").handler(new GetServiceHandler(serviceRepository));

    router.post("/service").handler(new CreateServiceHandler(serviceRepository))
        .failureHandler(routingContext -> {
      LOGGER.log(Level.SEVERE, "Unexpected internal error:", routingContext.failure());
      routingContext.response().setStatusCode(500).end();
    });
  }

}



