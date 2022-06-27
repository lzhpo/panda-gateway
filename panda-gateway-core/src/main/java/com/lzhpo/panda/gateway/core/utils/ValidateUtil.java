package com.lzhpo.panda.gateway.core.utils;

import java.util.Set;
import java.util.StringJoiner;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

/**
 * @author lzhpo
 */
@UtilityClass
public class ValidateUtil {

  private static final Validator VALIDATOR;

  static {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      VALIDATOR = validatorFactory.getValidator();
    }
  }

  public static Validator getValidator() {
    return VALIDATOR;
  }

  public static <T> void validate(T bean) {
    Set<ConstraintViolation<T>> violations = getValidator().validate(bean);
    handleValidateResult(violations);
  }

  public static <T> void validate(T bean, Class<?>... groups) {
    Set<ConstraintViolation<T>> violations = getValidator().validate(bean, groups);
    handleValidateResult(violations);
  }

  private static <T> void handleValidateResult(Set<ConstraintViolation<T>> violations) {
    if (!ObjectUtils.isEmpty(violations)) {
      StringJoiner validateErrors = new StringJoiner(";");
      for (ConstraintViolation<T> violation : violations) {
        String validateError = violation.getPropertyPath().toString() + violation.getMessage();
        validateErrors.add(validateError);
      }
      throw new ValidationException(validateErrors.toString());
    }
  }
}
