package org.expressme.webwind.converter;

/**
 * Convert String to Integer.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class IntegerConverter implements Converter<Integer> {

    public Integer convert(String s) {
        return Integer.parseInt(s);
    }

}
