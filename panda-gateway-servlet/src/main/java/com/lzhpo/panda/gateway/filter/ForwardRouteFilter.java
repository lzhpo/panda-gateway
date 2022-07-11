package com.lzhpo.panda.gateway.filter;

import cn.hutool.extra.servlet.ServletUtil;
import com.lzhpo.panda.gateway.core.GatewayProperties;
import com.lzhpo.panda.gateway.core.GatewayProperties.HttpClientConfig;
import com.lzhpo.panda.gateway.core.route.RouteMetadataConst;
import com.lzhpo.panda.gateway.core.utils.ExtractUtil;
import com.lzhpo.panda.gateway.route.Route;
import com.lzhpo.panda.gateway.support.CacheRequestWrapper;
import java.io.IOException;
import java.time.Duration;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * @author lzhpo
 */
@RequiredArgsConstructor
public class ForwardRouteFilter implements RouteFilter, Ordered {

  private final Route route;
  private final RestTemplate restTemplate;
  private final GatewayProperties gatewayProperties;

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  @Override
  @SneakyThrows
  public void doFilter(
      HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain) {

    String method = request.getMethod();
    HttpMethod httpMethod = HttpMethod.resolve(method);
    Assert.notNull(httpMethod, "Bad request");
    MultiValueMap<String, String> headers = buildHeaders(request);

    Map<String, String> metadata = route.getMetadata();
    HttpClientConfig httpClient = gatewayProperties.getHttpClient();
    Duration connectTimeout = getConnectTimeout(metadata, httpClient);
    Duration responseTimeout = getResponseTimeout(metadata, httpClient);

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Math.toIntExact(connectTimeout.toMillis()));
    requestFactory.setReadTimeout(Math.toIntExact(responseTimeout.toMillis()));
    restTemplate.setRequestFactory(requestFactory);

    String finallyRequestPath = request.getRequestURI();
    finallyRequestPath = buildPathWithParams(request, finallyRequestPath);
    HttpEntity<?> httpEntity = buildHttpEntity(request, httpMethod, headers);
    ResponseEntity<byte[]> responseEntity =
        restTemplate.exchange(
            route.getUri() + finallyRequestPath, httpMethod, httpEntity, byte[].class);

    byte[] responseBody = responseEntity.getBody();
    if (Objects.nonNull(responseBody)) {
      response.setStatus(responseEntity.getStatusCodeValue());

      ServletOutputStream outputStream = response.getOutputStream();
      outputStream.write(responseBody);
      // In order to make response.isCommitted() is true
      outputStream.flush();
      outputStream.close();
    }
  }

  /**
   * Build http entity.
   *
   * @param request {@link HttpServletRequest}
   * @param httpMethod {@link HttpMethod}
   * @param headers request headers
   * @return http entity
   * @throws IOException if operate inputStream throw exception
   */
  private HttpEntity<?> buildHttpEntity(
      HttpServletRequest request, HttpMethod httpMethod, MultiValueMap<String, String> headers)
      throws IOException {

    final HttpEntity<?> httpEntity;
    if (ExtractUtil.requireBody(httpMethod)) {
      CacheRequestWrapper cachedRequest = new CacheRequestWrapper(request);

      if (ServletUtil.isMultipart(request)) {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest multipart = multipartResolver.resolveMultipart(cachedRequest);
        Map<String, MultipartFile> fileMap = multipart.getFileMap();

        MultiValueMap<String, Object> multiForm = new LinkedMultiValueMap<>();
        fileMap.forEach((name, multipartFile) -> multiForm.add(name, multipartFile.getResource()));
        httpEntity = new HttpEntity<>(multiForm, headers);
      } else {
        ServletInputStream inputStream = cachedRequest.getInputStream();
        byte[] bodyBytes = IOUtils.toByteArray(inputStream);
        httpEntity = new HttpEntity<>(bodyBytes, headers);
      }

    } else {
      httpEntity = new HttpEntity<>(null, headers);
    }

    return httpEntity;
  }

  /**
   * Append request params to request path.
   *
   * @param request {@link ServletRequest}
   * @param fullPath full-request path
   * @return request path after appended request params
   */
  private String buildPathWithParams(ServletRequest request, String fullPath) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    if (!ObjectUtils.isEmpty(parameterMap)) {
      Map<String, String> queryParams = new HashMap<>(parameterMap.size());
      parameterMap.forEach((paramName, paramValues) -> queryParams.put(paramName, paramValues[0]));
      String queryParamsInPath = ExtractUtil.mapToParamPath(queryParams);
      fullPath += "?" + queryParamsInPath;
    }
    return fullPath;
  }

  /**
   * Build request headers.
   *
   * @param request {@link HttpServletRequest}
   * @return request headers
   */
  private MultiValueMap<String, String> buildHeaders(HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.add(headerName, request.getHeader(headerName));
    }
    return headers;
  }

  /**
   * Get connect timeout.
   *
   * <p>priority: route metadata configuration > gateway properties httpClient configuration
   *
   * @param metadata route metadata. unit: milliseconds
   * @param httpClient gateway properties httpClient configuration
   * @return connect timeout
   */
  private Duration getConnectTimeout(Map<String, String> metadata, HttpClientConfig httpClient) {
    return Optional.ofNullable(metadata.get(RouteMetadataConst.CONNECT_TIMEOUT))
        .filter(StringUtils::hasText)
        .map(connectTimeoutMillis -> Duration.ofMillis(Long.parseLong(connectTimeoutMillis)))
        .orElseGet(httpClient::getConnectTimeout);
  }

  /**
   * Get response timeout.
   *
   * <p>priority: route metadata configuration > gateway properties httpClient configuration
   *
   * @param metadata route metadata. unit: milliseconds
   * @param httpClient gateway properties httpClient configuration
   * @return response timeout
   */
  private Duration getResponseTimeout(Map<String, String> metadata, HttpClientConfig httpClient) {
    return Optional.ofNullable(metadata.get(RouteMetadataConst.RESPONSE_TIMEOUT))
        .filter(StringUtils::hasText)
        .map(responseTimeoutMillis -> Duration.ofMillis(Long.parseLong(responseTimeoutMillis)))
        .orElseGet(httpClient::getResponseTimeout);
  }
}
