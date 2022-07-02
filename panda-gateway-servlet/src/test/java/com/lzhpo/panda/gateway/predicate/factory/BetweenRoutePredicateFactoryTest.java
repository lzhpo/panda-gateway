package com.lzhpo.panda.gateway.predicate.factory;

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
    //    Config config = new Config();
    //    config.setStart(ZonedDateTime.now().minusYears(6));
    //    config.setEnd(ZonedDateTime.now().plusYears(6));
    //
    //    BetweenRoutePredicateFactory factory = new BetweenRoutePredicateFactory();
    //    RoutePredicate predicate = factory.create(config);
    //    assertNotNull(predicate);
    //
    //    boolean result = predicate.test(request);
    //    assertTrue(result);
  }
}
