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

> e.g: If I want make request path `/api/service-sample/**` and `/api/sample/**` forward to `lb://panda-service-sample`.

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

## Actuator api

```java
// TODO
```









