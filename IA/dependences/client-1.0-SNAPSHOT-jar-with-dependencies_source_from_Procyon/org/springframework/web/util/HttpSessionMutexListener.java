// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.io.Serializable;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionMutexListener implements HttpSessionListener
{
    public void sessionCreated(final HttpSessionEvent event) {
        event.getSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, (Object)new Mutex());
    }
    
    public void sessionDestroyed(final HttpSessionEvent event) {
        event.getSession().removeAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);
    }
    
    private static class Mutex implements Serializable
    {
    }
}
