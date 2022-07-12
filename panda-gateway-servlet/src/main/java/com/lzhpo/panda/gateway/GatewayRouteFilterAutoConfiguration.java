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

package com.lzhpo.panda.gateway;

import com.lzhpo.panda.gateway.filter.factory.AddRequestHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.AddRequestParameterRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.AddResponseHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RateLimiterRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RemoveRequestHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RemoveRequestParameterRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.RemoveResponseHeaderRouteFilterFactory;
import com.lzhpo.panda.gateway.filter.factory.StripPrefixRouteFilterFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzhpo
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class GatewayRouteFilterAutoConfiguration {

  @Bean
  public RateLimiterRouteFilterFactory rateLimiterRouteFilterFactory() {
    return new RateLimiterRouteFilterFactory();
  }

  @Bean
  public AddRequestParameterRouteFilterFactory addRequestParameterRouteFilterFactory() {
    return new AddRequestParameterRouteFilterFactory();
  }

  @Bean
  public RemoveRequestParameterRouteFilterFactory removeRequestParameterRouteFilterFactory() {
    return new RemoveRequestParameterRouteFilterFactory();
  }

  @Bean
  public StripPrefixRouteFilterFactory stripPrefixRouteFilterFactory() {
    return new StripPrefixRouteFilterFactory();
  }

  @Bean
  public AddRequestHeaderRouteFilterFactory addRequestHeaderRouteFilterFactory() {
    return new AddRequestHeaderRouteFilterFactory();
  }

  @Bean
  public RemoveRequestHeaderRouteFilterFactory removeRequestHeaderRouteFilterFactory() {
    return new RemoveRequestHeaderRouteFilterFactory();
  }

  @Bean
  public AddResponseHeaderRouteFilterFactory addResponseHeaderRouteFilterFactory() {
    return new AddResponseHeaderRouteFilterFactory();
  }

  @Bean
  public RemoveResponseHeaderRouteFilterFactory removeResponseHeaderRouteFilterFactory() {
    return new RemoveResponseHeaderRouteFilterFactory();
  }
}
