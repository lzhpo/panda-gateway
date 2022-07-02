package com.lzhpo.panda.gateway.core;

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
