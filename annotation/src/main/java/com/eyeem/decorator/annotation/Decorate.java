package com.eyeem.decorator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by budius on 20.07.15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Decorate {
}
