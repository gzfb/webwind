package org.expressme.webwind.guice;

import javax.servlet.ServletContext;

/**
 * Guice module which implements this interface will automatically get the 
 * ServletContext object in web application.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public interface ServletContextAware {

    /**
     * Called by GuiceContainerFactory when initialize module.
     * 
     * @param servletContext The ServletContext object.
     */
    void setServletContext(ServletContext servletContext);

}
