/*
 * Copyright 2022 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.panda.gateway.core.utils;

import java.util.Objects;
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

  public static <T> void validate(T bean, ErrorAction<String> errorAction) {
    Set<ConstraintViolation<T>> violations = getValidator().validate(bean);
    handleValidateResult(violations, errorAction);
  }

  public static <T> void validate(T bean) {
    Set<ConstraintViolation<T>> violations = getValidator().validate(bean);
    handleValidateResult(violations, null);
  }

  public static <T> void validate(T bean, Class<?>... groups) {
    Set<ConstraintViolation<T>> violations = getValidator().validate(bean, groups);
    handleValidateResult(violations, null);
  }

  private static <T> void handleValidateResult(
      Set<ConstraintViolation<T>> violations, ErrorAction<String> errorAction) {
    if (!ObjectUtils.isEmpty(violations)) {
      StringJoiner validateErrors = new StringJoiner(";");
      for (ConstraintViolation<T> violation : violations) {
        String validateError = violation.getPropertyPath().toString() + violation.getMessage();
        validateErrors.add(validateError);
      }

      String errorMsg = validateErrors.toString();
      if (Objects.nonNull(errorAction)) {
        errorMsg = errorAction.accept(errorMsg);
      }
      throw new ValidationException(errorMsg);
    }
  }

  public interface ErrorAction<T> {

    /**
     * Customize validate error message
     *
     * @param errorMsg validate error message
     * @return new validate error message
     */
    String accept(T errorMsg);
  }
}
