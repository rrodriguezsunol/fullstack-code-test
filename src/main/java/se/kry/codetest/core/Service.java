package se.kry.codetest.core;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Service {
  private String name;
  private String url;
  private String status;
  private LocalDateTime createdAt;

  public Service(String name, String url, String status) {
    this(name, url, status, LocalDateTime.now());
  }

  public Service(String name, String url, String status, LocalDateTime createdAt) {
    this.name = name;
    this.url = url;
    this.status = status;
    this.createdAt = createdAt;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Service service = (Service) o;
    return name.equals(service.name) &&
        url.equals(service.url) &&
        status.equals(service.status) &&
        createdAt.equals(service.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, url, status, createdAt);
  }

  @Override
  public String toString() {
    return "Service{" +
        "name='" + name + '\'' +
        ", url='" + url + '\'' +
        ", status='" + status + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}
