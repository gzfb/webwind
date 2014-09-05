package org.expressme.webwind.converter;

/**
 * Convert String to Byte.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class ByteConverter implements Converter<Byte> {

    public Byte convert(String s) {
        return Byte.parseByte(s);
    }

}
