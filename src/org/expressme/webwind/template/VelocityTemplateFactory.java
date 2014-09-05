package org.expressme.webwind.template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.expressme.webwind.Config;

/**
 * Template factory using Velocity.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class VelocityTemplateFactory extends TemplateFactory {

    private final Log log = LogFactory.getLog(getClass());

    // velocity runtime instance as singleton:
    private RuntimeInstance rtInstance;

    private String inputEncoding = "UTF-8";
    private String outputEncoding = "UTF-8";

    /**
     * Get input encoding of template. Default to UTF-8, can be specified by 
     * 'input.encoding' in velocity.properties.
     */
    public String getInputEncoding() {
        return inputEncoding;
    }

    /**
     * Get output encoding of template. Default to UTF-8, can be specified by 
     * 'output.encoding' in velocity.properties.
     */
    public String getOutputEncoding() {
        return outputEncoding;
    }

    public Template loadTemplate(String path) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Load Velocity template '" + path + "'.");
        return new VelocityTemplate(
                rtInstance.getTemplate(path, inputEncoding),
                null,
                outputEncoding
        );
    }

    public void init(Config config) {
        String webAppPath = config.getServletContext().getRealPath("/");
        if (webAppPath==null) {
            String err = "Cannot get web application path. Are you deploy the application as a .war file?";
            log.warn(err);
            throw new ExceptionInInitializerError(err);
        }
        if (!webAppPath.endsWith("/") && !webAppPath.endsWith("\\"))
            webAppPath = webAppPath + File.separator;
        log.info("Detect web application path: " + webAppPath);
        log.info("init VelocityTemplateFactory...");
        rtInstance = new RuntimeInstance();
        // read property file:
        Properties props = readProperties(webAppPath + "/WEB-INF/velocity.properties");
        checkProperty(props);
        Set<?> keys = props.keySet();
        for(Object key : keys) {
            if(key instanceof String) {
                Object value = props.get(key);
                rtInstance.addProperty((String)key, value);
            }
        }
        try {
            if(rtInstance.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH)==null) {
                rtInstance.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, webAppPath);
                log.info("Using web application context path as velocity template root path: " + webAppPath);
            }
            else {
                log.info("Velocity property \"" + RuntimeConstants.FILE_RESOURCE_LOADER_PATH + "\" has been specified in \"/WEB-INF/velocity.properties\".");
                log.info("To using web application context path, remove property \"" + RuntimeConstants.FILE_RESOURCE_LOADER_PATH + "\" from configuration file.");
            }
            rtInstance.init();
            afterVelocityEngineInit(rtInstance);
        }
        catch(Exception e) {
            log.error("VelocityTemplateFactory init failed.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    void checkProperty(Properties props) {
        this.inputEncoding = setDefaultPropertyIfNotExist(props, "input.encoding", "UTF-8");
        this.outputEncoding = setDefaultPropertyIfNotExist(props, "output.encoding", "UTF-8");
    }

    String setDefaultPropertyIfNotExist(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key);
        if (value==null || value.length()==0) {
            props.setProperty(key, defaultValue);
        }
        return props.getProperty(key);
    }

    protected void afterVelocityEngineInit(RuntimeInstance instance) {
        // let subclass do some work
    }

    /**
     * Read velocity properties from file.
     * 
     * @param file Velocity configuration file.
     * @return Velocity Properties.
     */
    protected Properties readProperties(String file) {
        Properties props = new Properties();
        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            props.load(input);
        }
        catch(IOException ioe) {
            log.warn("Read properties file failed.", ioe);
        }
        finally {
            if(input!=null) {
                try {
                    input.close();
                }
                catch(IOException e) {}
            }
        }
        return props;
    }
}
