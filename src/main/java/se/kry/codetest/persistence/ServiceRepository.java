package se.kry.codetest.persistence;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import se.kry.codetest.core.Service;

import java.util.ArrayList;
import java.util.Collection;

public final class ServiceRepository {
  private final DBConnector connector;

  public ServiceRepository(DBConnector connector) {
    this.connector = connector;
  }

  public Future<Collection<Service>> findAll() {
    return connector.query("SELECT * FROM service").map(resultSet -> {
      Collection<Service> retrievedServices = new ArrayList<>(resultSet.getNumRows());

      for (JsonObject row : resultSet.getRows()) {
        retrievedServices.add(new Service(row.getString("url"), row.getString("status")));
      }

      return retrievedServices;
    });
  }

  public Future<Service> save(Service newService) {
    return connector.query(
        "INSERT INTO SERVICE VALUES (?, ?)",
        new JsonArray().add(newService.getName()).add(newService.getStatus()))
        .map(newService);
  }
}
