package com.lzhpo.panda.gateway.predicate;

import java.util.function.Predicate;
import org.springframework.web.server.ServerWebExchange;

/**
 * Just for route predicate.
 *
 * @author lzhpo
 */
public interface RoutePredicate extends Predicate<ServerWebExchange> {}
