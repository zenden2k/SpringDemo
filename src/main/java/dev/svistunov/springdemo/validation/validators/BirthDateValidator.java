package dev.svistunov.springdemo.validation.validators;

import dev.svistunov.springdemo.validation.annotations.ValidBirthDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true;
        }

        // Дата должна быть в прошлом, минимальная дата - 1900 год
        return birthDate.isBefore(LocalDate.now())
                && birthDate.isAfter(LocalDate.of(1900, 1, 1));
    }
}