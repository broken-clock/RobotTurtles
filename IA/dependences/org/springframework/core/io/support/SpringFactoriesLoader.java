// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;
import java.util.Properties;
import java.util.Enumeration;
import java.io.IOException;
import java.util.Collection;
import java.util.Arrays;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.URL;
import java.util.Iterator;
import org.springframework.core.OrderComparator;
import java.util.ArrayList;
import org.springframework.util.Assert;
import java.util.List;
import org.apache.commons.logging.Log;

public abstract class SpringFactoriesLoader
{
    private static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    private static final Log logger;
    
    public static <T> List<T> loadFactories(final Class<T> factoryClass, ClassLoader classLoader) {
        Assert.notNull(factoryClass, "'factoryClass' must not be null");
        if (classLoader == null) {
            classLoader = SpringFactoriesLoader.class.getClassLoader();
        }
        final List<String> factoryNames = loadFactoryNames(factoryClass, classLoader);
        if (SpringFactoriesLoader.logger.isTraceEnabled()) {
            SpringFactoriesLoader.logger.trace("Loaded [" + factoryClass.getName() + "] names: " + factoryNames);
        }
        final List<T> result = new ArrayList<T>(factoryNames.size());
        for (final String factoryName : factoryNames) {
            result.add(instantiateFactory(factoryName, factoryClass, classLoader));
        }
        OrderComparator.sort(result);
        return result;
    }
    
    public static List<String> loadFactoryNames(final Class<?> factoryClass, final ClassLoader classLoader) {
        final String factoryClassName = factoryClass.getName();
        try {
            final List<String> result = new ArrayList<String>();
            final Enumeration<URL> urls = classLoader.getResources("META-INF/spring.factories");
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                final Properties properties = PropertiesLoaderUtils.loadProperties(new UrlResource(url));
                final String factoryClassNames = properties.getProperty(factoryClassName);
                result.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(factoryClassNames)));
            }
            return result;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load [" + factoryClass.getName() + "] factories from location [" + "META-INF/spring.factories" + "]", ex);
        }
    }
    
    private static <T> T instantiateFactory(final String instanceClassName, final Class<T> factoryClass, final ClassLoader classLoader) {
        try {
            final Class<?> instanceClass = ClassUtils.forName(instanceClassName, classLoader);
            if (!factoryClass.isAssignableFrom(instanceClass)) {
                throw new IllegalArgumentException("Class [" + instanceClassName + "] is not assignable to [" + factoryClass.getName() + "]");
            }
            return (T)instanceClass.newInstance();
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("Cannot instantiate factory class: " + factoryClass.getName(), ex);
        }
    }
    
    static {
        logger = LogFactory.getLog(SpringFactoriesLoader.class);
    }
}
