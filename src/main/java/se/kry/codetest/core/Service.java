package se.kry.codetest.core;

import java.util.Objects;

public final class Service {
  private String name;
  private String url;
  private String status;

  public Service(String name, String url, String status) {
    this.name = name;
    this.url = url;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Service service = (Service) o;
    return name.equals(service.name) &&
        url.equals(service.url) &&
        status.equals(service.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, url, status);
  }
}
