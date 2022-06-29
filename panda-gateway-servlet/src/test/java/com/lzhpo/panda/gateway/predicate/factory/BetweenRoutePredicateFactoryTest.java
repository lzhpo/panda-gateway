package com.lzhpo.panda.gateway.predicate.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.predicate.factory.BetweenRoutePredicateFactory.Config;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author lzhpo
 */
class BetweenRoutePredicateFactoryTest extends ZonedDateTimeMethod {

  private static MockHttpServletRequest request;

  @BeforeAll
  static void setUp() {
    request = new MockHttpServletRequest();
  }

  @Test
  void testCreate() {
    Config config = new Config();
    config.setStart(ZonedDateTime.now().minusYears(6));
    config.setEnd(ZonedDateTime.now().plusYears(6));

    BetweenRoutePredicateFactory factory = new BetweenRoutePredicateFactory();
    RoutePredicate predicate = factory.create(config);
    assertNotNull(predicate);

    boolean result = predicate.test(request);
    assertTrue(result);
  }
}
