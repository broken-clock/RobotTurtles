// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis;

import org.springframework.objenesis.instantiator.ObjectInstantiator;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.objenesis.strategy.InstantiatorStrategy;

public class ObjenesisBase implements Objenesis
{
    protected final InstantiatorStrategy strategy;
    protected ConcurrentHashMap<String, ObjectInstantiator<?>> cache;
    
    public ObjenesisBase(final InstantiatorStrategy strategy) {
        this(strategy, true);
    }
    
    public ObjenesisBase(final InstantiatorStrategy strategy, final boolean useCache) {
        if (strategy == null) {
            throw new IllegalArgumentException("A strategy can't be null");
        }
        this.strategy = strategy;
        this.cache = (useCache ? new ConcurrentHashMap<String, ObjectInstantiator<?>>() : null);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " using " + this.strategy.getClass().getName() + ((this.cache == null) ? " without" : " with") + " caching";
    }
    
    public <T> T newInstance(final Class<T> clazz) {
        return this.getInstantiatorOf(clazz).newInstance();
    }
    
    public <T> ObjectInstantiator<T> getInstantiatorOf(final Class<T> clazz) {
        if (this.cache == null) {
            return this.strategy.newInstantiatorOf(clazz);
        }
        ObjectInstantiator<?> instantiator = this.cache.get(clazz.getName());
        if (instantiator == null) {
            final ObjectInstantiator<?> newInstantiator = this.strategy.newInstantiatorOf((Class<?>)clazz);
            instantiator = this.cache.putIfAbsent(clazz.getName(), newInstantiator);
            if (instantiator == null) {
                instantiator = newInstantiator;
            }
        }
        return (ObjectInstantiator<T>)instantiator;
    }
}
