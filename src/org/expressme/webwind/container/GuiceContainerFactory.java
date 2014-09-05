package org.expressme.webwind.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expressme.webwind.Config;
import org.expressme.webwind.ConfigException;
import org.expressme.webwind.Destroyable;
import org.expressme.webwind.guice.ServletContextAware;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;

/**
 * Create Guice 2.0 Injector instance, and bind it on ServletContext with 
 * name of <code>Injector.class.getName()</code>.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class GuiceContainerFactory implements ContainerFactory {

    private Log log = LogFactory.getLog(getClass());

    private Injector injector;

    public List<Object> findAllBeans() {
        Map<Key<?>, Binding<?>> map = injector.getBindings();
        Set<Key<?>> keys = map.keySet();
        List<Object> list = new ArrayList<Object>(keys.size());
        for (Key<?> key : keys) {
            Object bean = injector.getInstance(key);
            list.add(bean);
        }
        return list;
    }

    public void init(final Config config) {
        String value = config.getInitParameter("modules");
        if (value==null)
            throw new ConfigException("Init guice failed. Missing parameter '<modules>'.");
        String[] ss = value.split(",");
        List<Module> moduleList = new ArrayList<Module>(ss.length);
        for (String s : ss) {
            Module m = initModule(s, config.getServletContext());
            if (m!=null)
                moduleList.add(m);
        }
        if (moduleList.isEmpty())
            throw new ConfigException("No module found.");
        this.injector = Guice.createInjector(Stage.PRODUCTION, moduleList);
        config.getServletContext().setAttribute(Injector.class.getName(), this.injector);
    }

    Module initModule(String s, ServletContext servletContext) {
        s = trim(s);
        if (s.length()>0) {
            if (log.isDebugEnabled())
                log.debug("Initializing module '" + s + "'...");
            try {
                Object o = Class.forName(s).newInstance();
                if (o instanceof Module) {
                    if (o instanceof ServletContextAware) {
                        ((ServletContextAware) o).setServletContext(servletContext);
                    }
                    return (Module) o;
                }
                throw new ConfigException("Class '" + s + "' does not implement '" + Module.class.getName() + "'.");
            }
            catch(InstantiationException e) {
                throw new ConfigException("Cannot instanciate class '" + s + "'.", e);
            }
            catch(IllegalAccessException e) {
                throw new ConfigException("Cannot instanciate class '" + s + "'.", e);
            }
            catch(ClassNotFoundException e) {
                throw new ConfigException("Cannot instanciate class '" + s + "'.", e);
            }
        }
        return null;
    }

    String trim(String s) {
        while (s.length()>0) {
            char c = s.charAt(0);
            if (" \t\r\n".indexOf(c)!=(-1))
                s = s.substring(1);
            else
                break;
        }
        while (s.length()>0) {
            char c = s.charAt(s.length()-1);
            if (" \t\r\n".indexOf(c)!=(-1))
                s = s.substring(0, s.length()-1);
            else
                break;
        }
        return s;
    }

    public void destroy() {
        List<Object> beans = findAllBeans();
        for (Object bean : beans) {
            if (bean instanceof Destroyable) {
                ((Destroyable)bean).destroy();
            }
        }
    }

}
