package com.lzhpo.panda.gateway.route;

import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.predicate.RoutePredicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Data
@Validated
public class Route {

  @NotBlank private String id;

  @NotBlank private String uri;

  private int order;

  @Valid private List<RoutePredicate> predicates = new ArrayList<>();

  @Valid private List<RouteFilter> filters = new ArrayList<>();

  private Map<String, String> metadata = new HashMap<>();
}
