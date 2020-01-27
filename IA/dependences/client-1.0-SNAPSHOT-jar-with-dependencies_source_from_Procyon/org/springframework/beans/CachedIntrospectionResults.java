// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.util.WeakHashMap;
import java.util.HashSet;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.SpringProperties;
import org.springframework.util.StringUtils;
import java.beans.IntrospectionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import java.beans.Introspector;
import java.lang.ref.WeakReference;
import org.springframework.util.ClassUtils;
import java.lang.ref.Reference;
import java.util.Iterator;
import org.springframework.core.convert.TypeDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import java.util.List;

public class CachedIntrospectionResults
{
    public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";
    private static final boolean shouldIntrospectorIgnoreBeaninfoClasses;
    private static List<BeanInfoFactory> beanInfoFactories;
    private static final Log logger;
    static final Set<ClassLoader> acceptedClassLoaders;
    static final Map<Class<?>, Object> classCache;
    private final BeanInfo beanInfo;
    private final Map<String, PropertyDescriptor> propertyDescriptorCache;
    private final Map<PropertyDescriptor, TypeDescriptor> typeDescriptorCache;
    
    public static void acceptClassLoader(final ClassLoader classLoader) {
        if (classLoader != null) {
            synchronized (CachedIntrospectionResults.acceptedClassLoaders) {
                CachedIntrospectionResults.acceptedClassLoaders.add(classLoader);
            }
        }
    }
    
    public static void clearClassLoader(final ClassLoader classLoader) {
        synchronized (CachedIntrospectionResults.classCache) {
            final Iterator<Class<?>> it = CachedIntrospectionResults.classCache.keySet().iterator();
            while (it.hasNext()) {
                final Class<?> beanClass = it.next();
                if (isUnderneathClassLoader(beanClass.getClassLoader(), classLoader)) {
                    it.remove();
                }
            }
        }
        synchronized (CachedIntrospectionResults.acceptedClassLoaders) {
            final Iterator<ClassLoader> it2 = CachedIntrospectionResults.acceptedClassLoaders.iterator();
            while (it2.hasNext()) {
                final ClassLoader registeredLoader = it2.next();
                if (isUnderneathClassLoader(registeredLoader, classLoader)) {
                    it2.remove();
                }
            }
        }
    }
    
