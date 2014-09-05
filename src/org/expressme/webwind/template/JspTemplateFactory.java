package org.expressme.webwind.template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expressme.webwind.Config;

/**
 * TemplateFactory which uses JSP.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class JspTemplateFactory extends TemplateFactory {

    private Log log = LogFactory.getLog(getClass());

    public Template loadTemplate(String path) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Load JSP template '" + path + "'.");
        return new JspTemplate(path);
    }

    public void init(Config config) {
        log.info("JspTemplateFactory init ok.");
    }

}
