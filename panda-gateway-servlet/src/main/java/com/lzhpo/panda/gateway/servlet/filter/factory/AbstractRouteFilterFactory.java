package com.lzhpo.panda.gateway.servlet.filter.factory;

import cn.hutool.core.util.ReflectUtil;
import com.lzhpo.panda.gateway.core.FilterDefinition;
import com.lzhpo.panda.gateway.core.GatewayCustomException;
import com.lzhpo.panda.gateway.core.RouteDefinition;
import com.lzhpo.panda.gateway.core.config.ConfigFactory;
import com.lzhpo.panda.gateway.core.config.ConfigTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
public abstract class AbstractRouteFilterFactory<T>
    implements ConfigFactory<T>, RouteFilterFactory<T> {

  protected final Class<T> configClass;

  public AbstractRouteFilterFactory(Class<T> configClass) {
    this.configClass = configClass;
  }

  @Override
  public T parseToConfig(RouteDefinition route) {
    if (ObjectUtils.isEmpty(route)) {
      return null;
    }

    T config = newConfigInstance(configClass);
    List<String> fieldNames = configFieldOrder();
    String currentPredicateName = currentName();
    List<FilterDefinition> filters = route.getFilters();

    ConfigTypeEnum configType = configFieldType();
    switch (configType) {
      case DEFAULT:
        filters.stream()
            .filter(filter -> filter.getName().equalsIgnoreCase(currentPredicateName))
            .findAny()
            .ifPresentOrElse(
                filter -> {
                  Map<String, String> args = filter.getArgs();
                  args.forEach(
                      (name, arg) -> {
                        String fieldName = fieldNames.get(Integer.parseInt(name));
                        ReflectUtil.setFieldValue(config, fieldName, arg);
                      });
                },
                () -> {
                  throw new GatewayCustomException("Not found config.");
                });
        break;
      case LIST:
        Assert.isTrue(fieldNames.size() == 1, "Config type of LIST should be 1 field order.");
        filters.stream()
            .filter(filter -> filter.getName().equalsIgnoreCase(currentPredicateName))
            .findAny()
            .ifPresentOrElse(
                filter -> {
                  Map<String, String> args = filter.getArgs();
                  List<String> values = new ArrayList<>(args.values());
                  ReflectUtil.setFieldValue(config, fieldNames.get(0), values);
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
