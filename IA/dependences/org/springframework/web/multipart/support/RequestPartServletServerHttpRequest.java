// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import org.springframework.web.multipart.MultipartException;
import java.io.InputStream;
import org.springframework.util.ClassUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;

public class RequestPartServletServerHttpRequest extends ServletServerHttpRequest
{
    private final MultipartHttpServletRequest multipartRequest;
    private final String partName;
    private final HttpHeaders headers;
    
    public RequestPartServletServerHttpRequest(final HttpServletRequest request, final String partName) throws MissingServletRequestPartException {
        super(request);
        this.multipartRequest = asMultipartRequest(request);
        this.partName = partName;
        this.headers = this.multipartRequest.getMultipartHeaders(this.partName);
        if (this.headers != null) {
            return;
        }
        if (request instanceof MultipartHttpServletRequest) {
            throw new MissingServletRequestPartException(partName);
        }
        throw new IllegalArgumentException("Failed to obtain request part: " + partName + ". " + "The part is missing or multipart processing is not configured. " + "Check for a MultipartResolver bean or if Servlet 3.0 multipart processing is enabled.");
    }
    
    private static MultipartHttpServletRequest asMultipartRequest(final HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest) {
            return (MultipartHttpServletRequest)request;
        }
        if (ClassUtils.hasMethod(HttpServletRequest.class, "getParts", (Class<?>[])new Class[0])) {
            return new StandardMultipartHttpServletRequest(request);
        }
        throw new IllegalArgumentException("Expected MultipartHttpServletRequest: is a MultipartResolver configured?");
    }
    
    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }
    
    @Override
    public InputStream getBody() throws IOException {
        if (this.multipartRequest instanceof StandardMultipartHttpServletRequest) {
            try {
                return this.multipartRequest.getPart(this.partName).getInputStream();
            }
            catch (Exception ex) {
                throw new MultipartException("Could not parse multipart servlet request", ex);
            }
        }
        final MultipartFile file = this.multipartRequest.getFile(this.partName);
        if (file != null) {
            return file.getInputStream();
        }
        final String paramValue = this.multipartRequest.getParameter(this.partName);
        return new ByteArrayInputStream(paramValue.getBytes("UTF-8"));
    }
}
