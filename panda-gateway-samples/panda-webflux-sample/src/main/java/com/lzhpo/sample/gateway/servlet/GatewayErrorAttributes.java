package com.lzhpo.sample.gateway.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * Customize error response data.
 *
 * @author lzhpo
 */
// @Component
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
