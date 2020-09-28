package se.kry.codetest.core;

import io.vertx.core.Future;
import se.kry.codetest.persistence.ServiceRepository;
import se.kry.codetest.poller.ServicePoller;

import java.util.Collection;

public final class ServiceHealthChecker {
  private final ServicePoller webPoller;
  private final ServiceRepository serviceRepository;

  public ServiceHealthChecker(ServicePoller webPoller, ServiceRepository serviceRepository) {
    this.webPoller = webPoller;
    this.serviceRepository = serviceRepository;
  }

  public void checkAll() {
    Future<Collection<Service>> allServicesFuture = serviceRepository.findAll();

    allServicesFuture.setHandler(asyncResult -> {
      for (Service service : asyncResult.result()) {
        webPoller.poll(service, serviceRepository::update);
      }
    });
  }
}
