package org.expressme.webwind.converter;

/**
 * Convert String to Boolean.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class BooleanConverter implements Converter<Boolean> {

    public Boolean convert(String s) {
        return Boolean.parseBoolean(s);
    }

}
