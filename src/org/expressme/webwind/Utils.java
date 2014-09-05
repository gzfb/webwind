package org.expressme.webwind;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expressme.webwind.container.ContainerFactory;
import org.expressme.webwind.template.TemplateFactory;

/**
 * Utils for create ContainerFactory, TemplateFactory, etc.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
class Utils {

    static final Log log = LogFactory.getLog(Utils.class);

    public static ContainerFactory createContainerFactory(String name) throws ServletException {
        ContainerFactory cf = tryInitContainerFactory(name);
        if (cf==null)
            cf = tryInitContainerFactory(ContainerFactory.class.getPackage().getName() + "." + name + ContainerFactory.class.getSimpleName());
        if (cf==null)
            throw new ConfigException("Cannot create container factory by name '" + name + "'.");
        return cf;
    }

    static ContainerFactory tryInitContainerFactory(String clazz) {
        try {
            Object obj = Class.forName(clazz).newInstance();
            if (obj instanceof ContainerFactory)
                return (ContainerFactory) obj;
        }
        catch (Exception e) { }
        return null;
    }

    public static TemplateFactory createTemplateFactory(String name) {
        TemplateFactory tf = tryInitTemplateFactory(name);
        if (tf==null)
            tf = tryInitTemplateFactory(TemplateFactory.class.getPackage().getName() + "." + name + TemplateFactory.class.getSimpleName());
        if (tf==null) {
            log.warn("Cannot init template factory '" + name + "'.");
            throw new ConfigException("Cannot init template factory '" + name + "'.");
        }
        return tf;
    }

    static TemplateFactory tryInitTemplateFactory(String clazz) {
        try {
            Object obj = Class.forName(clazz).newInstance();
            if (obj instanceof TemplateFactory)
                return (TemplateFactory) obj;
        }
        catch(Exception e) { }
        return null;
    }

}
