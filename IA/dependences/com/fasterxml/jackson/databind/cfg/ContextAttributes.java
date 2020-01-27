// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.cfg;

import java.util.Collections;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Map;

public abstract class ContextAttributes
{
    public static ContextAttributes getEmpty() {
        return Impl.getEmpty();
    }
    
    public abstract ContextAttributes withSharedAttribute(final Object p0, final Object p1);
    
    public abstract ContextAttributes withSharedAttributes(final Map<?, ?> p0);
    
    public abstract ContextAttributes withoutSharedAttribute(final Object p0);
    
    public abstract Object getAttribute(final Object p0);
    
    public abstract ContextAttributes withPerCallAttribute(final Object p0, final Object p1);
    
    public static class Impl extends ContextAttributes implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected static final Impl EMPTY;
        protected static final Object NULL_SURROGATE;
        protected final Map<?, ?> _shared;
        protected transient Map<Object, Object> _nonShared;
        
        protected Impl(final Map<?, ?> shared) {
            this._shared = shared;
            this._nonShared = null;
        }
        
        protected Impl(final Map<?, ?> shared, final Map<Object, Object> nonShared) {
            this._shared = shared;
            this._nonShared = nonShared;
        }
        
        public static ContextAttributes getEmpty() {
            return Impl.EMPTY;
        }
        
        @Override
        public ContextAttributes withSharedAttribute(final Object key, final Object value) {
            Map<Object, Object> m;
            if (this == Impl.EMPTY) {
                m = new HashMap<Object, Object>(8);
            }
            else {
                m = this._copy(this._shared);
            }
            m.put(key, value);
            return new Impl(m);
        }
        
        @Override
        public ContextAttributes withSharedAttributes(final Map<?, ?> shared) {
            return new Impl(shared);
        }
        
        @Override
        public ContextAttributes withoutSharedAttribute(final Object key) {
            if (this._shared.isEmpty()) {
                return this;
            }
            if (!this._shared.containsKey(key)) {
                return this;
            }
            if (this._shared.size() == 1) {
                return Impl.EMPTY;
            }
            final Map<Object, Object> m = this._copy(this._shared);
            m.remove(key);
            return new Impl(m);
        }
        
        @Override
        public Object getAttribute(final Object key) {
            if (this._nonShared != null) {
                final Object ob = this._nonShared.get(key);
                if (ob != null) {
                    if (ob == Impl.NULL_SURROGATE) {
                        return null;
                    }
                    return ob;
                }
            }
            return this._shared.get(key);
        }
        
        @Override
        public ContextAttributes withPerCallAttribute(final Object key, Object value) {
            if (value == null) {
                if (this._shared.containsKey(key)) {
                    value = Impl.NULL_SURROGATE;
                }
                else {
                    if (this._nonShared == null || !this._nonShared.containsKey(key)) {
                        return this;
                    }
                    this._nonShared.remove(key);
                    return this;
                }
            }
            if (this._nonShared == null) {
                return this.nonSharedInstance(key, value);
            }
            this._nonShared.put(key, value);
            return this;
        }
        
        protected ContextAttributes nonSharedInstance(final Object key, Object value) {
            final Map<Object, Object> m = new HashMap<Object, Object>();
            if (value == null) {
                value = Impl.NULL_SURROGATE;
            }
            m.put(key, value);
            return new Impl(this._shared, m);
        }
        
        private Map<Object, Object> _copy(final Map<?, ?> src) {
            return new HashMap<Object, Object>(src);
        }
        
        static {
            EMPTY = new Impl(Collections.emptyMap());
            NULL_SURROGATE = new Object();
        }
    }
}
