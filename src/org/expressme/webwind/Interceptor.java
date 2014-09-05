package org.expressme.webwind;

/**
 * Intercept action's execution like servlet Filter, but interceptors are 
 * configured and managed by IoC container. Another difference from Filter 
 * is that Interceptor is executed around Action's execution, but before 
 * rendering view.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public interface Interceptor {

    /**
     * Do intercept and invoke chain.doInterceptor() to process next interceptor. 
     * NOTE that process will not continue if chain.doInterceptor() method is not 
     * invoked.
     * 
     * @param execution Execution instance to handle http request.
     * @param chain Interceptor chain.
     * @throws Exception If any exception is thrown, process will not continued.
     */
    void intercept(Execution execution, InterceptorChain chain) throws Exception;

}
