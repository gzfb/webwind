package org.expressme.webwind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to sort interceptors.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptorOrder {

    /**
     * Lower value has more priority.
     */
    int value();

}
