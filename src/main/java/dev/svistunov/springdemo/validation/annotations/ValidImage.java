package dev.svistunov.springdemo.validation.annotations;

import dev.svistunov.springdemo.validation.validators.ImageFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, валидирующая загруженное изображение в параметре метода контроллера
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileValidator.class})
public @interface ValidImage {
    String message() default "Недопустимый файл изображения";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}