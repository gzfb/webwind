package org.expressme.webwind;

/**
 * If any configuration is incorrect.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class ConfigException extends IllegalArgumentException {

    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
