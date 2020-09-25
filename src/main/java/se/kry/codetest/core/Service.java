package se.kry.codetest.core;

import java.util.Objects;

public final class Service {
  private String name;
  private String status;

  public Service(String name, String status) {
    this.name = name;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Service service = (Service) o;
    return Objects.equals(name, service.name) &&
        Objects.equals(status, service.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, status);
  }

  @Override
  public String toString() {
    return "Service{" +
        "url='" + name + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}
