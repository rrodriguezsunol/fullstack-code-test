package se.kry.codetest.core;

import io.vertx.core.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import se.kry.codetest.persistence.ServiceRepository;
import se.kry.codetest.poller.ServicePoller;

import java.util.Arrays;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServiceHealthCheckerTest {

  private ServiceHealthChecker serviceHealthChecker;

  @Mock private ServiceRepository mockedServiceRepository;
  @Mock private ServicePoller mockedWebPoller;

  @BeforeEach
  void init_test_subject() {
    serviceHealthChecker = new ServiceHealthChecker(mockedWebPoller, mockedServiceRepository);
  }

  @Test
  void checks_zero_services_when_there_are_not_registered() {
    given(mockedServiceRepository.findAll()).willReturn(Future.succeededFuture(emptyList()));

    serviceHealthChecker.checkAll();

    then(mockedWebPoller).shouldHaveNoInteractions();
  }

  @Test
  void polls_one_service_when_the_repository_returns_one() {
    Service serviceA = new Service("service-a", "http://service-a.com");
    given(mockedServiceRepository.findAll()).willReturn(Future.succeededFuture(singletonList(serviceA)));

    serviceHealthChecker.checkAll();

    then(mockedWebPoller).should().poll(eq(serviceA), any());
  }

  @Test
  void polls_two_services_when_the_repository_returns_two() {
    Service serviceA = new Service("service-a", "http://service-a.com");
    Service serviceB = new Service("service-b", "http://service-b.com");
    given(mockedServiceRepository.findAll()).willReturn(Future.succeededFuture(Arrays.asList(serviceA, serviceB)));

    serviceHealthChecker.checkAll();

    then(mockedWebPoller).should().poll(eq(serviceA), any());
    then(mockedWebPoller).should().poll(eq(serviceB), any());
  }
}