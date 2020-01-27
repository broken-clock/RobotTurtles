// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequest;

public class ServletContextRequestLoggingFilter extends AbstractRequestLoggingFilter
{
    @Override
    protected void beforeRequest(final HttpServletRequest request, final String message) {
        this.getServletContext().log(message);
    }
    
    @Override
    protected void afterRequest(final HttpServletRequest request, final String message) {
        this.getServletContext().log(message);
    }
}
