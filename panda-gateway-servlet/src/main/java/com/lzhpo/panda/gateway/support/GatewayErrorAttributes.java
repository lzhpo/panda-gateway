package com.lzhpo.panda.gateway.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Customize error response data.
 *
 * @author lzhpo
 */
public class GatewayErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      WebRequest webRequest, ErrorAttributeOptions options) {
    Map<String, Object> errors = super.getErrorAttributes(webRequest, options);
    Map<String, Object> errorAttributes = new HashMap<>(4);
    errorAttributes.put("success", false);
    errorAttributes.put("code", errors.getOrDefault("status", 500));
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
