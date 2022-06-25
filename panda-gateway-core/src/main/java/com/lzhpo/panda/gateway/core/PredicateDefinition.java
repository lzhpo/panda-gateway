package com.lzhpo.panda.gateway.core;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class PredicateDefinition {

  private String name;
  private Map<String, String> args = new LinkedHashMap<>();

  public PredicateDefinition(String text) {
    int eqIdx = text.indexOf('=');
    if (eqIdx <= 0) {
      setName(text);
      return;
    }

    setName(text.substring(0, eqIdx));
    String[] args = tokenizeToStringArray(text.substring(eqIdx + 1), ",");
    for (int i = 0; i < args.length; i++) {
      this.args.put(String.valueOf(i), args[i]);
    }
  }
}
