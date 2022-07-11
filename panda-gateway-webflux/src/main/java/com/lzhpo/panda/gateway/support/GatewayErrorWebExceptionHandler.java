package com.lzhpo.panda.gateway.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Customize error response.
 *
 * <p>Also can see: {@link DefaultErrorAttributes}
 *
 * @author lzhpo
 */
public class GatewayErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

  public GatewayErrorWebExceptionHandler(
      ErrorAttributes errorAttributes,
      Resources resources,
      ErrorProperties errorProperties,
      ApplicationContext applicationContext) {
    super(errorAttributes, resources, errorProperties, applicationContext);
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
