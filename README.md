
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
            methods: POST, GET
        - name: Query
          args:
            param: name
            regexp: Lewis
        - name: ClientIp
          args:
            sources: 127.0.0.1, 192.168.200.111
        - name: Between
          args:
            start: 2019-04-29T00:00:00+08:00[Asia/Shanghai]
            end: 2030-05-01T00:00:00+08:00[Asia/Shanghai]
        - name: Before
          args:
            time: 2030-05-01T00:00:00+08:00[Asia/Shanghai]
        - name: After
          args:
            time: 2019-04-29T00:00:00+08:00[Asia/Shanghai]
        - name: Weight
          args:
            group: service-sample
            weight: 8
      filters:
        - name: StripPrefix
          args:
            parts: 2
        - name: AddHeader
          args:
            headers:
              name: Lewis
              age: 123
        - name: RemoveHeader
          args:
            headers: X-B3-TraceId, X-B3-SpanId
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
```