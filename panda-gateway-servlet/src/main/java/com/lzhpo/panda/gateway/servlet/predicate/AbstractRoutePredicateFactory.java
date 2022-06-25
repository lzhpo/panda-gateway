package com.lzhpo.panda.gateway.servlet.predicate;

import cn.hutool.core.util.ReflectUtil;
import com.lzhpo.panda.gateway.core.GatewayCustomException;
import com.lzhpo.panda.gateway.core.PredicateDefinition;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.servlet.enums.ConfigTypeEnum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;

/**
 * @author lzhpo
 */
public abstract class AbstractRoutePredicateFactory<T> implements RoutePredicateFactory<T> {

  protected final Class<T> configClass;

  protected AbstractRoutePredicateFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  protected T newConfig() {
    return BeanUtils.instantiateClass(configClass);
  }

  @Override
  public T getConfig(RouteDefinition route) {
    T config = newConfig();
    List<String> fieldNames = configFieldNames();
    String currentPredicateName = getName();
    List<PredicateDefinition> predicates = route.getPredicates();
    String fieldName = fieldNames.get(0);

    ConfigTypeEnum configType = configTypeEnum();
    switch (configType) {
      case STRING:
        predicates.stream()
            .filter(predicate -> predicate.getName().equalsIgnoreCase(currentPredicateName))
            .findAny()
            .ifPresentOrElse(
                predicate -> {
                  Map<String, String> args = predicate.getArgs();
                  Collection<String> argValues = args.values();
                  for (String value : argValues) {
                    ReflectUtil.setFieldValue(config, fieldName, value);
                    break;
                  }
                },
                () -> {
                  throw new GatewayCustomException("Not found config.");
                });
        break;
      case LIST:
        predicates.stream()
            .filter(predicate -> predicate.getName().equalsIgnoreCase(currentPredicateName))
            .findAny()
            .ifPresentOrElse(
                predicate -> {
                  Map<String, String> args = predicate.getArgs();
                  Collection<String> argValues = args.values();
                  List<String> values = new ArrayList<>(argValues);
                  ReflectUtil.setFieldValue(config, fieldName, values);
                },
                () -> {
                  throw new GatewayCustomException("Not found config.");
                });
        break;
      case MAP:
      default:
        throw new UnsupportedOperationException("Not support type " + configType.name());
    }

    return config;
  }
}
