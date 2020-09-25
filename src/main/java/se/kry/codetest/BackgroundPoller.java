package se.kry.codetest;

import io.vertx.core.Future;
import se.kry.codetest.core.Service;

import java.util.Collection;
import java.util.List;

public class BackgroundPoller {

  public Future<List<String>> pollServices(Collection<Service> services) {
    //TODO
    return Future.failedFuture("TODO");
  }

}
