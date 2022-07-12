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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * Route definition
 *
 * @author lzhpo
 */
@Data
@Validated
public class RouteDefinition {

  /** Unique route id. */
  @NotBlank private String id;

  /** e.g: lb://<serviceId>, http://<serviceId>:<port>, https://<serviceId>:<port> */
  @NotBlank private String uri;

  /** Route order */
  private int order;

  /** Route predicates */
  @Valid private List<ComponentDefinition> predicates = new ArrayList<>();

  /** Just for route filters */
  @Valid private List<ComponentDefinition> filters = new ArrayList<>();

  /** Route metadata */
  private Map<String, String> metadata = new HashMap<>();
}
