package com.lzhpo.panda.gateway.servlet;

import cn.hutool.extra.servlet.ServletUtil;
import com.lzhpo.panda.gateway.core.ExtractUtils;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.GatewayProxyRoute;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class ServletForwardFilter extends GenericFilterBean implements Ordered {

  private final RestTemplate restTemplate;
  private final GatewayProperties gatewayProperties;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    long startMillis = System.currentTimeMillis();
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String path = httpRequest.getRequestURI();

    List<GatewayProxyRoute> proxyRoutes = gatewayProperties.getRoutes();
    Optional<GatewayProxyRoute> proxyRouteOptional =
        proxyRoutes.stream()
            .filter(proxyRoute -> antPathMatcher.match(proxyRoute.getMatchPath(), path))
            .findAny();

    if (proxyRouteOptional.isEmpty()) {
      chain.doFilter(request, response);
      return;
    }

    GatewayProxyRoute proxyRoute = proxyRouteOptional.get();
    String filteredPath = ExtractUtils.stripPrefix(path, proxyRoute.getStripPrefix());
    String fullPath = proxyRoute.getTargetUrl() + filteredPath;
    log.info("Request [{}] match to route {}", path, proxyRoute);

    String method = httpRequest.getMethod();
    HttpMethod httpMethod = HttpMethod.resolve(method);
    Assert.notNull(httpMethod, "Can not resolve http method [" + method + "]");

    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.add(headerName, httpRequest.getHeader(headerName));
    }

    final HttpEntity<String> httpEntity;
    if (ExtractUtils.requireBody(httpMethod)) {
      String requestBody = ServletUtil.getBody(request);
      httpEntity = new HttpEntity<>(requestBody, headers);
    } else {
      httpEntity = new HttpEntity<>(null, headers);
    }

    ResponseEntity<byte[]> resEntity =
        restTemplate.exchange(fullPath, httpMethod, httpEntity, byte[].class);
    byte[] responseBody = resEntity.getBody();
    if (Objects.nonNull(responseBody)) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      headers.toSingleValueMap().forEach(httpResponse::setHeader);
      httpResponse.setStatus(resEntity.getStatusCodeValue());
      httpResponse.getOutputStream().write(responseBody);

      log.info(
          "Request [{}] completed, take time {} millis.",
          fullPath,
          System.currentTimeMillis() - startMillis);
    }
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
