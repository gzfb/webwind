package org.expressme.webwind;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default exception handler which just print the exception trace on web page.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class DefaultExceptionHandler implements ExceptionHandler {

    /**
     * Handle exception that print stack trace on HTML page.
     */
    public void handle(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        PrintWriter pw = response.getWriter();
        pw.write("<html><head><title>Exception</title></head><body><pre>");
        e.printStackTrace(pw);
        pw.write("</pre></body></html>");
        pw.flush();
    }

}
