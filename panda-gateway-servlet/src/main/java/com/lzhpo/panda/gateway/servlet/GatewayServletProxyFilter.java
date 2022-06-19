package com.lzhpo.panda.gateway.servlet;

import cn.hutool.extra.servlet.ServletUtil;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.GatewayProxyRoute;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class GatewayServletProxyFilter extends GenericFilterBean implements Ordered {

  private final RestTemplate restTemplate;
  private final GatewayProperties gatewayProperties;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    long startMillis = System.currentTimeMillis();

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String requestPath = httpRequest.getRequestURI();

    if (!response.isCommitted()) {
      List<GatewayProxyRoute> proxyRoutes = gatewayProperties.getRoutes();
      Optional<GatewayProxyRoute> proxyRouteOptional =
          proxyRoutes.stream()
              .filter(proxyRoute -> antPathMatcher.match(proxyRoute.getMatchPath(), requestPath))
              .findAny();

      if (proxyRouteOptional.isPresent()) {
        GatewayProxyRoute proxyRoute = proxyRouteOptional.get();
        String stripPrefixedRequestPath = stripPrefix(requestPath, proxyRoute.getStripPrefix());
        String fullRequestPath = proxyRoute.getTargetUrl() + stripPrefixedRequestPath;
        log.info("Request [{}] match to route {}", requestPath, proxyRoute);

        String requestMethod = httpRequest.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(requestMethod);
        Assert.notNull(httpMethod, "Can not resolve http method [" + requestMethod + "]");

        String requestBody = ServletUtil.getBody(request);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody);

        ResponseEntity<String> responseEntity =
            restTemplate.exchange(fullRequestPath, httpMethod, httpEntity, String.class);
        String responseEntityBody = responseEntity.getBody();

        if (Objects.nonNull(responseEntityBody)) {
          httpResponse.setCharacterEncoding("UTF-8");
          httpResponse.setStatus(responseEntity.getStatusCodeValue());
          httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
          httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

          PrintWriter writer = response.getWriter();
          writer.write(responseEntityBody);
          writer.flush();
          writer.close();
          response.flushBuffer();

          log.info(
              "Request [{}] completed, take time {} millis.",
              fullRequestPath,
              System.currentTimeMillis() - startMillis);
          return;
        }
      }
    }

    chain.doFilter(request, response);
  }

  private String stripPrefix(String requestPath, Integer stripPrefix) {
    if (Objects.nonNull(stripPrefix) && stripPrefix > 0) {
      int finallyStripPrefix = stripPrefix;
      List<String> requestPaths =
          Arrays.stream(requestPath.split("/")).collect(Collectors.toList());
      while (finallyStripPrefix > 0) {
        requestPaths.remove(finallyStripPrefix--);
      }
      requestPath = String.join("/", requestPaths);
    }
    return requestPath;
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
