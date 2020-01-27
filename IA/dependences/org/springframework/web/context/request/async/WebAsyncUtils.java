// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.WebRequest;
import javax.servlet.ServletRequest;
import java.lang.reflect.Constructor;

public abstract class WebAsyncUtils
{
    public static final String WEB_ASYNC_MANAGER_ATTRIBUTE;
    private static Constructor<?> standardAsyncRequestConstructor;
    
    public static WebAsyncManager getAsyncManager(final ServletRequest servletRequest) {
        WebAsyncManager asyncManager = (WebAsyncManager)servletRequest.getAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE);
        if (asyncManager == null) {
            asyncManager = new WebAsyncManager();
            servletRequest.setAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, (Object)asyncManager);
        }
        return asyncManager;
    }
    
    public static WebAsyncManager getAsyncManager(final WebRequest webRequest) {
        final int scope = 0;
        WebAsyncManager asyncManager = (WebAsyncManager)webRequest.getAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, scope);
        if (asyncManager == null) {
            asyncManager = new WebAsyncManager();
            webRequest.setAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager, scope);
        }
        return asyncManager;
    }
    
    public static AsyncWebRequest createAsyncWebRequest(final HttpServletRequest request, final HttpServletResponse response) {
        return ClassUtils.hasMethod(ServletRequest.class, "startAsync", (Class<?>[])new Class[0]) ? createStandardServletAsyncWebRequest(request, response) : new NoSupportAsyncWebRequest(request, response);
    }
    
    private static AsyncWebRequest createStandardServletAsyncWebRequest(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            if (WebAsyncUtils.standardAsyncRequestConstructor == null) {
                final String className = "org.springframework.web.context.request.async.StandardServletAsyncWebRequest";
                final Class<?> clazz = ClassUtils.forName(className, WebAsyncUtils.class.getClassLoader());
                WebAsyncUtils.standardAsyncRequestConstructor = clazz.getConstructor(HttpServletRequest.class, HttpServletResponse.class);
            }
            return BeanUtils.instantiateClass(WebAsyncUtils.standardAsyncRequestConstructor, request, response);
        }
        catch (Throwable t) {
            throw new IllegalStateException("Failed to instantiate StandardServletAsyncWebRequest", t);
        }
    }
    
    static {
        WEB_ASYNC_MANAGER_ATTRIBUTE = WebAsyncManager.class.getName() + ".WEB_ASYNC_MANAGER";
    }
}
