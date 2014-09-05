package org.expressme.webwind;

/**
 * Holds all interceptors as a chain.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public interface InterceptorChain {

    /**
     * Apply next interceptor around the execution of Action.
     * 
     * @param execution Execution to execute.
     * @throws Exception Any exception if error occured.
     */
    void doInterceptor(Execution execution) throws Exception;

}
