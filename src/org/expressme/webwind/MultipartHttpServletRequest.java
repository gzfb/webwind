package org.expressme.webwind;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * Wrapper class for multipart HTTP request which usually used to upload file.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class MultipartHttpServletRequest extends HttpServletRequestWrapper {

    final HttpServletRequest target;
    final Map<String, List<FileItemStream>> fileItems;
    final Map<String, List<String>> formItems;

    /**
     * Test if current HttpServletRequest is a multipart request.
     */
    public static boolean isMultipartRequest(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    /**
     * Get the file names by field name. If no file found by this field, null 
     * will be returned, otherwise, file names are returned.
     * 
     * @param fieldName Field name in the form.
     * @return File names.
     */
    public String[] getFileNames(String fieldName) {
        List<FileItemStream> list = fileItems.get(fieldName);
        if (list==null)
            return null;
        String[] names = new String[list.size()];
        int n = 0;
        for (FileItemStream fis : list) {
            names[n] = fis.getName();
            n++;
        }
        return names;
    }

    /**
     * Get the file name by field name. If no file found by this field, null 
     * will be returned. If more than one files are uploaded as the same field 
     * name, the first file name will be returned.
     * 
     * @param fieldName Field name in the form.
     * @return File name.
     */
    public String getFileName(String fieldName) {
        List<FileItemStream> list = fileItems.get(fieldName);
        if (list==null)
            return null;
        return list.get(0).getName();
    }

    /**
     * Get the input streams of uploaded file by field name. If no file found by 
     * this field, IOException will raise.
     * 
     * @param fieldName Field name in the form.
     * @return InputStream array of the file.
     */
    public InputStream[] getFileInputStreams(String fieldName) throws IOException {
        List<FileItemStream> list = fileItems.get(fieldName);
        if (list==null)
            throw new IOException("No file item with name '" + fieldName + "'.");
        InputStream[] iss = new InputStream[list.size()];
        int n = 0;
        for (FileItemStream fis : list) {
            iss[n] = fis.openStream();
            n++;
        }
        return iss;
    }

    /**
     * Get the input stream of uploaded file by field name. If no file found by 
     * this field, IOException will raise. If more than one files are uploaded 
     * as the same field name, the input stream of first file will be returned.
     * 
     * @param fieldName Field name in the form.
     * @return InputStream of the file.
     */
    public InputStream getFileInputStream(String fieldName) throws IOException {
        List<FileItemStream> list = fileItems.get(fieldName);
        if (list==null)
            throw new IOException("No file item with name '" + fieldName + "'.");
        return list.get(0).openStream();
    }

    /**
     * Get all fields' names of uploaded files.
     * 
     * @return Fields' names of uploaded files.
     */
    public String[] getFileFields() {
        Set<String> set = fileItems.keySet();
        return set.toArray(new String[set.size()]);
    }

    @Override
    public String getParameter(String name) {
        List<String> list = formItems.get(name);
        if (list==null)
            return null;
        return list.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map getParameterMap() {
        Map<String, String[]> map = new HashMap<String, String[]>();
        Set<String> keys = formItems.keySet();
        for (String key : keys) {
            List<String> list = formItems.get(key);
            map.put(key, list.toArray(new String[list.size()]));
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration getParameterNames() {
        return Collections.enumeration(formItems.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        List<String> list = formItems.get(name);
        if (list==null)
            return null;
        return list.toArray(new String[list.size()]);
    }

    public MultipartHttpServletRequest(HttpServletRequest request, long maxFileSize) throws IOException {
        super(request);
        this.target = request;
        this.fileItems = new HashMap<String, List<FileItemStream>>();
        this.formItems = new HashMap<String, List<String>>();

        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileSizeMax(maxFileSize);
        try {
            FileItemIterator it = upload.getItemIterator(target);
            while (it.hasNext()) {
                FileItemStream item = it.next();
                if (item.isFormField()) {
                    InputStream input = null;
                    try {
                        input = item.openStream();
                        String fieldName = item.getFieldName();
                        String encode = request.getCharacterEncoding();
                        String fieldValue = encode==null ? Streams.asString(input) : Streams.asString(input, encode);
                        addFormItem(fieldName, fieldValue);
                    }
                    finally {
                        if (input!=null) {
                            input.close();
                        }
                    }
                }
                else {
                    // it is a file item:
                    addFileItem(item);
                }
            }
        }
        catch (FileUploadException e) {
            throw new IOException(e);
        }
    }

    void addFileItem(FileItemStream item) {
        String fieldName = item.getFieldName();
        List<FileItemStream> list = fileItems.get(fieldName);
        if (list==null) {
            list = new ArrayList<FileItemStream>(3);
            fileItems.put(fieldName, list);
        }
        list.add(item);
    }

    void addFormItem(String fieldName, String fieldValue) {
        List<String> list = formItems.get(fieldName);
        if (list==null) {
            list = new ArrayList<String>(5);
            formItems.put(fieldName, list);
        }
        list.add(fieldValue);
    }

}
