![style check](https://github.com/lzhpo/panda-gateway/actions/workflows/style-check.yml/badge.svg)

English | [中文](README-CN.md)

## What's it?

### Foreword

- This project aims to handwrite SpringCloud Gateway.
- Referring to the core idea of SpringCloud Gateway, I basically implemented all its functions in my own way.
- You can use it to understand the internal working principle of SpringCloud Gateway and secondary development more quickly.

### Features

1. Powerful predicates and filters, easier to understand and expand.
2. Route information storage can be dynamically switched to memory or redis.
3. Supports servlet and webflux environments, as well as microservice mode and http/https mode.

## How it works?

```java
// TODO
```

## Route predicate

### Exist route predicate

#### 1.`Path` route predicate

> e.g: If I want make request path is `/api/service-sample/**` or `/api/sample/**` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: Path
          args:
            paths: /api/service-sample/**, /api/sample/**
```

#### 2.`Weight` route predicate

> e.g: If I want to assign weight for route.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      order: 1
      predicates:
        - name: Weight
          args:
            group: service-sample
            weight: 8
    - id: panda-service-sample-02
      uri: lb://panda-service-sample
      order: 2
      predicates:
        - name: Weight
          args:
            group: service-sample
            weight: 2
```

#### 3.`Parameter` route predicate

> e.g: If I want make request parameter have `nickName=Lewis` or `age=22` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: Parameter
          args:
            parameters:
              nickName: Lewis
              age: 20
```

**Notes**: The value support regex expression.

#### 4.`ClientIp` route predicate

> e.g: If I want make request client ip is `192.168.200.111` or `192.168.200.112` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: ClientIp
          args:
            clientIps: 192.168.200.111, 192.168.200.112
```

#### 5.`Cookie` route predicate

> e.g: If I want make request cookie is `deviceId=123456` or `age=22` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: Cookie
          args:
            cookies:
              deviceId: 123456
              age: 22
```

**Notes**: The value support regex expression.

#### 6.`Header` route predicate

> e.g: If I want make request cookie is `X-B3-TraceId=123456` or `X-B3-SpanId=123456` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: Header
          args:
            headers:
              X-B3-TraceId: 123456
              X-B3-SpanId: 123456
```

**Notes**: The value support regex expression.

#### 7.`Method` route predicate

> e.g: If I want make request method is `PUT` or `PATCH` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: Method
          args:
            methods: PUT, PATCH
```

#### 8.`After` route predicate

> e.g: If I want make request time is after `2030-06-30T01:29:48.0875598+08:00[Asia/Shanghai]` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: After
          args:
            time: 2030-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
```

**Notes**: The value type is `ZonedDateTime`.

You can easy to get the format value:

```java
ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
```

#### 9.`Before` route predicate

> e.g: If I want make request time is before `2015-06-30T01:29:48.0875598+08:00[Asia/Shanghai]` forward to `lb://panda-service-sample`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: Before
          args:
            time: 2015-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
```

**Notes**: The value type is `ZonedDateTime`.

You can easy to get the format value:

```java
ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
```

#### 10.`Between` route predicate

> e.g: If you want make the request time is 
>
> ```java
> start time: 2012-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
> end time: 2018-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
> ```
>
> or
>
> ```java
> start time: 2020-10-01T01:29:48.0875598+08:00[Asia/Shanghai]
> end time: 2030-10-01T01:29:48.0875598+08:00[Asia/Shanghai]
> ```
>
> forward to `lb://panda-service-sample`。

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: Between
          args:
            times:
              - start: 2012-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
                end: 2018-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
              - start: 2020-10-01T01:29:48.0875598+08:00[Asia/Shanghai]
                end: 2030-10-01T01:29:48.0875598+08:00[Asia/Shanghai]
```

**Notes**: 

1. Support many time pair.

2. The end value type is `ZonedDateTime`.

   You can easy to get the format value:

   ```java
   ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
   ```

### Custom route predicate relation

> You can define the relation of routing predicates. By setting `gateway.routes[x].metadata.predicate-relation`, it can be set to `AND` (match all predicates) or `OR` (match any predicate), case insensitive, the default is `AND` (match all predicates).

#### `AND`

> This route is used when the request header have `X-B3-TraceId=123456` **and** the request parameter have `nickName=Lewis`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      order: 1
      metadata:
        predicate-relation: and
      predicates:
        - name: Header
          args:
            headers:
              X-B3-TraceId: 123456
        - name: Parameter
          args:
            parameters:
              nickName: Lewis 
```

#### `OR`

> This route is used when the request header have `X-B3-TraceId=123456` **or** the request parameter have `nickName=Lewis`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      order: 1
      metadata:
        predicate-relation: or
      predicates:
        - name: Header
          args:
            headers:
              X-B3-TraceId: 123456
        - name: Parameter
          args:
            parameters:
              nickName: Lewis 
```

### How to implement route predicate?

> Format: `[PredicateName]`RoutePredicateFactory

I will use `After` route predicate as example to tell you how to implement it.

#### Servlet environment

```java
@Component
public class AfterRoutePredicateFactory
    extends AbstractRoutePredicateFactory<AfterRoutePredicateFactory.Config> {

  public AfterRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      ZonedDateTime nowTime = ZonedDateTime.now();
      ZonedDateTime afterTime = config.getTime();
      return nowTime.isAfter(afterTime);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotNull 
    private ZonedDateTime time;
  }
}
```

#### Webflux environment

```java
@Component
public class AfterRoutePredicateFactory
    extends AbstractRoutePredicateFactory<AfterRoutePredicateFactory.Config> {

  public AfterRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public RoutePredicate create(Config config) {
    return request -> {
      ZonedDateTime nowTime = ZonedDateTime.now();
      ZonedDateTime afterTime = config.getTime();
      return nowTime.isAfter(afterTime);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotNull 
    private ZonedDateTime time;
  }
}
```

#### Use the `After` route predicate

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      predicates:
        - name: After
          args:
            time: 2030-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
```

## Route filter

### Exist route filter

#### 1.`AddRequestHeader` route filter

> e.g: If you want add request headers.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: AddRequestHeader
          args:
            headers:
              name: Lewis
              age: 123
```

#### 2.`AddRequestParameter` route filter

> e.g: If you want add request parameters.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: AddRequestParameter
          args:
            parameters:
              userId: 123
```

#### 3.`AddResponseHeader` route filter

> e.g: If you want add response headers.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: AddResponseHeader
          args:
            headers:
              name: Jack
              age: 20
```

#### 4.`RemoveRequestHeader` route filter

> e.g: If you want remove request headers.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: RemoveRequestHeader
          args:
            headers:
              X-B3-TraceId: 123
              X-B3-SpanId: 456
```

**Notes**: the value support regex expression.

#### 5.`RemoveRequestParameter` route filter

> e.g: If you want remove request parameters.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: RemoveRequestParameter
          args:
            parameters:
              traceId: 123
              spanId: 456
```

**Notes**: the value support regex expression.

#### 6.`RemoveResponseHeader` route filter

> e.g: If you want remove response headers.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: RemoveResponseHeader
          args:
            headers:
              country: China
              city: Guangzhou
```

**Notes**: the value support regex expression.

#### 7.`RateLimiter` route filter

> e.g: If you want limit the current of the request.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: RateLimiter
          args:
            includeHeaders: true
            replenishRate: 1
            burstCapacity: 1
            requestedTokens: 1
            limitedCode: 429
            limitedMessage: "Request too frequent"
            keyResolver: "#{@clientIpKeyResolver}"
            rateLimiter: "#{@redisRateLimiter}"
```

### How to implement route filter?

> Format: `[FilterName]`RouteFilterFactory

I will use `AddResponseHeader` route filter to tell you how to implement it.

#### Servlet environment

```java
@Component
public class AddResponseHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<AddResponseHeaderRouteFilterFactory.Config>
    implements Ordered {

  public AddResponseHeaderRouteFilterFactory() {
    super(AddResponseHeaderRouteFilterFactory.Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (request, response, chain) -> {
      Map<String, String> configHeaders = config.getHeaders();
      configHeaders.forEach(response::addHeader);
      chain.doFilter(request, response);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty 
    private Map<String, String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
```

#### Webflux environment

```java
@Component
public class AddResponseHeaderRouteFilterFactory
    extends AbstractRouteFilterFactory<AddResponseHeaderRouteFilterFactory.Config>
    implements Ordered {

  public AddResponseHeaderRouteFilterFactory() {
    super(Config.class);
  }

  @Override
  public RouteFilter create(Config config) {
    return (exchange, filterChain) -> {
      Map<String, String> configHeaders = config.getHeaders();
      ServerHttpResponse response = exchange.getResponse();
      HttpHeaders respHeaders = response.getHeaders();
      configHeaders.forEach(respHeaders::remove);
      return filterChain.filter(exchange);
    };
  }

  @Data
  @Validated
  public static class Config {

    @NotEmpty 
    private Map<String, String> headers;
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
```

#### Use the `AddResponseHeader` route filter

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: AddResponseHeader
          args:
            headers:
              name: Jack
              age: 20
```

## Global filter

> The global filter will apply all routes, and the global filter not have anything name constraint.

### How to implement global filter?

#### Servlet environment

```java
@Component
public class ResponseGlobalFilter implements GlobalFilter {

  @Override
  public void filter(
      HttpServletRequest request, HttpServletResponse response, RouteFilterChain chain) {
    response.addHeader("country", "China");
    response.addHeader("city", "Guangzhou");
    chain.doFilter(request, response);
  }
}
```

#### Webflux environment

```java
@Component
public class ResponseGlobalFilter implements GlobalFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, DefaultRouteFilterChain filterChain) {
    ServerHttpResponse response = exchange.getResponse();
    HttpHeaders headers = response.getHeaders();
    headers.add("country", "China");
    headers.add("city", "Guangzhou");
    return filterChain.filter(exchange);
  }
}
```

## Unified custom exception response format

### Servlet environment

#### method1 - extends `DefaultErrorAttributes`

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Customize error response data.
 *
 * @see ErrorMvcAutoConfiguration#errorAttributes()
 * @author lzhpo
 */
@Component
public class GatewayErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      WebRequest webRequest, ErrorAttributeOptions options) {
    Map<String, Object> errors = super.getErrorAttributes(webRequest, options);
    Map<String, Object> errorAttributes = new HashMap<>(4);
    errorAttributes.put("success", false);
    errorAttributes.put("code", errors.getOrDefault("status", 500));
    errorAttributes.put("message", getErrorMessage(errors));
    errorAttributes.put("data", null);
    return errorAttributes;
  }

  /**
   * Get an error message.
   *
   * @param errors error attributes
   * @return error message
   */
  private Object getErrorMessage(Map<String, Object> errors) {
    return Optional.ofNullable(errors.get("message"))
        .orElseGet(() -> errors.getOrDefault("error", "Internal Server Error"));
  }
}
```

return format (example): 

```json
{
    "code": 504,
    "message": "Gateway Timeout",
    "data": null,
    "success": false
}
```

### Webflux environment

#### method1 - extends `DefaultErrorWebExceptionHandler`

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

/**
 * Customize error response.
 *
 * @see DefaultErrorAttributes
 * @see ErrorWebFluxAutoConfiguration#errorWebExceptionHandler
 * @author lzhpo
 */
@Order(-2)
@Component
public class GatewayErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

  public GatewayErrorWebExceptionHandler(
      WebProperties webProperties,
      ErrorAttributes errorAttributes,
      ServerProperties serverProperties,
      ApplicationContext applicationContext,
      ObjectProvider<ViewResolver> viewResolvers,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(
        errorAttributes,
        webProperties.getResources(),
        serverProperties.getError(),
        applicationContext);
    setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
    setMessageWriters(serverCodecConfigurer.getWriters());
    setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  public Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Map<String, Object> errors =
        getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
    int status = (int) errors.getOrDefault("status", 500);

    Map<String, Object> errorAttributes = new HashMap<>(4);
    errorAttributes.put("success", false);
    errorAttributes.put("code", status);
    errorAttributes.put("message", getErrorMessage(errors));
    errorAttributes.put("data", null);

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(errorAttributes));
  }

  /**
   * Get an error message.
   *
   * @param errors error attributes
   * @return error message
   */
  private Object getErrorMessage(Map<String, Object> errors) {
    return Optional.ofNullable(errors.get("message"))
        .orElseGet(() -> errors.getOrDefault("error", "Internal Server Error"));
  }
}
```

