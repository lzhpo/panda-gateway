package com.lzhpo.panda.gateway.core.route;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use config constructor to parse {@link ComponentDefinition} args.
 *
 * @author lzhpo
 */
@Documented
@Target({ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentConstructorArgs {}
