package com.eyeem.decorator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by budius on 26.11.15.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Decorate {
   String value() default "";

   String decorator() default "";

   String decoratored() default "";

   String decorators() default "";
}

