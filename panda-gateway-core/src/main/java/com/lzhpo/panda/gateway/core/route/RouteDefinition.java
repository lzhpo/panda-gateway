package com.lzhpo.panda.gateway.core.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
  private Map<String, Object> metadata = new HashMap<>();

  /** Enhance config */
  private EnhanceConfig enhances;

  /** Some enhance config for route */
  @Data
  @Validated
  public static class EnhanceConfig {

    /** Predicates relation */
    @NotNull private RelationType predicatesRelation = RelationType.AND;
  }
}
