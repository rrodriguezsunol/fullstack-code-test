package se.kry.codetest.persistence;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import se.kry.codetest.core.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public final class ServiceRepository {
  private final DBConnector connector;

  public ServiceRepository(DBConnector connector) {
    this.connector = connector;
  }

  public Future<Collection<Service>> findAll() {
    return connector.query("SELECT * FROM service").map(resultSet -> {
      Collection<Service> retrievedServices = new ArrayList<>(resultSet.getNumRows());

      for (JsonObject row : resultSet.getRows()) {
        retrievedServices.add(new Service(
            row.getString("name"),
            row.getString("url"),
            row.getString("status"),
            LocalDateTime.parse(row.getString("created_at"))));
      }

      return retrievedServices;
    });
  }

  public Future<Service> save(Service newService) {
    return connector.query(
        "INSERT INTO service VALUES (?, ?, ?, ?)",
        new JsonArray()
            .add(newService.getName())
            .add(newService.getUrl())
            .add(newService.getCreatedAt().format(ISO_DATE_TIME))
            .add(newService.getStatus()))
        .map(newService);
  }

  public Future<Void> delete(String serviceName) {
    return connector.query("DELETE FROM service WHERE name = ?", new JsonArray().add(serviceName)).mapEmpty();
  }
}
