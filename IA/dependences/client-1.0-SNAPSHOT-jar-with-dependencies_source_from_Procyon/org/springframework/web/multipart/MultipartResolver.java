// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

public interface MultipartResolver
{
    boolean isMultipart(final HttpServletRequest p0);
    
    MultipartHttpServletRequest resolveMultipart(final HttpServletRequest p0) throws MultipartException;
    
    void cleanupMultipart(final MultipartHttpServletRequest p0);
}
