// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.support;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.beans.BeansException;
import java.io.Serializable;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.MutablePropertySources;
import java.util.Enumeration;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import org.springframework.web.context.request.WebRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;
import org.springframework.web.context.request.SessionScope;
import org.springframework.beans.factory.config.Scope;
import org.springframework.web.context.request.RequestScope;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;

public abstract class WebApplicationContextUtils
{
    private static final boolean jsfPresent;
    
    public static WebApplicationContext getRequiredWebApplicationContext(final ServletContext sc) throws IllegalStateException {
        final WebApplicationContext wac = getWebApplicationContext(sc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }
    
    public static WebApplicationContext getWebApplicationContext(final ServletContext sc) {
        return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }
    
    public static WebApplicationContext getWebApplicationContext(final ServletContext sc, final String attrName) {
        Assert.notNull(sc, "ServletContext must not be null");
        final Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw (RuntimeException)attr;
        }
        if (attr instanceof Error) {
            throw (Error)attr;
        }
        if (attr instanceof Exception) {
            throw new IllegalStateException((Throwable)attr);
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
        }
        return (WebApplicationContext)attr;
    }
    
    public static void registerWebApplicationScopes(final ConfigurableListableBeanFactory beanFactory) {
        registerWebApplicationScopes(beanFactory, null);
    }
    
    public static void registerWebApplicationScopes(final ConfigurableListableBeanFactory beanFactory, final ServletContext sc) {
        beanFactory.registerScope("request", new RequestScope());
        beanFactory.registerScope("session", new SessionScope(false));
        beanFactory.registerScope("globalSession", new SessionScope(true));
        if (sc != null) {
            final ServletContextScope appScope = new ServletContextScope(sc);
            beanFactory.registerScope("application", appScope);
            sc.setAttribute(ServletContextScope.class.getName(), (Object)appScope);
        }
        beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
        beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
        beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
        if (WebApplicationContextUtils.jsfPresent) {
            FacesDependencyRegistrar.registerFacesDependencies(beanFactory);
        }
    }
    
    public static void registerEnvironmentBeans(final ConfigurableListableBeanFactory bf, final ServletContext sc) {
        registerEnvironmentBeans(bf, sc, null);
    }
    
    public static void registerEnvironmentBeans(final ConfigurableListableBeanFactory bf, final ServletContext sc, final ServletConfig config) {
        if (sc != null && !bf.containsBean("servletContext")) {
            bf.registerSingleton("servletContext", sc);
        }
        if (config != null && !bf.containsBean("servletConfig")) {
            bf.registerSingleton("servletConfig", config);
        }
        if (!bf.containsBean("contextParameters")) {
            final Map<String, String> parameterMap = new HashMap<String, String>();
            if (sc != null) {
                final Enumeration<?> paramNameEnum = (Enumeration<?>)sc.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    final String paramName = (String)paramNameEnum.nextElement();
                    parameterMap.put(paramName, sc.getInitParameter(paramName));
                }
            }
            if (config != null) {
                final Enumeration<?> paramNameEnum = (Enumeration<?>)config.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    final String paramName = (String)paramNameEnum.nextElement();
                    parameterMap.put(paramName, config.getInitParameter(paramName));
                }
            }
            bf.registerSingleton("contextParameters", Collections.unmodifiableMap((Map<?, ?>)parameterMap));
        }
        if (!bf.containsBean("contextAttributes")) {
            final Map<String, Object> attributeMap = new HashMap<String, Object>();
            if (sc != null) {
                final Enumeration<?> attrNameEnum = (Enumeration<?>)sc.getAttributeNames();
                while (attrNameEnum.hasMoreElements()) {
                    final String attrName = (String)attrNameEnum.nextElement();
                    attributeMap.put(attrName, sc.getAttribute(attrName));
                }
            }
            bf.registerSingleton("contextAttributes", Collections.unmodifiableMap((Map<?, ?>)attributeMap));
        }
    }
    
    public static void initServletPropertySources(final MutablePropertySources propertySources, final ServletContext servletContext) {
        initServletPropertySources(propertySources, servletContext, null);
    }
    
    public static void initServletPropertySources(final MutablePropertySources propertySources, final ServletContext servletContext, final ServletConfig servletConfig) {
        Assert.notNull(propertySources, "propertySources must not be null");
        if (servletContext != null && propertySources.contains("servletContextInitParams") && propertySources.get("servletContextInitParams") instanceof PropertySource.StubPropertySource) {
            propertySources.replace("servletContextInitParams", new ServletContextPropertySource("servletContextInitParams", servletContext));
        }
        if (servletConfig != null && propertySources.contains("servletConfigInitParams") && propertySources.get("servletConfigInitParams") instanceof PropertySource.StubPropertySource) {
            propertySources.replace("servletConfigInitParams", new ServletConfigPropertySource("servletConfigInitParams", servletConfig));
        }
    }
    
    private static ServletRequestAttributes currentRequestAttributes() {
        final RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
        if (!(requestAttr instanceof ServletRequestAttributes)) {
            throw new IllegalStateException("Current request is not a servlet request");
        }
        return (ServletRequestAttributes)requestAttr;
    }
    
    static {
        jsfPresent = ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());
    }
    
    private static class RequestObjectFactory implements ObjectFactory<ServletRequest>, Serializable
    {
        @Override
        public ServletRequest getObject() {
            return (ServletRequest)currentRequestAttributes().getRequest();
        }
        
        @Override
        public String toString() {
            return "Current HttpServletRequest";
        }
    }
    
    private static class SessionObjectFactory implements ObjectFactory<HttpSession>, Serializable
    {
        @Override
        public HttpSession getObject() {
            return currentRequestAttributes().getRequest().getSession();
        }
        
        @Override
        public String toString() {
            return "Current HttpSession";
        }
    }
    
    private static class WebRequestObjectFactory implements ObjectFactory<WebRequest>, Serializable
    {
        @Override
        public WebRequest getObject() {
            return new ServletWebRequest(currentRequestAttributes().getRequest());
        }
        
        @Override
        public String toString() {
            return "Current ServletWebRequest";
        }
    }
    
    private static class FacesDependencyRegistrar
    {
        public static void registerFacesDependencies(final ConfigurableListableBeanFactory beanFactory) {
            beanFactory.registerResolvableDependency(FacesContext.class, new ObjectFactory<FacesContext>() {
                @Override
                public FacesContext getObject() {
                    return FacesContext.getCurrentInstance();
                }
                
                @Override
                public String toString() {
                    return "Current JSF FacesContext";
                }
            });
            beanFactory.registerResolvableDependency(ExternalContext.class, new ObjectFactory<ExternalContext>() {
                @Override
                public ExternalContext getObject() {
                    return FacesContext.getCurrentInstance().getExternalContext();
                }
                
                @Override
                public String toString() {
                    return "Current JSF ExternalContext";
                }
            });
        }
    }
}
