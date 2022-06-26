package com.lzhpo.panda.gateway.predicate.factory;

import cn.hutool.core.collection.ListUtil;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import com.lzhpo.panda.gateway.support.ClientIpResolver;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
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
  public List<String> configFieldOrder() {
    return ListUtil.of("sources");
  }

  @Override
  public ConfigTypeEnum configFieldType() {
    return ConfigTypeEnum.DEFAULT;
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      List<String> sources = config.getSources();
      String clientIp = clientIpResolver.resolve(request);
      return sources.stream().anyMatch(source -> source.equals(clientIp));
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty private List<String> sources;
  }
}
