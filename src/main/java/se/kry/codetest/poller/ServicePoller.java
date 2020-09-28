package se.kry.codetest.poller;

import se.kry.codetest.core.Service;
import se.kry.codetest.core.UpdateServiceCallback;

public interface ServicePoller {

  void poll(Service service, UpdateServiceCallback updateServiceCallback);
}
