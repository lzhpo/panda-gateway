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

  private int order;

  @Valid private List<ComponentDefinition> predicates = new ArrayList<>();

  @Valid private List<ComponentDefinition> filters = new ArrayList<>();

  private Map<String, Object> metadata = new HashMap<>();
}
