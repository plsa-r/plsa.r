package net.plsar.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Meta {
    public String design() default "";
    public String title() default "";
    public String description() default "";
    public String keywords() default "";
}
