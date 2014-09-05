package org.expressme.webwind.renderer;

/**
 * Convenience for render JavaScript.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class JavaScriptRenderer extends TextRenderer {

    static final String MIME_JAVASCRIPT = "application/x-javascript";

    public JavaScriptRenderer() {
        setContentType(MIME_JAVASCRIPT);
    }

    public JavaScriptRenderer(String text) {
        super(text);
        setContentType(MIME_JAVASCRIPT);
    }

    public JavaScriptRenderer(String text, String characterEncoding) {
        super(text, characterEncoding);
        setContentType(MIME_JAVASCRIPT);
    }

}
