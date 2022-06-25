package com.lzhpo.panda.gateway.servlet.predicate;

import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lzhpo
 */
public interface PredicateInvoker<T> extends PredicateFactory<T> {

  Predicate<HttpServletRequest> invoke(T config);
}
