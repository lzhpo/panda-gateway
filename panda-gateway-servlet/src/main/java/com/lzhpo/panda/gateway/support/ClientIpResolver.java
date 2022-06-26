package com.lzhpo.panda.gateway.support;

import cn.hutool.extra.servlet.ServletUtil;
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
  default String resolve(HttpServletRequest request) {
    return ServletUtil.getClientIP(request);
  }
}
