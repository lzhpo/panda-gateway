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

package com.lzhpo.panda.gateway.core.route;

import cn.hutool.core.bean.BeanUtil;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Setter
@Getter
@Validated
public class BetweenZonedDateTimeConfig {

  @NotEmpty private List<ZonedDateTimePair> times = new ArrayList<>();

  @ComponentConstructorArgs
  public BetweenZonedDateTimeConfig(Map<String, Map<String, Object>> args) {
    Map<String, Object> timesMap = args.get("times");
    timesMap.forEach(
        (index, timePair) -> {
          ZonedDateTimePair dateTimePair = BeanUtil.toBean(timePair, ZonedDateTimePair.class);
          if (Objects.nonNull(dateTimePair)) {
            this.times.add(dateTimePair);
          }
        });
  }

  @Data
  @Validated
  public static class ZonedDateTimePair {

    @NotNull private ZonedDateTime start;

    @NotNull private ZonedDateTime end;
  }
}
