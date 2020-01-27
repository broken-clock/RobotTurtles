// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.util.StringUtils;
import org.springframework.util.ClassUtils;
import java.util.Iterator;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.core.GenericTypeResolver;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import java.util.ArrayList;
import org.springframework.core.env.ConfigurableEnvironment;
import javax.servlet.ServletConfig;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.access.BeanFactoryReference;
import java.util.Map;
import java.util.Properties;

public class ContextLoader
{
    public static final String CONTEXT_ID_PARAM = "contextId";
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    public static final String CONTEXT_CLASS_PARAM = "contextClass";
    public static final String CONTEXT_INITIALIZER_CLASSES_PARAM = "contextInitializerClasses";
    public static final String GLOBAL_INITIALIZER_CLASSES_PARAM = "globalInitializerClasses";
    public static final String LOCATOR_FACTORY_SELECTOR_PARAM = "locatorFactorySelector";
    public static final String LOCATOR_FACTORY_KEY_PARAM = "parentContextKey";
    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";
    private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";
    private static final Properties defaultStrategies;
    private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread;
    private static volatile WebApplicationContext currentContext;
    private WebApplicationContext context;
    private BeanFactoryReference parentContextRef;
    
    public ContextLoader() {
    }
    
    public ContextLoader(final WebApplicationContext context) {
        this.context = context;
    }
    
