// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Enumeration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.MultiValueMap;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class DefaultMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest
{
    private static final String CONTENT_TYPE = "Content-Type";
    private Map<String, String[]> multipartParameters;
    private Map<String, String> multipartParameterContentTypes;
    
    public DefaultMultipartHttpServletRequest(final HttpServletRequest request, final MultiValueMap<String, MultipartFile> mpFiles, final Map<String, String[]> mpParams, final Map<String, String> mpParamContentTypes) {
        super(request);
        this.setMultipartFiles(mpFiles);
        this.setMultipartParameters(mpParams);
        this.setMultipartParameterContentTypes(mpParamContentTypes);
    }
    
    public DefaultMultipartHttpServletRequest(final HttpServletRequest request) {
        super(request);
    }
    
    public Enumeration<String> getParameterNames() {
        final Set<String> paramNames = new HashSet<String>();
        final Enumeration<String> paramEnum = (Enumeration<String>)super.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            paramNames.add(paramEnum.nextElement());
        }
        paramNames.addAll(this.getMultipartParameters().keySet());
        return Collections.enumeration(paramNames);
    }
    
    public String getParameter(final String name) {
        final String[] values = this.getMultipartParameters().get(name);
        if (values != null) {
            return (values.length > 0) ? values[0] : null;
        }
        return super.getParameter(name);
    }
    
    public String[] getParameterValues(final String name) {
        final String[] values = this.getMultipartParameters().get(name);
        if (values != null) {
            return values;
        }
        return super.getParameterValues(name);
    }
    
    public Map<String, String[]> getParameterMap() {
        final Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.putAll(super.getParameterMap());
        paramMap.putAll(this.getMultipartParameters());
        return paramMap;
    }
    
    public String getMultipartContentType(final String paramOrFileName) {
        final MultipartFile file = this.getFile(paramOrFileName);
        if (file != null) {
            return file.getContentType();
        }
        return this.getMultipartParameterContentTypes().get(paramOrFileName);
    }
    
    public HttpHeaders getMultipartHeaders(final String paramOrFileName) {
        final String contentType = this.getMultipartContentType(paramOrFileName);
        if (contentType != null) {
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", contentType);
            return headers;
        }
        return null;
    }
    
    protected final void setMultipartParameters(final Map<String, String[]> multipartParameters) {
        this.multipartParameters = multipartParameters;
    }
    
    protected Map<String, String[]> getMultipartParameters() {
        if (this.multipartParameters == null) {
            this.initializeMultipart();
        }
        return this.multipartParameters;
    }
    
    protected final void setMultipartParameterContentTypes(final Map<String, String> multipartParameterContentTypes) {
        this.multipartParameterContentTypes = multipartParameterContentTypes;
    }
    
    protected Map<String, String> getMultipartParameterContentTypes() {
        if (this.multipartParameterContentTypes == null) {
            this.initializeMultipart();
        }
        return this.multipartParameterContentTypes;
    }
}
