package com.lzhpo.panda.gateway.predicate.factory;

import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by request client ip.
 *
 * @author lzhpo
 */
@Slf4j
public class ClientIpRoutePredicateFactory
    extends AbstractRoutePredicateFactory<ClientIpRoutePredicateFactory.Config> {

  private final ClientIpResolver clientIpResolver;

  public ClientIpRoutePredicateFactory(ClientIpResolver clientIpResolver) {
    super(Config.class);
    this.clientIpResolver = clientIpResolver;
  }

  @Override
  public RoutePredicate create(Config config) {
    return serverWebExchange -> {
      List<String> configClientIps = config.getClientIps();
      String requestClientIp = clientIpResolver.resolve(serverWebExchange);
      return configClientIps.stream()
          .anyMatch(configClientIp -> configClientIp.equals(requestClientIp));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> clientIps;
  }
}
