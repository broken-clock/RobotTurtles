// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.util.UUID;

public class ObjectIdGenerators
{
    private abstract static class Base<T> extends ObjectIdGenerator<T>
    {
        protected final Class<?> _scope;
        
        protected Base(final Class<?> scope) {
            this._scope = scope;
        }
        
        @Override
        public final Class<?> getScope() {
            return this._scope;
        }
        
        @Override
        public boolean canUseFor(final ObjectIdGenerator<?> gen) {
            return gen.getClass() == this.getClass() && gen.getScope() == this._scope;
        }
        
        @Override
        public abstract T generateId(final Object p0);
    }
    
    public abstract static class None extends ObjectIdGenerator<Object>
    {
    }
    
    public abstract static class PropertyGenerator extends Base<Object>
    {
        private static final long serialVersionUID = 1L;
        
        protected PropertyGenerator(final Class<?> scope) {
            super(scope);
        }
    }
    
    public static final class IntSequenceGenerator extends Base<Integer>
    {
        private static final long serialVersionUID = 1L;
        protected transient int _nextValue;
        
        public IntSequenceGenerator() {
            this(Object.class, -1);
        }
        
        public IntSequenceGenerator(final Class<?> scope, final int fv) {
            super(scope);
            this._nextValue = fv;
        }
        
        protected int initialValue() {
            return 1;
        }
        
        @Override
        public ObjectIdGenerator<Integer> forScope(final Class<?> scope) {
            return (this._scope == scope) ? this : new IntSequenceGenerator(scope, this._nextValue);
        }
        
        @Override
        public ObjectIdGenerator<Integer> newForSerialization(final Object context) {
            return new IntSequenceGenerator(this._scope, this.initialValue());
        }
        
        @Override
        public IdKey key(final Object key) {
            if (key == null) {
                return null;
            }
            return new IdKey(this.getClass(), this._scope, key);
        }
        
        @Override
        public Integer generateId(final Object forPojo) {
            if (forPojo == null) {
                return null;
            }
            final int id = this._nextValue;
            ++this._nextValue;
            return id;
        }
    }
    
    public static final class UUIDGenerator extends Base<UUID>
    {
        private static final long serialVersionUID = 1L;
        
        public UUIDGenerator() {
            this(Object.class);
        }
        
        private UUIDGenerator(final Class<?> scope) {
            super(Object.class);
        }
        
        @Override
        public ObjectIdGenerator<UUID> forScope(final Class<?> scope) {
            return this;
        }
        
        @Override
        public ObjectIdGenerator<UUID> newForSerialization(final Object context) {
            return this;
        }
        
        @Override
        public UUID generateId(final Object forPojo) {
            return UUID.randomUUID();
        }
        
        @Override
        public IdKey key(final Object key) {
            if (key == null) {
                return null;
            }
            return new IdKey(this.getClass(), null, key);
        }
        
        @Override
        public boolean canUseFor(final ObjectIdGenerator<?> gen) {
            return gen.getClass() == this.getClass();
        }
    }
    
    public static final class StringIdGenerator extends Base<String>
    {
        private static final long serialVersionUID = 1L;
        
        public StringIdGenerator() {
            this(Object.class);
        }
        
        private StringIdGenerator(final Class<?> scope) {
            super(Object.class);
        }
        
        @Override
        public ObjectIdGenerator<String> forScope(final Class<?> scope) {
            return this;
        }
        
        @Override
        public ObjectIdGenerator<String> newForSerialization(final Object context) {
            return this;
        }
        
        @Override
        public String generateId(final Object forPojo) {
            return UUID.randomUUID().toString();
        }
        
        @Override
        public IdKey key(final Object key) {
            if (key == null) {
                return null;
            }
            return new IdKey(this.getClass(), null, key);
        }
        
        @Override
        public boolean canUseFor(final ObjectIdGenerator<?> gen) {
            return gen instanceof StringIdGenerator;
        }
    }
}
