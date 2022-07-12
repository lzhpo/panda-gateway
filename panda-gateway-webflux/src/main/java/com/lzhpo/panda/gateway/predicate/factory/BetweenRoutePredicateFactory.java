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

import com.lzhpo.panda.gateway.core.route.BetweenZonedDateTimeConfig;
import com.lzhpo.panda.gateway.core.route.BetweenZonedDateTimeConfig.ZonedDateTimePair;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Route predicate by between time.
 *
 * @author lzhpo
 */
@Slf4j
public class BetweenRoutePredicateFactory
    extends AbstractRoutePredicateFactory<BetweenZonedDateTimeConfig> {

  public BetweenRoutePredicateFactory() {
    super(BetweenZonedDateTimeConfig.class);
  }

  @Override
  public RoutePredicate create(BetweenZonedDateTimeConfig config) {
    return serverWebExchange -> {
      ZonedDateTime nowTime = ZonedDateTime.now();
      List<ZonedDateTimePair> times = config.getTimes();
      return times.stream()
          .anyMatch(
              zonedDateTimePair -> {
                ZonedDateTime startTime = zonedDateTimePair.getStart();
                ZonedDateTime endTime = zonedDateTimePair.getEnd();
                return nowTime.isAfter(startTime) && nowTime.isBefore(endTime);
              });
    };
  }
}
