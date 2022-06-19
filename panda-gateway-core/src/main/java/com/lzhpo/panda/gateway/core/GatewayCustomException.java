package com.lzhpo.panda.gateway.core;

import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@NoArgsConstructor
public class GatewayCustomException extends RuntimeException {

  public GatewayCustomException(String message) {
    super(message);
  }

  public GatewayCustomException(String message, Throwable cause) {
    super(message, cause);
  }

  public GatewayCustomException(Throwable cause) {
    super(cause);
  }
}
