package se.kry.codetest.poller;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.client.WebClient;
import se.kry.codetest.core.Service;
import se.kry.codetest.core.UpdateServiceCallback;

public class WebPoller implements ServicePoller {
  private final WebClient webClient;

  public WebPoller(WebClient webClient) {
    this.webClient = webClient;
  }

  public void poll(Service service, UpdateServiceCallback updateServiceCallback) {
    webClient.get(443, service.getUrl(), "/").ssl(true).send(asyncResult -> {
      if (asyncResult.succeeded()) {
        if (asyncResult.result().statusCode() == HttpResponseStatus.OK.code()) {
          service.setStatus("OK");
        } else {
          service.setStatus("FAIL");
        }
      } else {
        service.setStatus("FAIL");
      }

      updateServiceCallback.update(service);
    });
  }
}
