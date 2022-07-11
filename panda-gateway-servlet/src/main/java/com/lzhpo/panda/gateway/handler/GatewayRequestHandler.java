package com.lzhpo.panda.gateway.handler;

import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.filter.DefaultRouteFilterChain;
import com.lzhpo.panda.gateway.filter.ForwardRouteFilter;
import com.lzhpo.panda.gateway.filter.GlobalFilterAdapter;
import com.lzhpo.panda.gateway.filter.RouteFilter;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.route.RouteComponentExtractor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * @author lzhpo
 * @see ResourceHttpRequestHandler
 * @see DefaultServletHttpRequestHandler
 */
@Slf4j
public class GatewayRequestHandler extends DefaultServletHttpRequestHandler {

  private final Route route;
  private final RestTemplate restTemplate;
  private final GatewayProperties gatewayProperties;

  public GatewayRequestHandler(
      ServletContext servletContext,
      Route route,
      RestTemplate restTemplate,
      GatewayProperties gatewayProperties) {
    this.route = route;
    this.restTemplate = restTemplate;
    this.gatewayProperties = gatewayProperties;
    setServletContext(servletContext);
  }

  @Override
  public void handleRequest(
      @NonNull HttpServletRequest request, @NonNull HttpServletResponse response)
      throws ServletException, IOException {

    List<GlobalFilterAdapter> globalFilters = RouteComponentExtractor.getGlobalFilterAdapters();
    List<RouteFilter> filters = new ArrayList<>(globalFilters);

    List<RouteFilter> routeFilters = route.getFilters();
    filters.addAll(routeFilters);
    filters.add(new ForwardRouteFilter(route, restTemplate, gatewayProperties));

    AnnotationAwareOrderComparator.sort(filters);
    DefaultRouteFilterChain.create(filters).doFilter(request, response);

    if (!response.isCommitted()) {
      super.handleRequest(request, response);
    }
  }
}
