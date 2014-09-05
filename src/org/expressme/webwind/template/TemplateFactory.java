package org.expressme.webwind.template;

import org.apache.commons.logging.LogFactory;
import org.expressme.webwind.Config;

/**
 * TemplateFactory which holds the singleton instance of TemplateFactory.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public abstract class TemplateFactory {

    private static TemplateFactory instance;

    /**
     * Set the static TemplateFactory instance by webwind framework.
     */
    public static void setTemplateFactory(TemplateFactory templateFactory) {
        instance = templateFactory;
        LogFactory.getLog(TemplateFactory.class).info("TemplateFactory is set to: " + instance);
    }

    /**
     * Get the static TemplateFactory instance.
     */
    public static TemplateFactory getTemplateFactory() {
        return instance;
    }

    /**
     * Init TemplateFactory.
     */
    public abstract void init(Config config);

    /**
     * Load Template from path.
     * 
     * @param path Template file path, relative with webapp's root path.
     * @return Template instance.
     * @throws Exception If load failed, e.g., file not found.
     */
    public abstract Template loadTemplate(String path) throws Exception;

}
