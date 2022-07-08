![style check](https://github.com/lzhpo/panda-gateway/actions/workflows/style-check.yml/badge.svg)

中文 | [English](README.md)

## 它是什么？

### 前言

- 本项目旨在手写SpringCloud Gateway。
- 参考SpringCloud Gateway的核心思想，我基本上都是自己实现了它的所有功能。
- 可以使用它更快的了解SpringCloud Gateway的内部工作原理和二次开发。

### 特点

1. 强大的谓词和过滤器，更容易理解和扩展。
2. 路由信息存储可以动态切换到内存或者redis。
3. 支持servlet和webflux环境，以及微服务模式和http/https模式。

## 它是如何工作的？

```java
// TODO
```

## 路由谓词

### 已存在的谓词

#### 1.`Path` route predicate

> e.g: If I want make request path `/api/service-sample/**` and `/api/sample/**` proxy to `lb://panda-service-sample`.

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

> e.g: If I want make request parameter `nickName=Lewis` and `age=22` proxy to `lb://panda-service-sample`.

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

> e.g: If I want make request client ip is `192.168.200.111` and `192.168.200.112` proxy to `lb://panda-service-sample`.

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

> e.g: If I want make request cookie is `deviceId=123456` and `age=22` proxy to `lb://panda-service-sample`.

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

> e.g: If I want make request cookie is `X-B3-TraceId=123456` and `X-B3-SpanId=123456` proxy to `lb://panda-service-sample`.

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

> e.g: If I want make request method is `PUT` and `PATCH` proxy to `lb://panda-service-sample`.

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

> e.g: If I want make request time is after `2030-06-30T01:29:48.0875598+08:00[Asia/Shanghai]` proxy to `lb://panda-service-sample`.

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

> e.g: If I want make request time is before `2015-06-30T01:29:48.0875598+08:00[Asia/Shanghai]` proxy to `lb://panda-service-sample`.

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

> e.g: If you want make the request time is `(2018-06-30T01:29:48.0875598+08:00[Asia/Shanghai], 2018-06-30T01:29:48.0875598+08:00[Asia/Shanghai])` and `(2020-10-01T01:29:48.0875598+08:00[Asia/Shanghai], 2030-10-01T01:29:48.0875598+08:00[Asia/Shanghai])` proxy to `lb://panda-service-sample`.

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

### 自定义路由谓词关系

> You can custom define the relation for the route predicates, whether **match all**(AND) or **match anyone**(OR).

#### `AND`

> This route is used when the X-B3-TraceId in the request header is equal to 123456 and the nickName in the request parameter is equal to Lewis.

You can set `gateway.routes[x].metadata.predicate.relation=and`, and the default value also is `and`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      order: 1
      metadata:
        predicate.relation: and
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

> This route is used when the `X-B3-TraceId` in the request header is equal to 123456, or the `nickName` in the request parameter is equal to Lewis.

You can set`gateway.routes[x].metadata.predicate.relation=or`

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      order: 1
      metadata:
        predicate.relation: or
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

### 如何实现一个路由谓词？

> Format: `[PredicateName]`RoutePredicateFactory

I will use `After` route predicate as example to tell you how to implement it.

#### Servlet环境

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

#### Webflux环境

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

#### 使用这个`After`路由谓词

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

## 路由过滤器

### 已存在的路由过滤器

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

### 如何实现一个路由过滤器？

> Format: `[FilterName]`RouteFilterFactory

I will use `AddResponseHeader` route filter to tell you how to implement it.

#### Servlet环境

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

#### Webflux环境

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

#### 使用这个`AddResponseHeader`路由过滤器

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

## 全局过滤器

> The global filter will apply all routes, and the global filter not have anything name constraint.

### 如何实现一个全局过滤器？

#### Servlet环境

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

#### Webflux环境

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

## Actuator api

```java
// TODO
```









