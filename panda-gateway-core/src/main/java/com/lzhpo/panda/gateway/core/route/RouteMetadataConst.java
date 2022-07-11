package com.lzhpo.panda.gateway.core.route;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RouteMetadataConst {

  /** Route predicate for {@link RelationType} */
  public static final String PREDICATE_RELATION = "predicate-relation";

  /** Http client connect timeout. Unit: milliseconds */
  public static final String CONNECT_TIMEOUT = "connect-timeout";

  /** Http client response timeout. Unit: milliseconds */
  public static final String RESPONSE_TIMEOUT = "response-timeout";
}
