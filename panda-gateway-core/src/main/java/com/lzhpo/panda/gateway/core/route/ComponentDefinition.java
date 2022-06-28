package com.lzhpo.panda.gateway.core.route;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * @author lzhpo
 */
@Data
@Validated
public class ComponentDefinition {

  @NotBlank private String name;

  @NotEmpty private Map<String, Object> args = new HashMap<>();
}
