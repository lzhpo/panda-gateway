package com.lzhpo.panda.gateway.webflux.predicate.factory;

import com.lzhpo.panda.gateway.webflux.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.webflux.support.ClientIpResolver;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * Route predicate by between time.
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
      List<String> sources = config.getSources();
      String clientIp = clientIpResolver.resolve(serverWebExchange);
      return sources.stream().anyMatch(source -> source.equals(clientIp));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> sources;
  }
}
