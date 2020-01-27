// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import javax.servlet.http.HttpServletRequest;

public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest
{
    HttpMethod getRequestMethod();
    
    HttpHeaders getRequestHeaders();
    
    HttpHeaders getMultipartHeaders(final String p0);
}
