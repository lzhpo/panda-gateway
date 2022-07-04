package com.lzhpo.panda.gateway.core.route;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.panda.gateway.core.exception.GatewayCustomException;
import com.lzhpo.panda.gateway.core.utils.ValidateUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * @author lzhpo
 */
public interface ConfigFactory<T> {

  /**
   * Get config class
   *
   * @return config class
   */
  Class<T> getConfigClass();

  /**
   * Get current component name.
   *
   * @return current component name
   */
  String name();

  /**
   * To new config instance.
   *
   * @return config instance
   */
  default T newConfigInstance() {
    Class<T> configClass = getConfigClass();
    return BeanUtils.instantiateClass(configClass);
  }

  /**
   * Use {@code componentDefinition} to create config.
   *
   * @param componentDefinition {@link ComponentDefinition}
   * @return created config
   */
  default T getConfig(ComponentDefinition componentDefinition) {
    String name = getClass().getSimpleName();
    Assert.notNull(
        componentDefinition, String.format("[%s] componentDefinition cannot null.", name));
    Class<T> configClass = getConfigClass();
    Map<String, Object> args = componentDefinition.getArgs();
    T config;

    try {
      Constructor<T> constructor =
          Arrays.stream(ReflectUtil.getConstructors(configClass))
              .filter(x -> AnnotationUtil.hasAnnotation(x, ComponentConstructorArgs.class))
              .findAny()
              .orElse(null);

      if (Objects.nonNull(constructor)) {
        config = constructor.newInstance(args);
      } else {
        CopyOptions copyOptions = CopyOptions.create().setConverter(this::converterTargetValue);
        config = BeanUtil.toBean(args, configClass, copyOptions);
      }
    } catch (Exception e) {
      throw new GatewayCustomException(String.format("[%s] args configuration is wrong.", name), e);
    }

    ValidateUtil.validate(config, errorMsg -> String.format("[%s] %s", name, errorMsg));
    return config;
  }

  /**
   * Converter target value
   *
   * @param targetType {@link Type}
   * @param value value
   * @return target value
   */
  default Object converterTargetValue(Type targetType, Object value) {
    // Whether is spring el expression for the value
    if (Objects.nonNull(value) && value instanceof String) {
      String expressionString = (String) value;
      if (expressionString.startsWith(GatewayConst.EXPRESSION_PREFIX)
          && expressionString.endsWith(GatewayConst.EXPRESSION_SUFFIX)) {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(SpringUtil.getBeanFactory()));
        Expression expression =
            parser.parseExpression(expressionString, new TemplateParserContext());
        return expression.getValue(context);
      }
    }

    // If value is not spring el expression, use default converter
    return Convert.convertWithCheck(targetType, value, null, false);
  }
}
