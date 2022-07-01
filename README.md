## Quick start

`application.yml` example: 
```yaml
gateway:
  discovery: true
  routes:
    - id: panda-service-sample-01
      # uri: http://localhost:9000
      uri: lb://panda-service-sample
      order: 1
      predicates:
        - name: Path
          args:
            patterns: /api/service-sample/**, /api/sample/**
        - name: Cookie
          args:
            cookie: deviceId
            regexp: 123456
        - name: Header
          args:
            header: X-B3-TraceId
            regexp: 123456
        - name: Method
          args:
            methods: PUT, PATCH
        - name: Query
          args:
            param: name
            regexp: Lewis
        - name: ClientIp
          args:
            sources: 192.168.200.111, 192.168.200.112
        - name: Between
          args:
            start: 2012-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
            end: 2018-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
        - name: Before
          args:
            time: 2015-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
        - name: After
          args:
            time: 2030-06-30T01:29:48.0875598+08:00[Asia/Shanghai]
        - name: Weight
          args:
            group: service-sample
            weight: 8
      filters:
        - name: StripPrefix
          args:
            parts: 2
        - name: AddRequestHeader
          args:
            headers:
              name: Lewis
              age: 123
        - name: RemoveRequestHeader
          args:
            headers:
              X-B3-TraceId: 123
              X-B3-SpanId: 456
        - name: AddResponseHeader
          args:
            headers:
              name: Jack
              age: 20
        - name: AddRequestParameter
          args:
            parameters:
              userId: 123
        - name: RemoveRequestParameter
          args:
            parameters:
              traceId: 123
              spanId: 456
    - id: panda-service-sample-02
      # uri: http://localhost:9000
      uri: lb://panda-service-sample
      order: 2
      predicates:
        - name: Weight
          args:
            group: service-sample
            weight: 2
      filters:
        - name: StripPrefix
          args:
            parts: 2

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```