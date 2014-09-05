package org.expressme.webwind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation for mapping URL.<br/>
 * For example:<br/>
 * <pre>
 * public class Blog {
 *     &#064;Mapping("/")
 *     public String index() {
 *         // handle index page...
 *     }
 * 
 *     &#064;Mapping("/blog/$1")
 *     public String show(int id) {
 *         // show blog with id...
 *     }
 * 
 *     &#064;Mapping("/blog/edit/$1")
 *     public void edit(int id) {
 *         // edit blog with id...
 *     }
 * }
 * </pre>
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    String value();

}
