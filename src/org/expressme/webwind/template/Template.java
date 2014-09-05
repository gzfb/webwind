package org.expressme.webwind.template;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Template interface.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public interface Template {

    /**
     * Render by template engine.
     * 
     * @param request HttpServletRequest object.
     * @param response HttpServletResponse object.
     * @param model Model as java.util.Map.
     * @throws Exception If render failed.
     */
    void render(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception;
}
