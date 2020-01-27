// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequest;

public class CommonsRequestLoggingFilter extends AbstractRequestLoggingFilter
{
    @Override
    protected void beforeRequest(final HttpServletRequest request, final String message) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(message);
        }
    }
    
    @Override
    protected void afterRequest(final HttpServletRequest request, final String message) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(message);
        }
    }
}
