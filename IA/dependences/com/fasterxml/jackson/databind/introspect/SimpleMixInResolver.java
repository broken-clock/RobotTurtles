// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.util.Iterator;
import java.util.HashMap;
import com.fasterxml.jackson.databind.type.ClassKey;
import java.util.Map;
import java.io.Serializable;

public class SimpleMixInResolver implements ClassIntrospector.MixInResolver, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final ClassIntrospector.MixInResolver _overrides;
    protected Map<ClassKey, Class<?>> _localMixIns;
    
    public SimpleMixInResolver(final ClassIntrospector.MixInResolver overrides) {
        this._overrides = overrides;
    }
    
    protected SimpleMixInResolver(final ClassIntrospector.MixInResolver overrides, final Map<ClassKey, Class<?>> mixins) {
        this._overrides = overrides;
        this._localMixIns = mixins;
    }
    
    public SimpleMixInResolver withOverrides(final ClassIntrospector.MixInResolver overrides) {
        return new SimpleMixInResolver(overrides, this._localMixIns);
    }
    
    public SimpleMixInResolver withoutLocalDefinitions() {
        return new SimpleMixInResolver(this._overrides, null);
    }
    
    public void setLocalDefinitions(final Map<Class<?>, Class<?>> sourceMixins) {
        if (sourceMixins == null || sourceMixins.isEmpty()) {
            this._localMixIns = null;
        }
        else {
            final Map<ClassKey, Class<?>> mixIns = new HashMap<ClassKey, Class<?>>(sourceMixins.size());
            for (final Map.Entry<Class<?>, Class<?>> en : sourceMixins.entrySet()) {
                mixIns.put(new ClassKey(en.getKey()), en.getValue());
            }
            this._localMixIns = mixIns;
        }
    }
    
    public void addLocalDefinition(final Class<?> target, final Class<?> mixinSource) {
        if (this._localMixIns == null) {
            this._localMixIns = new HashMap<ClassKey, Class<?>>();
        }
        this._localMixIns.put(new ClassKey(target), mixinSource);
    }
    
    @Override
    public SimpleMixInResolver copy() {
        final ClassIntrospector.MixInResolver overrides = (this._overrides == null) ? null : this._overrides.copy();
        final Map<ClassKey, Class<?>> mixIns = (this._localMixIns == null) ? null : new HashMap<ClassKey, Class<?>>(this._localMixIns);
        return new SimpleMixInResolver(overrides, mixIns);
    }
    
    @Override
    public Class<?> findMixInClassFor(final Class<?> cls) {
        Class<?> mixin = (this._overrides == null) ? null : this._overrides.findMixInClassFor(cls);
        if (mixin == null && this._localMixIns != null) {
            mixin = this._localMixIns.get(new ClassKey(cls));
        }
        return mixin;
    }
    
    public int localSize() {
        return (this._localMixIns == null) ? 0 : this._localMixIns.size();
    }
}
