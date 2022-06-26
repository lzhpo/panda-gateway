package com.lzhpo.panda.gateway.predicate;

import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;

/**
 * Just for route predicate.
 *
 * @author lzhpo
 */
public interface RoutePredicate extends Predicate<HttpServletRequest> {}
