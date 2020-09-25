package se.kry.codetest.persistence;

import se.kry.codetest.core.Service;

import java.util.ArrayList;
import java.util.Collection;

public final class ServiceRepository {
  //TODO use this
  private final DBConnector connector;

  private final Collection<Service> serviceCollection = new ArrayList<>();

  public ServiceRepository(DBConnector connector) {
    this.connector = connector;
  }

  public Collection<Service> findAll() {
    return new ArrayList<>(serviceCollection);
  }

  public Service save(Service newService) {
    serviceCollection.add(newService);

    return newService;
  }

}
