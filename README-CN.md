![style check](https://github.com/lzhpo/panda-gateway/actions/workflows/style-check.yml/badge.svg)

中文 | [English](README.md)

## 它是什么？

### 前言

- 本项目旨在手写 SpringCloud Gateway。
- 参考 SpringCloud Gateway 的核心思想，我基本上用自己的方式实现了它所有的功能。
- 可以使用它更快的了解 SpringCloud Gateway 的内部工作原理和二次开发。

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

#### 1.`Path` 路由谓词

> 例如：如果我想将请求路径是`/api/service-sample/**`或`/api/sample/**`转发到`lb://panda-service-sample`。

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

#### 2.`Weight` 路由谓词

> 例如：我想给路由分配权重。

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

#### 3.`Parameter` 路由谓词

> 例如：如果我想将请求参数含有`nickName=Lewis`或`age=22`转发到`lb://panda-service-sample`。

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

**注意**: 值支持正则表达式。

#### 4.`ClientIp` 路由谓词

> 例如：如果我想请求客户端IP为`192.168.200.111`或`192.168.200.112`转发到`lb://panda-service-sample`。

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

#### 5.`Cookie` 路由谓词

> 例如：如果我想让请求cookie含有`deviceId=123456`或`age=22`转发到`lb://panda-service-sample`。

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

**注意**: 值支持正则表达式。

#### 6.`Header` 路由谓词

> 例如：如果我想让请求cookie含有`X-B3-TraceId=123456`或`X-B3-SpanId=123456`转发到`lb://panda-service-sample`。

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

**注意**: 值支持正则表达式。

#### 7.`Method` 路由谓词

> 例如：如果我想让请求方法是`PUT`或`PATCH`转发到`lb://panda-service-sample`。

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

#### 8.`After` 路由谓词

> 例如：如果我让请求时间是在`2030-06-30T01:29:48.0875598+08:00[Asia/Shanghai]`之后的转发到`lb://panda-service-sample`。

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

**注意**: 值的类型是 `ZonedDateTime`.

你可以很轻松的获取值的格式：

```java
ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
```

#### 9.`Before` 路由谓词

> 例如：如果我想让请求时间在`2015-06-30T01:29:48.0875598+08:00[Asia/Shanghai]`之前的转发到`lb://panda-service-sample`。

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

**注意**: 值的类型是 `ZonedDateTime`.

你可以很轻松的获取值的格式：

```java
ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
```

#### 10.`Between` 路由谓词

> 例如：我想让请求时间在
>
> ```java
> start time: 2012-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
> end time: 2018-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
> ```
>
> 或者
>
> ```java
> start time: 2020-10-01T01:29:48.0875598+08:00[Asia/Shanghai]
> end time: 2030-10-01T01:29:48.0875598+08:00[Asia/Shanghai]
> ```
>
> 转发到 `lb://panda-service-sample`。

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

**注意**:

1. 支持多个时间对。

2. 值的类型是`ZonedDateTime`.

   你可以很轻松的获取值的格式：

   ```java
   ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
   ```

### 自定义路由谓词关系

> 您可以自定义定义路由谓词的关系，通过设置`gateway.routes[x].metadata.predicate.relation`，可以设置为`AND`（匹配所有谓词）或 `OR`（匹配任意一个谓词），不区分大小写，默认是`AND`（匹配所有谓词）。

#### `AND`

> 当请求头中的 X-B3-TraceId为123456，**并且**请求参数中的nickName为Lewis时使用此路由。

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

> 当请求头中的 X-B3-TraceId为123456，**或者**请求参数中的nickName为Lewis时使用此路由。

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

> 格式：`[PredicateName]`RoutePredicateFactory

我将以`After`路由谓词为例来告诉你如何实现它。

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

#### 1.`AddRequestHeader` 路由过滤器

> 例如：如果您想添加请求标头。

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

#### 2.`AddRequestParameter` 路由过滤器

> eg：如果你想添加请求参数。

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

#### 3.`AddResponseHeader` 路由过滤器

> 例如：如果你想添加响应头。

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

#### 4.`RemoveRequestHeader` 路由过滤器

> 例如：如果您想删除请求标头。

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

#### 5.`RemoveRequestParameter` 路由过滤器

> eg：如果你想删除请求参数。

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

#### 6.`RemoveResponseHeader` 路由过滤器

> 例如：如果你想删除响应头。

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

#### 7.`RateLimiter` 路由过滤器

> eg：如果你想限制请求的速率。

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

> 格式：`[FilterName]`RouteFilterFactory

我将使用`AddResponseHeader`路由过滤器来告诉你如何实现它。

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

> 全局过滤器将应用所有路由，并且全局过滤器没有任何名称约束。

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

## Actuator端点API

如果我们想对网关做一些事情，我们需要暴露`gateway`端点。

```java
management:
  endpoints:
    web:
      exposure:
        include: gateway
```

### 1.获取所有路由配置

```js
GET /actuator/gateway/routes
```

### 2.根据路由ID获取路由配置

```js
GET /actuator/gateway/routes/${routeId}
```

### 3.获取所有路由谓词类名

```js
GET /actuator/gateway/routes/predicates
```

### 4.根据路由ID获取路由谓词类名

```js
GET /actuator/gateway/routes/${routeId}/predicates
```

### 5.获取所有路由过滤器类名

```js
GET /actuator/gateway/routes/filters
```

### 6.根据路由ID获取路由过滤器类名

```js
GET /actuator/gateway/routes/${routeId}/filters
```

### 7.获取所有全局过滤器类名

```js
GET /actuator/gateway/routes/global-filters
```

### 8.刷新路由

```js
POST /actuator/gateway/routes/refresh
```











