package dev.svistunov.springdemo.validation.validators;

import dev.svistunov.springdemo.validation.annotations.ValidName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {
    private static final String PATTERN = "^[A-Za-zА-Яа-яЁё0-9\\s\\-]+$";

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return true;
        }

        return name.matches(PATTERN);
    }
}
