package org.librarymanagementsystem.annotation;


import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})  // Works on both methods and classes
//@Target({ ElementType.TYPE })
@Documented
public @interface Auditable {
    String name() default "" ;
}
