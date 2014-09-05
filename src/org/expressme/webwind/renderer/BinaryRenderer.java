package org.expressme.webwind.renderer;

import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Render http response as binary stream. This is usually used to render PDF, 
 * image, or any binary type.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class BinaryRenderer extends Renderer {

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void render(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(contentType==null ? "application/octet-stream" : contentType);
        response.setContentLength(data.length);
        OutputStream output = response.getOutputStream();
        output.write(data);
        output.flush();
    }

}
