// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import org.springframework.util.ObjectUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import org.springframework.core.OrderComparator;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.springframework.context.ApplicationEvent;
import java.util.Collection;
import org.springframework.context.ApplicationListener;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanFactory;
import java.util.Map;
import org.springframework.beans.factory.BeanFactoryAware;

public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware
{
    private final ListenerRetriever defaultRetriever;
    private final Map<ListenerCacheKey, ListenerRetriever> retrieverCache;
    private BeanFactory beanFactory;
    
    public AbstractApplicationEventMulticaster() {
        this.defaultRetriever = new ListenerRetriever(false);
        this.retrieverCache = new ConcurrentHashMap<ListenerCacheKey, ListenerRetriever>(64);
    }
    
    @Override
    public void addApplicationListener(final ApplicationListener<?> listener) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListeners.add(listener);
            this.retrieverCache.clear();
        }
    }
    
    @Override
    public void addApplicationListenerBean(final String listenerBeanName) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }
    
    @Override
    public void removeApplicationListener(final ApplicationListener<?> listener) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }
    
    @Override
    public void removeApplicationListenerBean(final String listenerBeanName) {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
            this.retrieverCache.clear();
        }
    }
    
    @Override
    public void removeAllListeners() {
        synchronized (this.defaultRetriever) {
            this.defaultRetriever.applicationListeners.clear();
            this.defaultRetriever.applicationListenerBeans.clear();
            this.retrieverCache.clear();
        }
    }
    
    @Override
    public final void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    private BeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans because it is not associated with a BeanFactory");
        }
        return this.beanFactory;
    }
    
    protected Collection<ApplicationListener<?>> getApplicationListeners() {
        synchronized (this.defaultRetriever) {
            return this.defaultRetriever.getApplicationListeners();
        }
    }
    
    protected Collection<ApplicationListener<?>> getApplicationListeners(final ApplicationEvent event) {
        final Class<? extends ApplicationEvent> eventType = event.getClass();
        final Object source = event.getSource();
        final Class<?> sourceType = (source != null) ? source.getClass() : null;
        final ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);
        ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getApplicationListeners();
        }
        retriever = new ListenerRetriever(true);
        final LinkedList<ApplicationListener<?>> allListeners = new LinkedList<ApplicationListener<?>>();
        final Set<ApplicationListener<?>> listeners;
        final Set<String> listenerBeans;
        synchronized (this.defaultRetriever) {
            listeners = new LinkedHashSet<ApplicationListener<?>>(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet<String>(this.defaultRetriever.applicationListenerBeans);
        }
        for (final ApplicationListener<?> listener : listeners) {
            if (this.supportsEvent(listener, eventType, sourceType)) {
                retriever.applicationListeners.add(listener);
                allListeners.add(listener);
            }
        }
        if (!listenerBeans.isEmpty()) {
            final BeanFactory beanFactory = this.getBeanFactory();
            for (final String listenerBeanName : listenerBeans) {
                try {
                    final ApplicationListener<?> listener2 = beanFactory.getBean(listenerBeanName, (Class<ApplicationListener<?>>)ApplicationListener.class);
                    if (allListeners.contains(listener2) || !this.supportsEvent(listener2, eventType, sourceType)) {
                        continue;
                    }
                    retriever.applicationListenerBeans.add(listenerBeanName);
                    allListeners.add(listener2);
                }
                catch (NoSuchBeanDefinitionException ex) {}
            }
        }
        OrderComparator.sort(allListeners);
        this.retrieverCache.put(cacheKey, retriever);
        return allListeners;
    }
    
    protected boolean supportsEvent(final ApplicationListener<?> listener, final Class<? extends ApplicationEvent> eventType, final Class<?> sourceType) {
        final SmartApplicationListener smartListener = (listener instanceof SmartApplicationListener) ? ((SmartApplicationListener)listener) : new GenericApplicationListenerAdapter(listener);
        return smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType);
    }
    
    private static class ListenerCacheKey
    {
        private final Class<?> eventType;
        private final Class<?> sourceType;
        
        public ListenerCacheKey(final Class<?> eventType, final Class<?> sourceType) {
            this.eventType = eventType;
            this.sourceType = sourceType;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            final ListenerCacheKey otherKey = (ListenerCacheKey)other;
            return ObjectUtils.nullSafeEquals(this.eventType, otherKey.eventType) && ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType);
        }
        
        @Override
        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.eventType) * 29 + ObjectUtils.nullSafeHashCode(this.sourceType);
        }
    }
    
    private class ListenerRetriever
    {
        public final Set<ApplicationListener<?>> applicationListeners;
        public final Set<String> applicationListenerBeans;
        private final boolean preFiltered;
        
        public ListenerRetriever(final boolean preFiltered) {
            this.applicationListeners = new LinkedHashSet<ApplicationListener<?>>();
            this.applicationListenerBeans = new LinkedHashSet<String>();
            this.preFiltered = preFiltered;
        }
        
        public Collection<ApplicationListener<?>> getApplicationListeners() {
            final LinkedList<ApplicationListener<?>> allListeners = new LinkedList<ApplicationListener<?>>();
            for (final ApplicationListener<?> listener : this.applicationListeners) {
                allListeners.add(listener);
            }
            if (!this.applicationListenerBeans.isEmpty()) {
                final BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                for (final String listenerBeanName : this.applicationListenerBeans) {
                    try {
                        final ApplicationListener<?> listener2 = beanFactory.getBean(listenerBeanName, (Class<ApplicationListener<?>>)ApplicationListener.class);
                        if (!this.preFiltered && allListeners.contains(listener2)) {
                            continue;
                        }
                        allListeners.add(listener2);
                    }
                    catch (NoSuchBeanDefinitionException ex) {}
                }
            }
            OrderComparator.sort(allListeners);
            return allListeners;
        }
    }
}
