package org.expressme.webwind;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DispatcherFilter must be mapped to URL "/*". It handles ALL requests from 
 * clients, and dispatches to appropriate handler to handle each request.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class DispatcherFilter implements Filter {

    private final Log log = LogFactory.getLog(getClass());

    private Dispatcher dispatcher;

    public void init(final FilterConfig filterConfig) throws ServletException {
        log.info("Init DispatcherFilter...");
        this.dispatcher = new Dispatcher();
        this.dispatcher.init(
                new Config() {
                    public String getInitParameter(String name) {
                        return filterConfig.getInitParameter(name);
                    }

                    public ServletContext getServletContext() {
                        return filterConfig.getServletContext();
                    }
                }
        );
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;
        String method = httpReq.getMethod();
        if ("GET".equals(method) || "POST".equals(method)) {
            if (!dispatcher.service(httpReq, httpResp))
                chain.doFilter(req, resp);
            return;
        }
        httpResp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    public void destroy() {
        log.info("Destroy DispatcherFilter...");
        this.dispatcher.destroy();
    }

}
