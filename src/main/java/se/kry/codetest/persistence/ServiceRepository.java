package se.kry.codetest.persistence;

import io.vertx.core.Future;
import se.kry.codetest.core.Service;

import java.util.Collection;

public interface ServiceRepository {

  Future<Collection<Service>> findAll();

  Future<Service> save(Service newService);

  Future<Void> delete(String serviceName);
}
