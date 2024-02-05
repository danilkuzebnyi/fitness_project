package org.danylo.annotation;

import org.springframework.context.annotation.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Scope("cache")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CacheScope {
    /**
     * Define lifetime in seconds. Default 60s
     */
    int lifetime() default 60;
}
