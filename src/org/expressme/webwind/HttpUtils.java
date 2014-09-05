package org.expressme.webwind;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpUtils that contains useful methods to access HTTP objects.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class HttpUtils {

    private static final String[] EMPTY_VALUES = new String[0];

    /**
     * Get int-type parameter. If parameter is not exist, or cannot be converted 
     * to int, default value is return.
     * 
     * @param name Parameter name.
     * @param defaultValue Default int value.
     * @return int value of specified parameter.
     */
    public static int getIntParameter(String name, int defaultValue) {
        String value = getStringParameter(name);
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get long-type parameter. If parameter is not exist, or cannot be converted 
     * to long, default value is return.
     * 
     * @param name Parameter name.
     * @param defaultValue Default long value.
     * @return long value of specified parameter.
     */
    public static long getLongParameter(String name, long defaultValue) {
        String value = getStringParameter(name);
        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get parameter value as String. If parameter is not exist, return null.
     * 
     * @param name Parameter name.
     * @return String value, or null if no such parameter.
     */
    public static String getStringParameter(String name) {
        return getStringParameter(name, null);
    }

    /**
     * Get parameter value as String. If parameter is not exist, return default value.
     * 
     * @param name Parameter name.
     * @param defaultValue Default String value.
     * @return String value.
     */
    public static String getStringParameter(String name, String defaultValue) {
        HttpServletRequest request = ActionContext.getActionContext().getHttpServletRequest();
        String value = request.getParameter(name);
        return value==null ? defaultValue : value;
    }

    /**
     * Get parameter values as String array. If parameter name is not exist, empty 
     * String is returned which is different with HttpServletRequest.getParameterValues().
     * 
     * @param name Parameter name.
     * @return Parameter values as String array.
     */
    public static String[] getParameterValues(String name) {
        HttpServletRequest request = ActionContext.getActionContext().getHttpServletRequest();
        String[] values = request.getParameterValues(name);
        return values==null ? EMPTY_VALUES : values;
    }

    /**
     * Get cookie value by name. Return null if cookie is not found.
     * 
     * @param name Cookie name.
     * @return Cookie value.
     */
    public static String getCookie(String name) {
        return getCookie(name, null);
    }

    /**
     * Get cookie value by name. Return default value if cookie is not found.
     * 
     * @param name Cookie name.
     * @param defaultValue Cookie default value.
     * @return Cookie value.
     */
    public static String getCookie(String name, String defaultValue) {
        HttpServletRequest request = ActionContext.getActionContext().getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return defaultValue;
    }

    /**
     * Remove a cookie by name and its default path "/".
     * 
     * @param name Cookie name.
     */
    public static void removeCookie(String name) {
        setCookie(name, "__deleted__", 0, "/");
    }

    /**
     * Remove a cookie by name and its path.
     * 
     * @param name Cookie name.
     * @param path Cookie path.
     */
    public static void removeCookie(String name, String path) {
        setCookie(name, "__deleted__", 0, path);
    }

    /**
     * Set new cookie by name, value and max age, with default path "/".
     * 
     * @param name Cookie name.
     * @param value Cookie value.
     * @param maxAgeInSeconds Cookie age in seconds.
     */
    public static void setCookie(String name, String value, int maxAgeInSeconds) {
        setCookie(name, value, maxAgeInSeconds, "/");
    }

    /**
     * Set new cookie by name, value, max age and path.
     * 
     * @param name Cookie name.
     * @param value Cookie value.
     * @param maxAgeInSeconds Cookie age in seconds.
     * @param path Cookie path.
     */
    public static void setCookie(String name, String value, int maxAgeInSeconds, String path) {
        HttpServletResponse response = ActionContext.getActionContext().getHttpServletResponse();
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        response.addCookie(cookie);
    }
}
