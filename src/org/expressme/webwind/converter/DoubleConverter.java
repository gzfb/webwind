package org.expressme.webwind.converter;

/**
 * Convert String to Double.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class DoubleConverter implements Converter<Double> {

    public Double convert(String s) {
        return Double.parseDouble(s);
    }

}
