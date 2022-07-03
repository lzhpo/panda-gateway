package com.lzhpo.panda.gateway.support;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzhpo
 */
public interface ClientIpResolver {

  /**
   * Resolve client ip from {@code request}
   *
   * @param request {@link HttpServletRequest}
   * @return client ip
   */
  String resolve(HttpServletRequest request);
}
