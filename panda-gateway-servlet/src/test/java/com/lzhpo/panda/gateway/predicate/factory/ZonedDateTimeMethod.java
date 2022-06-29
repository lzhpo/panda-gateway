package com.lzhpo.panda.gateway.predicate.factory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lzhpo
 */
public class ZonedDateTimeMethod {

  protected static String minusYears(int years) {
    return ZonedDateTime.now().minusYears(years).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }

  protected static String plusYears(int years) {
    return ZonedDateTime.now().plusYears(years).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }
}
