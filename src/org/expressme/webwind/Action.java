package org.expressme.webwind;

import java.lang.reflect.Method;

/**
 * Internal class which holds object instance, method and arguments' types.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
class Action {

    /**
     * Object instance.
     */
    public final Object instance;

    /**
     * Method instance.
     */
    public final Method method;

    /**
     * Method's arguments' types.
     */
    public final Class<?>[] arguments;

    public Action(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
        this.arguments = method.getParameterTypes();
    }

}
