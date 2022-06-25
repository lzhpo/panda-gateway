package com.lzhpo.panda.gateway.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Route definition
 *
 * @author lzhpo
 */
@Data
public class RouteDefinition {

  private String id;

  private String uri;

  private int order;

  private List<ComponentDefinition> predicates = new ArrayList<>();

  private List<ComponentDefinition> filters = new ArrayList<>();

  private Map<String, String> metadata = new LinkedHashMap<>();
}
