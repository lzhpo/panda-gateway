package com.lzhpo.panda.gateway.core.route;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lzhpo.panda.gateway.predicate.factory.ZonedDateTimeMethod;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
class ConfigFactoryTest extends ZonedDateTimeMethod {

  @Test
  void getConfigClass() {}

  @Test
  void currentName() {}

  @Test
  void newConfigInstance() {}

  @Test
  void getConfig() {
    Map<String, Object> args = new HashMap<>();
    args.put("start", minusYears(10));
    args.put("end", plusYears(10));

    ComponentDefinition componentDefinition = new ComponentDefinition();
    componentDefinition.setName("Test");
    componentDefinition.setArgs(args);

    TestConfigFactory configFactory = new TestConfigFactory();
    TestConfig config = configFactory.getConfig(componentDefinition);
    assertNotNull(config);
  }

  @Data
  @Validated
  public static class TestConfig {

    @NotNull private ZonedDateTime start;

    @NotNull private ZonedDateTime end;
  }

  public static class TestConfigFactory implements ConfigFactory<TestConfig> {

    @Override
    public Class<TestConfig> getConfigClass() {
      return TestConfig.class;
    }

    @Override
    public String currentName() {
      return "Test";
    }
  }
}
