package se.kry.codetest.migrate;

import io.vertx.core.Vertx;
import se.kry.codetest.persistence.DBConnector;

public class DBMigration {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DBConnector connector = new DBConnector(vertx);

    connector.query("CREATE TABLE IF NOT EXISTS service (name VARCHAR(50) NOT NULL PRIMARY KEY, url VARCHAR(128) NOT NULL, status VARCHAR(15) NOT NULL)")
        .setHandler(asyncResult -> {
          if (asyncResult.succeeded()) {
            System.out.println("completed db migrations");
          } else {
            asyncResult.cause().printStackTrace();
          }

          vertx.close(shutdown -> {
            System.exit(0);
          });
        });
  }
}
