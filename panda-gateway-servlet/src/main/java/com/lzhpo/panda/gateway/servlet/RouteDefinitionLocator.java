package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.RouteDefinition;
import java.util.List;

/**
 * @author lzhpo
 */
public interface RouteDefinitionLocator {

  List<RouteDefinition> getRoutes();
}
