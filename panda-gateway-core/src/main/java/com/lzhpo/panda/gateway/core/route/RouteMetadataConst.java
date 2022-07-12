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
