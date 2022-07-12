/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