    static CachedIntrospectionResults forClass(final Class<?> beanClass) throws BeansException {
        final Object value;
        synchronized (CachedIntrospectionResults.classCache) {
            value = CachedIntrospectionResults.classCache.get(beanClass);
        }
        CachedIntrospectionResults results;
        if (value instanceof Reference) {
            final Reference<CachedIntrospectionResults> ref = (Reference<CachedIntrospectionResults>)value;
            results = ref.get();
        }
        else {
            results = (CachedIntrospectionResults)value;
        }
        if (results == null) {
            if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) || isClassLoaderAccepted(beanClass.getClassLoader())) {
                results = new CachedIntrospectionResults(beanClass);
                synchronized (CachedIntrospectionResults.classCache) {
                    CachedIntrospectionResults.classCache.put(beanClass, results);
                }
            }
            else {
                if (CachedIntrospectionResults.logger.isDebugEnabled()) {
                    CachedIntrospectionResults.logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
                }
                results = new CachedIntrospectionResults(beanClass);
                synchronized (CachedIntrospectionResults.classCache) {
                    CachedIntrospectionResults.classCache.put(beanClass, new WeakReference(results));
                }
            }
        }
        return results;
    }
    
    private static boolean isClassLoaderAccepted(final ClassLoader classLoader) {
        final ClassLoader[] acceptedLoaderArray;
        synchronized (CachedIntrospectionResults.acceptedClassLoaders) {
            acceptedLoaderArray = CachedIntrospectionResults.acceptedClassLoaders.toArray(new ClassLoader[CachedIntrospectionResults.acceptedClassLoaders.size()]);
        }
        for (final ClassLoader acceptedLoader : acceptedLoaderArray) {
            if (isUnderneathClassLoader(classLoader, acceptedLoader)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isUnderneathClassLoader(final ClassLoader candidate, final ClassLoader parent) {
        if (candidate == parent) {
            return true;
        }
        if (candidate == null) {
            return false;
        }
        ClassLoader classLoaderToCheck = candidate;
        while (classLoaderToCheck != null) {
            classLoaderToCheck = classLoaderToCheck.getParent();
            if (classLoaderToCheck == parent) {
                return true;
            }
        }
        return false;
    }
    
    private CachedIntrospectionResults(final Class<?> beanClass) throws BeansException {
        try {
            if (CachedIntrospectionResults.logger.isTraceEnabled()) {
                CachedIntrospectionResults.logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
            }
            BeanInfo beanInfo = null;
            for (final BeanInfoFactory beanInfoFactory : CachedIntrospectionResults.beanInfoFactories) {
                beanInfo = beanInfoFactory.getBeanInfo(beanClass);
                if (beanInfo != null) {
                    break;
                }
            }
            if (beanInfo == null) {
                beanInfo = (CachedIntrospectionResults.shouldIntrospectorIgnoreBeaninfoClasses ? Introspector.getBeanInfo(beanClass, 3) : Introspector.getBeanInfo(beanClass));
            }
            this.beanInfo = beanInfo;
            if (CachedIntrospectionResults.logger.isTraceEnabled()) {
                CachedIntrospectionResults.logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
            }
            this.propertyDescriptorCache = new LinkedHashMap<String, PropertyDescriptor>();
            final PropertyDescriptor[] propertyDescriptors;
            final PropertyDescriptor[] pds = propertyDescriptors = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                Label_0425: {
                    if (Class.class.equals(beanClass)) {
                        if ("classLoader".equals(pd.getName())) {
                            break Label_0425;
                        }
                        if ("protectionDomain".equals(pd.getName())) {
                            break Label_0425;
                        }
                    }
                    if (CachedIntrospectionResults.logger.isTraceEnabled()) {
                        CachedIntrospectionResults.logger.trace("Found bean property '" + pd.getName() + "'" + ((pd.getPropertyType() != null) ? (" of type [" + pd.getPropertyType().getName() + "]") : "") + ((pd.getPropertyEditorClass() != null) ? ("; editor [" + pd.getPropertyEditorClass().getName() + "]") : ""));
                    }
                    pd = this.buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                    this.propertyDescriptorCache.put(pd.getName(), pd);
                }
            }
            this.typeDescriptorCache = new ConcurrentHashMap<PropertyDescriptor, TypeDescriptor>();
        }
        catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }
    
    BeanInfo getBeanInfo() {
        return this.beanInfo;
    }
    
    Class<?> getBeanClass() {
        return this.beanInfo.getBeanDescriptor().getBeanClass();
    }
    
    PropertyDescriptor getPropertyDescriptor(final String name) {
        PropertyDescriptor pd = this.propertyDescriptorCache.get(name);
        if (pd == null && StringUtils.hasLength(name)) {
            pd = this.propertyDescriptorCache.get(name.substring(0, 1).toLowerCase() + name.substring(1));
            if (pd == null) {
                pd = this.propertyDescriptorCache.get(name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        return (pd == null || pd instanceof GenericTypeAwarePropertyDescriptor) ? pd : this.buildGenericTypeAwarePropertyDescriptor(this.getBeanClass(), pd);
    }
    
    PropertyDescriptor[] getPropertyDescriptors() {
        final PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
        int i = 0;
        for (final PropertyDescriptor pd : this.propertyDescriptorCache.values()) {
            pds[i] = ((pd instanceof GenericTypeAwarePropertyDescriptor) ? pd : this.buildGenericTypeAwarePropertyDescriptor(this.getBeanClass(), pd));
            ++i;
        }
        return pds;
    }
    
    private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(final Class<?> beanClass, final PropertyDescriptor pd) {
        try {
            return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(), pd.getWriteMethod(), pd.getPropertyEditorClass());
        }
        catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
        }
    }
    
    void addTypeDescriptor(final PropertyDescriptor pd, final TypeDescriptor td) {
        this.typeDescriptorCache.put(pd, td);
    }
    
    TypeDescriptor getTypeDescriptor(final PropertyDescriptor pd) {
        return this.typeDescriptorCache.get(pd);
    }
    
    static {
        shouldIntrospectorIgnoreBeaninfoClasses = SpringProperties.getFlag("spring.beaninfo.ignore");
        CachedIntrospectionResults.beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());
        logger = LogFactory.getLog(CachedIntrospectionResults.class);
        acceptedClassLoaders = new HashSet<ClassLoader>();
        classCache = new WeakHashMap<Class<?>, Object>();
    }
}
