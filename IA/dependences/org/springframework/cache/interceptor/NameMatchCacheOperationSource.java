// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.logging.Log;
import java.io.Serializable;

public class NameMatchCacheOperationSource implements CacheOperationSource, Serializable
{
    protected static final Log logger;
    private Map<String, Collection<CacheOperation>> nameMap;
    
    public NameMatchCacheOperationSource() {
        this.nameMap = new LinkedHashMap<String, Collection<CacheOperation>>();
    }
    
    public void setNameMap(final Map<String, Collection<CacheOperation>> nameMap) {
        for (final Map.Entry<String, Collection<CacheOperation>> entry : nameMap.entrySet()) {
            this.addCacheMethod(entry.getKey(), entry.getValue());
        }
    }
    
    public void addCacheMethod(final String methodName, final Collection<CacheOperation> ops) {
        if (NameMatchCacheOperationSource.logger.isDebugEnabled()) {
            NameMatchCacheOperationSource.logger.debug("Adding method [" + methodName + "] with cache operations [" + ops + "]");
        }
        this.nameMap.put(methodName, ops);
    }
    
    @Override
    public Collection<CacheOperation> getCacheOperations(final Method method, final Class<?> targetClass) {
        final String methodName = method.getName();
        Collection<CacheOperation> ops = this.nameMap.get(methodName);
        if (ops == null) {
            String bestNameMatch = null;
            for (final String mappedName : this.nameMap.keySet()) {
                if (this.isMatch(methodName, mappedName) && (bestNameMatch == null || bestNameMatch.length() <= mappedName.length())) {
                    ops = this.nameMap.get(mappedName);
                    bestNameMatch = mappedName;
                }
            }
        }
        return ops;
    }
    
    protected boolean isMatch(final String methodName, final String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NameMatchCacheOperationSource)) {
            return false;
        }
        final NameMatchCacheOperationSource otherTas = (NameMatchCacheOperationSource)other;
        return ObjectUtils.nullSafeEquals(this.nameMap, otherTas.nameMap);
    }
    
    @Override
    public int hashCode() {
        return NameMatchCacheOperationSource.class.hashCode();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + this.nameMap;
    }
    
    static {
        logger = LogFactory.getLog(NameMatchCacheOperationSource.class);
    }
}
