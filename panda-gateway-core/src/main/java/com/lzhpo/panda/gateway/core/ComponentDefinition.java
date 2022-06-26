package com.lzhpo.panda.gateway.core;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class ComponentDefinition {

  private String name;

  private Map<String, String> args = new HashMap<>();
}
