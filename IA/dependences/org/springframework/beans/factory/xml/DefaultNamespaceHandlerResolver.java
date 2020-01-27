// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import java.util.Properties;
import java.io.IOException;
import org.springframework.util.CollectionUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.commons.logging.Log;

public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver
{
    public static final String DEFAULT_HANDLER_MAPPINGS_LOCATION = "META-INF/spring.handlers";
    protected final Log logger;
    private final ClassLoader classLoader;
    private final String handlerMappingsLocation;
    private volatile Map<String, Object> handlerMappings;
    
    public DefaultNamespaceHandlerResolver() {
        this(null, "META-INF/spring.handlers");
    }
    
    public DefaultNamespaceHandlerResolver(final ClassLoader classLoader) {
        this(classLoader, "META-INF/spring.handlers");
    }
    
    public DefaultNamespaceHandlerResolver(final ClassLoader classLoader, final String handlerMappingsLocation) {
        this.logger = LogFactory.getLog(this.getClass());
        Assert.notNull(handlerMappingsLocation, "Handler mappings location must not be null");
        this.classLoader = ((classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader());
        this.handlerMappingsLocation = handlerMappingsLocation;
    }
    
    @Override
    public NamespaceHandler resolve(final String namespaceUri) {
        final Map<String, Object> handlerMappings = this.getHandlerMappings();
        final Object handlerOrClassName = handlerMappings.get(namespaceUri);
        if (handlerOrClassName == null) {
            return null;
        }
        if (handlerOrClassName instanceof NamespaceHandler) {
            return (NamespaceHandler)handlerOrClassName;
        }
        final String className = (String)handlerOrClassName;
        try {
            final Class<?> handlerClass = ClassUtils.forName(className, this.classLoader);
            if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
                throw new FatalBeanException("Class [" + className + "] for namespace [" + namespaceUri + "] does not implement the [" + NamespaceHandler.class.getName() + "] interface");
            }
            final NamespaceHandler namespaceHandler = BeanUtils.instantiateClass(handlerClass);
            namespaceHandler.init();
            handlerMappings.put(namespaceUri, namespaceHandler);
            return namespaceHandler;
        }
        catch (ClassNotFoundException ex) {
            throw new FatalBeanException("NamespaceHandler class [" + className + "] for namespace [" + namespaceUri + "] not found", ex);
        }
        catch (LinkageError err) {
            throw new FatalBeanException("Invalid NamespaceHandler class [" + className + "] for namespace [" + namespaceUri + "]: problem with handler class file or dependent class", err);
        }
    }
    
    private Map<String, Object> getHandlerMappings() {
        if (this.handlerMappings == null) {
            synchronized (this) {
                if (this.handlerMappings == null) {
                    try {
                        final Properties mappings = PropertiesLoaderUtils.loadAllProperties(this.handlerMappingsLocation, this.classLoader);
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Loaded NamespaceHandler mappings: " + mappings);
                        }
                        final Map<String, Object> handlerMappings = new ConcurrentHashMap<String, Object>(mappings.size());
                        CollectionUtils.mergePropertiesIntoMap(mappings, handlerMappings);
                        this.handlerMappings = handlerMappings;
                    }
                    catch (IOException ex) {
                        throw new IllegalStateException("Unable to load NamespaceHandler mappings from location [" + this.handlerMappingsLocation + "]", ex);
                    }
                }
            }
        }
        return this.handlerMappings;
    }
    
    @Override
    public String toString() {
        return "NamespaceHandlerResolver using mappings " + this.getHandlerMappings();
    }
}
