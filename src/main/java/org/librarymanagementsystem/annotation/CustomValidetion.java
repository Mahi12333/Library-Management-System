package org.librarymanagementsystem.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.librarymanagementsystem.exception.validation.GenericDTOValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenericDTOValidator.class)
public @interface CustomValidetion {
    String message() default "Please fill all details";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
