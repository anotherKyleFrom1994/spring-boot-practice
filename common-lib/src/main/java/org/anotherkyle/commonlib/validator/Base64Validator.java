package org.anotherkyle.commonlib.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.codec.binary.Base64;

import java.util.List;

public class Base64Validator {
    public static class BytesValidator implements ConstraintValidator<LegalBase64, byte[]> {
        @Override
        public boolean isValid(byte[] value, ConstraintValidatorContext context) {
            return Base64.isBase64(value);
        }
    }

    public static class StringValidator implements ConstraintValidator<LegalBase64, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Base64.isBase64(value);
        }
    }

    public static class StringListValidator implements ConstraintValidator<LegalBase64, List<String>> {
        @Override
        public boolean isValid(List<String> value, ConstraintValidatorContext context) {
            return value.stream().allMatch(Base64::isBase64);
        }
    }
}
