// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.jsf;

import javax.faces.context.ExternalContext;
import org.springframework.web.util.WebUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import javax.faces.context.FacesContext;

public abstract class FacesContextUtils
{
    public static WebApplicationContext getWebApplicationContext(final FacesContext fc) {
        Assert.notNull(fc, "FacesContext must not be null");
        final Object attr = fc.getExternalContext().getApplicationMap().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw (RuntimeException)attr;
        }
        if (attr instanceof Error) {
            throw (Error)attr;
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Root context attribute is not of type WebApplicationContext: " + attr);
        }
        return (WebApplicationContext)attr;
    }
    
    public static WebApplicationContext getRequiredWebApplicationContext(final FacesContext fc) throws IllegalStateException {
        final WebApplicationContext wac = getWebApplicationContext(fc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }
    
    public static Object getSessionMutex(final FacesContext fc) {
        Assert.notNull(fc, "FacesContext must not be null");
        final ExternalContext ec = fc.getExternalContext();
        Object mutex = ec.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = ec.getSession(true);
        }
        return mutex;
    }
}
