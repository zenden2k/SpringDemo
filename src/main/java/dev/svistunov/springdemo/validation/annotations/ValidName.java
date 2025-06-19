package dev.svistunov.springdemo.validation.annotations;

import dev.svistunov.springdemo.validation.validators.NameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, валидирующая имя человека в поле сущности
 */
@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName {
    String message() default "Допустимы только буквы, цифры, пробелы и дефисы";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}