## How it works?

```java
// TODO
```

## Route predicate

### internal route predicate

#### AfterRoutePredicateFactory

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

#### BeforeRoutePredicateFactory

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

#### BetweenRoutePredicateFactory

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

#### ClientIpRoutePredicateFactory

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

#### CookieRoutePredicateFactory

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

#### HeaderRoutePredicateFactory

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

#### MethodRoutePredicateFactory

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

#### ParameterRoutePredicateFactory

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

#### PathRoutePredicateFactory

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

#### WeightRoutePredicateFactory

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

### How to implement route predicate?

```java
// TODO
```

## Route filter

### internal route filter

#### AddRequestHeaderRouteFilterFactory

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

#### AddRequestParameterRouteFilterFactory

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

#### AddResponseHeaderRouteFilterFactory

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

#### RemoveRequestHeaderRouteFilterFactory

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

#### RemoveRequestParameterRouteFilterFactory

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

#### RemoveResponseHeaderRouteFilterFactory

```yaml
gateway:
  routes:
    - id: panda-service-sample-01
      uri: lb://panda-service-sample
      filters:
        - name: RemoveResponseHeader
          args:
            headers:
              name: Jack
              age: 20
```

### How to implement route filter?

```java
// TODO
```

## Global filter

```java
// TODO
```

## Actuator api

```java
// TODO
```