    public WebApplicationContext initWebApplicationContext(final ServletContext servletContext) {
        if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
            throw new IllegalStateException("Cannot initialize context because there is already a root application context present - check whether you have multiple ContextLoader* definitions in your web.xml!");
        }
        final Log logger = LogFactory.getLog(ContextLoader.class);
        servletContext.log("Initializing Spring root WebApplicationContext");
        if (logger.isInfoEnabled()) {
            logger.info("Root WebApplicationContext: initialization started");
        }
        final long startTime = System.currentTimeMillis();
        try {
            if (this.context == null) {
                this.context = this.createWebApplicationContext(servletContext);
            }
            if (this.context instanceof ConfigurableWebApplicationContext) {
                final ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext)this.context;
                if (!cwac.isActive()) {
                    if (cwac.getParent() == null) {
                        final ApplicationContext parent = this.loadParentContext(servletContext);
                        cwac.setParent(parent);
                    }
                    this.configureAndRefreshWebApplicationContext(cwac, servletContext);
                }
            }
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, (Object)this.context);
            final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            if (ccl == ContextLoader.class.getClassLoader()) {
                ContextLoader.currentContext = this.context;
            }
            else if (ccl != null) {
                ContextLoader.currentContextPerThread.put(ccl, this.context);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Published root WebApplicationContext as ServletContext attribute with name [" + WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");
            }
            if (logger.isInfoEnabled()) {
                final long elapsedTime = System.currentTimeMillis() - startTime;
                logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
            }
            return this.context;
        }
        catch (RuntimeException ex) {
            logger.error("Context initialization failed", ex);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, (Object)ex);
            throw ex;
        }
        catch (Error err) {
            logger.error("Context initialization failed", err);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, (Object)err);
            throw err;
        }
    }
    
    protected WebApplicationContext createWebApplicationContext(final ServletContext sc) {
        final Class<?> contextClass = this.determineContextClass(sc);
        if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new ApplicationContextException("Custom context class [" + contextClass.getName() + "] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
        }
        return BeanUtils.instantiateClass(contextClass);
    }
    
    @Deprecated
    protected WebApplicationContext createWebApplicationContext(final ServletContext sc, final ApplicationContext parent) {
        return this.createWebApplicationContext(sc);
    }
    
    protected void configureAndRefreshWebApplicationContext(final ConfigurableWebApplicationContext wac, final ServletContext sc) {
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            final String idParam = sc.getInitParameter("contextId");
            if (idParam != null) {
                wac.setId(idParam);
            }
            else {
                wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + ObjectUtils.getDisplayString(sc.getContextPath()));
            }
        }
        wac.setServletContext(sc);
        final String configLocationParam = sc.getInitParameter("contextConfigLocation");
        if (configLocationParam != null) {
            wac.setConfigLocation(configLocationParam);
        }
        final ConfigurableEnvironment env = wac.getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment)env).initPropertySources(sc, null);
        }
        this.customizeContext(sc, wac);
        wac.refresh();
    }
    
    protected void customizeContext(final ServletContext sc, final ConfigurableWebApplicationContext wac) {
        final List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> initializerClasses = this.determineContextInitializerClasses(sc);
        if (initializerClasses.isEmpty()) {
            return;
        }
        final ArrayList<ApplicationContextInitializer<ConfigurableApplicationContext>> initializerInstances = new ArrayList<ApplicationContextInitializer<ConfigurableApplicationContext>>();
        for (final Class<ApplicationContextInitializer<ConfigurableApplicationContext>> initializerClass : initializerClasses) {
            final Class<?> initializerContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
            if (initializerContextClass != null) {
                Assert.isAssignable(initializerContextClass, wac.getClass(), String.format("Could not add context initializer [%s] since its generic parameter [%s] is not assignable from the type of application context used by this context loader [%s]: ", initializerClass.getName(), initializerContextClass.getName(), wac.getClass().getName()));
            }
            initializerInstances.add(BeanUtils.instantiateClass(initializerClass));
        }
        AnnotationAwareOrderComparator.sort(initializerInstances);
        for (final ApplicationContextInitializer<ConfigurableApplicationContext> initializer : initializerInstances) {
            initializer.initialize(wac);
        }
    }
    
    protected Class<?> determineContextClass(final ServletContext servletContext) {
        String contextClassName = servletContext.getInitParameter("contextClass");
        if (contextClassName != null) {
            try {
                return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
            }
            catch (ClassNotFoundException ex) {
                throw new ApplicationContextException("Failed to load custom context class [" + contextClassName + "]", ex);
            }
        }
        contextClassName = ContextLoader.defaultStrategies.getProperty(WebApplicationContext.class.getName());
        try {
            return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load default context class [" + contextClassName + "]", ex);
        }
    }
    
    protected List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> determineContextInitializerClasses(final ServletContext servletContext) {
        final List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> classes = new ArrayList<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>>();
        final String globalClassNames = servletContext.getInitParameter("globalInitializerClasses");
        if (globalClassNames != null) {
            for (final String className : StringUtils.tokenizeToStringArray(globalClassNames, ",; \t\n")) {
                classes.add(this.loadInitializerClass(className));
            }
        }
        final String localClassNames = servletContext.getInitParameter("contextInitializerClasses");
        if (localClassNames != null) {
            for (final String className2 : StringUtils.tokenizeToStringArray(localClassNames, ",; \t\n")) {
                classes.add(this.loadInitializerClass(className2));
            }
        }
        return classes;
    }
    
    private Class<ApplicationContextInitializer<ConfigurableApplicationContext>> loadInitializerClass(final String className) {
        try {
            final Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
            Assert.isAssignable(ApplicationContextInitializer.class, clazz);
            return (Class<ApplicationContextInitializer<ConfigurableApplicationContext>>)clazz;
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load context initializer class [" + className + "]", ex);
        }
    }
    
    protected ApplicationContext loadParentContext(final ServletContext servletContext) {
        ApplicationContext parentContext = null;
        final String locatorFactorySelector = servletContext.getInitParameter("locatorFactorySelector");
        final String parentContextKey = servletContext.getInitParameter("parentContextKey");
        if (parentContextKey != null) {
            final BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locatorFactorySelector);
            final Log logger = LogFactory.getLog(ContextLoader.class);
            if (logger.isDebugEnabled()) {
                logger.debug("Getting parent context definition: using parent context key of '" + parentContextKey + "' with BeanFactoryLocator");
            }
            this.parentContextRef = locator.useBeanFactory(parentContextKey);
            parentContext = (ApplicationContext)this.parentContextRef.getFactory();
        }
        return parentContext;
    }
    
    public void closeWebApplicationContext(final ServletContext servletContext) {
        servletContext.log("Closing Spring root WebApplicationContext");
        try {
            if (this.context instanceof ConfigurableWebApplicationContext) {
                ((ConfigurableWebApplicationContext)this.context).close();
            }
        }
        finally {
            final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            if (ccl == ContextLoader.class.getClassLoader()) {
                ContextLoader.currentContext = null;
            }
            else if (ccl != null) {
                ContextLoader.currentContextPerThread.remove(ccl);
            }
            servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (this.parentContextRef != null) {
                this.parentContextRef.release();
            }
        }
    }
    
    public static WebApplicationContext getCurrentWebApplicationContext() {
        final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl != null) {
            final WebApplicationContext ccpt = ContextLoader.currentContextPerThread.get(ccl);
            if (ccpt != null) {
                return ccpt;
            }
        }
        return ContextLoader.currentContext;
    }
    
    static {
        try {
            final ClassPathResource resource = new ClassPathResource("ContextLoader.properties", ContextLoader.class);
            defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
        }
        currentContextPerThread = new ConcurrentHashMap<ClassLoader, WebApplicationContext>(1);
    }
}
