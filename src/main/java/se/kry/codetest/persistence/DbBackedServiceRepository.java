package se.kry.codetest.persistence;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import se.kry.codetest.core.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public final class DbBackedServiceRepository implements ServiceRepository {
  private static final Logger LOGGER = Logger.getLogger(DbBackedServiceRepository.class.getName());

  private final DBConnector connector;

  public DbBackedServiceRepository(DBConnector connector) {
    this.connector = connector;
  }

  @Override
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

  @Override
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

  @Override
  public Future<Void> delete(String serviceName) {
    return connector.query("DELETE FROM service WHERE name = ?", new JsonArray().add(serviceName)).mapEmpty();
  }

  @Override
  public Future<Void> update(Service service) {
    LOGGER.info(() -> String.format("Updating service %s", service));

    return connector.query(
        "UPDATE service SET url = ?, status = ? WHERE name = ?",
        new JsonArray()
            .add(service.getUrl())
            .add(service.getStatus())
            .add(service.getName()))
        .mapEmpty();
  }
}