return format (example): 

```json
{
    "code": 504,
    "message": "Gateway Timeout",
    "data": null,
    "success": false
}
```

#### method2 - extends `DefaultErrorAttributes`

Using this method, the `errorAttributes` returned by rewriting in the webflux environment needs to have `status`, otherwise, will throw NullPointerException, which does not happen in the servlet environment.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * Customize error response data.
 *
 * @author lzhpo
 */
@Component
public class GatewayErrorAttributes extends DefaultErrorAttributes {

  /**
   * Notes: errorAttributes must containsKey "status", otherwise, will throw NullPointerException
   *
   * <pre>{@code
   * 	protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
   * 		Map<String, Object> error = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
   * 		return ServerResponse.status(getHttpStatus(error)).contentType(MediaType.APPLICATION_JSON)
   * 				.body(BodyInserters.fromValue(error));
   *  }
   *
   * 	protected int getHttpStatus(Map<String, Object> errorAttributes) {
   * 		return (int) errorAttributes.get("status");
   *  }
   * }</pre>
   *
   * @see DefaultErrorWebExceptionHandler#renderErrorResponse
   * @see DefaultErrorWebExceptionHandler#getHttpStatus
   * @param request the source request
   * @param options options for error attribute contents
   * @return error attributes
   */
  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions options) {
    Map<String, Object> errors = super.getErrorAttributes(request, options);
    Map<String, Object> errorAttributes = new HashMap<>(4);
    errorAttributes.put("success", false);
    errorAttributes.put("status", errors.getOrDefault("status", 500));
    errorAttributes.put("message", getErrorMessage(errors));
    errorAttributes.put("data", null);
    return errorAttributes;
  }

  /**
   * Get an error message.
   *
   * @param errors error attributes
   * @return error message
   */
  private Object getErrorMessage(Map<String, Object> errors) {
    return Optional.ofNullable(errors.get("message"))
        .orElseGet(() -> errors.getOrDefault("error", "Internal Server Error"));
  }
}
```

Details can be seen: 

```java
// org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler#renderErrorResponse
protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Map<String, Object> error = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
    return ServerResponse.status(getHttpStatus(error)).contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(error));
}

// org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler#getHttpStatus
protected int getHttpStatus(Map<String, Object> errorAttributes) {
    return (int) errorAttributes.get("status");
}
```

return format (example): 

```json
{
    "status": 504,
    "message": "Gateway Timeout",
    "data": null,
    "success": false
}
```

## Actuator endpoint API

We need exposure `gateway` endpoint if we want to do about gateway something.

```java
management:
  endpoints:
    web:
      exposure:
        include: gateway
```

### 1.Get configuration of all route

```js
GET /actuator/gateway/routes
```

### 2.Get route configuration by routeId

```js
GET /actuator/gateway/routes/${routeId}
```

### 3.Get predicate class name of all route

```js
GET /actuator/gateway/routes/predicates
```

### 4.Get predicate class name by routeId

```js
GET /actuator/gateway/routes/${routeId}/predicates
```

### 5.Get filter class name of all route

```js
GET /actuator/gateway/routes/filters
```

### 6.Get filter class name by routeId

```js
GET /actuator/gateway/routes/${routeId}/filters
```

### 7.Get global filter class name of all route

```js
GET /actuator/gateway/routes/global-filters
```

### 8.Refresh route

```js
POST /actuator/gateway/routes/refresh
```







