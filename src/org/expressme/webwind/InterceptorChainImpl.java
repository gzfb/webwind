package org.expressme.webwind;

/**
 * Used for holds an interceptor chain.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
class InterceptorChainImpl implements InterceptorChain {

    private final Interceptor[] interceptors;
    private int index = 0;
    private Object result = null;

    InterceptorChainImpl(Interceptor[] interceptors) {
        this.interceptors = interceptors;
    }

    Object getResult() {
        return result;
    }

    public void doInterceptor(Execution execution) throws Exception {
        if(index==interceptors.length)
            result = execution.execute();
        else {
            // must update index first, otherwise will cause stack overflow:
            index++;
            interceptors[index-1].intercept(execution, this);
        }
    }
}
