package dev.svistunov.springdemo.validation.validators;

import dev.svistunov.springdemo.util.PhoneNumberUtils;
import dev.svistunov.springdemo.validation.annotations.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(phoneNumber)) {
            return true;
        }

        try {
            PhoneNumberUtils.normalizePhoneNumber(phoneNumber, "RU");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}