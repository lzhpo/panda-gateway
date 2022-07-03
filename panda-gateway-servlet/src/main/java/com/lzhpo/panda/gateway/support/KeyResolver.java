package com.lzhpo.panda.gateway.support;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzhpo
 */
public interface KeyResolver {

  /**
   * Resolve key
   *
   * @param request {@link HttpServletRequest}
   * @return key
   */
  String resolve(HttpServletRequest request);
}
