// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import org.apache.log4j.NDC;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class Log4jNestedDiagnosticContextFilter extends AbstractRequestLoggingFilter
{
    protected final Logger log4jLogger;
    
    public Log4jNestedDiagnosticContextFilter() {
        this.log4jLogger = Logger.getLogger((Class)this.getClass());
    }
    
    @Override
    protected void beforeRequest(final HttpServletRequest request, final String message) {
        if (this.log4jLogger.isDebugEnabled()) {
            this.log4jLogger.debug((Object)message);
        }
        NDC.push(this.getNestedDiagnosticContextMessage(request));
    }
    
    protected String getNestedDiagnosticContextMessage(final HttpServletRequest request) {
        return this.createMessage(request, "", "");
    }
    
    @Override
    protected void afterRequest(final HttpServletRequest request, final String message) {
        NDC.pop();
        if (NDC.getDepth() == 0) {
            NDC.remove();
        }
        if (this.log4jLogger.isDebugEnabled()) {
            this.log4jLogger.debug((Object)message);
        }
    }
}
