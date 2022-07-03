## How it works?

```java
// TODO
```

## Route predicate

### Internal route predicate

#### `Path`RoutePredicateFactory

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

#### `Weight`RoutePredicateFactory

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

#### `Parameter`RoutePredicateFactory

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
```

#### `ClientIp`RoutePredicateFactory

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

#### `Cookie`RoutePredicateFactory

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
```

#### `Header`RoutePredicateFactory

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
```

#### `Method`RoutePredicateFactory

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

#### `After` RoutePredicateFactory

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

#### `Before`RoutePredicateFactory

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

#### `Between`RoutePredicateFactory

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

### Custom predicate relation

#### `AND`

> This route is used when the X-B3-TraceId in the request header is equal to 123456 and the nickName in the request parameter is equal to Lewis.

You can set `gateway.routes[x].enhances.predicates-relation=and`, and the default value also is `and`.

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      order: 1
      enhances:
        predicates-relation: and
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

You can set `gateway.routes[x].enhances.predicates-relation=or`

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      order: 1
      enhances:
        predicates-relation: or
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

### Internal route filter

#### `AddRequestHeader`RouteFilterFactory

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

#### `AddRequestParameter`RouteFilterFactory

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

#### `AddResponseHeader`RouteFilterFactory

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

#### `RemoveRequestHeader`RouteFilterFactory

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

#### `RemoveRequestParameter`RouteFilterFactory

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

#### `RemoveResponseHeader`RouteFilterFactory

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

#### Configure `AddResponseHeader` route filter

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









