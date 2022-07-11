package com.lzhpo.sample.gateway.servlet;

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
