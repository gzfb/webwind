package org.expressme.webwind.container;

import java.util.ArrayList;
import java.util.List;

import org.expressme.webwind.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Wrapper for Spring 2.x.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class SpringContainerFactory implements ContainerFactory {

    private ApplicationContext appContext;

    public List<Object> findAllBeans() {
        String[] beanNames = appContext.getBeanDefinitionNames();
        List<Object> beans = new ArrayList<Object>(beanNames.length);
        for (int i=0; i<beanNames.length; i++) {
            beans.add(appContext.getBean(beanNames[i]));
        }
        return beans;
    }

    public void init(Config config) {
        appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
    }

    public void destroy() {
        // nothing to do, let spring destroy all beans.
    }

}
