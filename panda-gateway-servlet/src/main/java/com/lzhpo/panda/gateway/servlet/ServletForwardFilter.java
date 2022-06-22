package com.lzhpo.panda.gateway.servlet;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.loadbalancer.RouteLoadBalancer;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class ServletForwardFilter extends OncePerRequestFilter {

  private final RestTemplate restTemplate;
  private final RouteLoadBalancer routeLoadBalancer;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestPath = request.getRequestURI();
    RouteDefinition proxyRoute = routeLoadBalancer.choose(requestPath);
    if (Objects.isNull(proxyRoute) || response.isCommitted()) {
      filterChain.doFilter(request, response);
      return;
    }

    String filteredPath = ExtractUtils.stripPrefix(requestPath, proxyRoute.getStripPrefix());
    String fullPath = proxyRoute.getTargetUrl() + filteredPath;
    String method = request.getMethod();
    HttpMethod httpMethod = HttpMethod.resolve(method);
    Assert.notNull(httpMethod, "Can not resolve http method [" + method + "]");
    MultiValueMap<String, String> headers = filterHeaders(request);

    fullPath = buildPathWithParams(request, fullPath);
    HttpEntity<?> httpEntity = buildHttpEntity(request, httpMethod, headers);

    ResponseEntity<byte[]> responseEntity =
        restTemplate.exchange(fullPath, httpMethod, httpEntity, byte[].class);
    byte[] responseBody = responseEntity.getBody();
    if (Objects.nonNull(responseBody)) {
      headers.toSingleValueMap().forEach(response::setHeader);
      response.setStatus(responseEntity.getStatusCodeValue());
      response.getOutputStream().write(responseBody);
    }
  }

  private HttpEntity<?> buildHttpEntity(
      HttpServletRequest request, HttpMethod httpMethod, MultiValueMap<String, String> headers)
      throws IOException {
    final HttpEntity<?> httpEntity;
    if (ExtractUtils.requireBody(httpMethod)) {
      CachingServletRequestWrapper cachingRequest = new CachingServletRequestWrapper(request);
      ServletInputStream inputStream = cachingRequest.getInputStream();
      byte[] inputStreamBodyBytes = IOUtils.toByteArray(inputStream);
      httpEntity = new HttpEntity<>(inputStreamBodyBytes, headers);
    } else {
      httpEntity = new HttpEntity<>(null, headers);
    }
    return httpEntity;
  }

  private String buildPathWithParams(ServletRequest request, String fullPath) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    if (!ObjectUtils.isEmpty(parameterMap)) {
      Map<String, String> queryParams = new HashMap<>(parameterMap.size());
      parameterMap.forEach((paramName, paramValues) -> queryParams.put(paramName, paramValues[0]));
      String queryParamsInPath = ExtractUtils.mapToParamPath(queryParams);
      fullPath += "?" + queryParamsInPath;
    }
    return fullPath;
  }

  private MultiValueMap<String, String> filterHeaders(HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.add(headerName, request.getHeader(headerName));
    }
    return headers;
  }
}
