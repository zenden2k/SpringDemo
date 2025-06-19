package dev.svistunov.springdemo.validation.annotations;

import dev.svistunov.springdemo.validation.validators.BirthDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, валидирующая дату рождения в поле сущности
 */
@Constraint(validatedBy = BirthDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthDate {
    String message() default "Неправильная дата рождения";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}