// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import java.util.Iterator;
import org.apache.commons.logging.LogFactory;
import javax.servlet.http.Part;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

public class StandardServletMultipartResolver implements MultipartResolver
{
    @Override
    public boolean isMultipart(final HttpServletRequest request) {
        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }
        final String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }
    
    @Override
    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
        return new StandardMultipartHttpServletRequest(request);
    }
    
    @Override
    public void cleanupMultipart(final MultipartHttpServletRequest request) {
        try {
            for (final Part part : request.getParts()) {
                if (request.getFile(part.getName()) != null) {
                    part.delete();
                }
            }
        }
        catch (Exception ex) {
            LogFactory.getLog(this.getClass()).warn("Failed to perform cleanup of multipart items", ex);
        }
    }
}
