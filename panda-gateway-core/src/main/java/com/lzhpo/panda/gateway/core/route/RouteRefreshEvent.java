package com.lzhpo.panda.gateway.core.route;

import org.springframework.context.ApplicationEvent;

/**
 * @author lzhpo
 */
public class RouteRefreshEvent extends ApplicationEvent {

  public RouteRefreshEvent(Object source) {
    super(source);
  }
}
