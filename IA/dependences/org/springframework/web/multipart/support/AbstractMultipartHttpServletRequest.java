// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import javax.servlet.ServletRequest;
import org.springframework.util.LinkedMultiValueMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public abstract class AbstractMultipartHttpServletRequest extends HttpServletRequestWrapper implements MultipartHttpServletRequest
{
    private MultiValueMap<String, MultipartFile> multipartFiles;
    
    protected AbstractMultipartHttpServletRequest(final HttpServletRequest request) {
        super(request);
    }
    
    public HttpServletRequest getRequest() {
        return (HttpServletRequest)super.getRequest();
    }
    
    public HttpMethod getRequestMethod() {
        return HttpMethod.valueOf(this.getRequest().getMethod());
    }
    
    public HttpHeaders getRequestHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        final Enumeration<String> headerNames = (Enumeration<String>)this.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            headers.put(headerName, (List<String>)Collections.list((Enumeration<Object>)this.getHeaders(headerName)));
        }
        return headers;
    }
    
    public Iterator<String> getFileNames() {
        return this.getMultipartFiles().keySet().iterator();
    }
    
    public MultipartFile getFile(final String name) {
        return this.getMultipartFiles().getFirst(name);
    }
    
    public List<MultipartFile> getFiles(final String name) {
        final List<MultipartFile> multipartFiles = this.getMultipartFiles().get(name);
        if (multipartFiles != null) {
            return multipartFiles;
        }
        return Collections.emptyList();
    }
    
    public Map<String, MultipartFile> getFileMap() {
        return this.getMultipartFiles().toSingleValueMap();
    }
    
    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        return this.getMultipartFiles();
    }
    
    protected final void setMultipartFiles(final MultiValueMap<String, MultipartFile> multipartFiles) {
        this.multipartFiles = new LinkedMultiValueMap<String, MultipartFile>(Collections.unmodifiableMap((Map<? extends String, ? extends List<MultipartFile>>)multipartFiles));
    }
    
    protected MultiValueMap<String, MultipartFile> getMultipartFiles() {
        if (this.multipartFiles == null) {
            this.initializeMultipart();
        }
        return this.multipartFiles;
    }
    
    protected void initializeMultipart() {
        throw new IllegalStateException("Multipart request not initialized");
    }
}
