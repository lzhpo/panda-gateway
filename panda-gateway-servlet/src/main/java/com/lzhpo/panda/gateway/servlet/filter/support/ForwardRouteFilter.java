package com.lzhpo.panda.gateway.servlet.filter.support;

import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.consts.GatewayConst;
import com.lzhpo.panda.gateway.servlet.filter.RouteFilter;
import com.lzhpo.panda.gateway.servlet.filter.chain.RouteFilterChain;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class ForwardRouteFilter implements RouteFilter {

  private final RestTemplate restTemplate;

  @Override
  @SneakyThrows
  public void doFilter(
      HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain) {

    String method = request.getMethod();
    HttpMethod httpMethod = HttpMethod.resolve(method);
    Assert.notNull(httpMethod, "Bad request");
    MultiValueMap<String, String> headers = filterHeaders(request);
    RouteDefinition route = (RouteDefinition) request.getAttribute(GatewayConst.ROUTE_DEFINITION);

    String finallyRequestPath = request.getRequestURI();
    finallyRequestPath = buildPathWithParams(request, finallyRequestPath);
    HttpEntity<?> httpEntity = buildHttpEntity(request, httpMethod, headers);
    ResponseEntity<byte[]> responseEntity =
        restTemplate.exchange(
            route.getUri() + finallyRequestPath, httpMethod, httpEntity, byte[].class);

    byte[] responseBody = responseEntity.getBody();
    if (Objects.nonNull(responseBody)) {
      response.setStatus(responseEntity.getStatusCodeValue());
      response.getOutputStream().write(responseBody);
    }
  }

  private HttpEntity<?> buildHttpEntity(
      HttpServletRequest request, HttpMethod httpMethod, MultiValueMap<String, String> headers)
      throws IOException {
    final HttpEntity<?> httpEntity;
    if (ExtractUtils.requireBody(httpMethod)) {
      CacheRequestWrapper cachingRequest = new CacheRequestWrapper(request);
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
