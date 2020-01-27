// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.io.Serializable;

public abstract class ObjectIdGenerator<T> implements Serializable
{
    public abstract Class<?> getScope();
    
    public abstract boolean canUseFor(final ObjectIdGenerator<?> p0);
    
    public boolean maySerializeAsObject() {
        return false;
    }
    
    public boolean isValidReferencePropertyName(final String name, final Object parser) {
        return false;
    }
    
    public abstract ObjectIdGenerator<T> forScope(final Class<?> p0);
    
    public abstract ObjectIdGenerator<T> newForSerialization(final Object p0);
    
    public abstract IdKey key(final Object p0);
    
    public abstract T generateId(final Object p0);
    
    public static final class IdKey implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public final Class<?> type;
        public final Class<?> scope;
        public final Object key;
        private final int hashCode;
        
        public IdKey(final Class<?> type, final Class<?> scope, final Object key) {
            if (key == null) {
                throw new IllegalArgumentException("Can not construct IdKey for null key");
            }
            this.type = type;
            this.scope = scope;
            this.key = key;
            int h = key.hashCode() + type.getName().hashCode();
            if (scope != null) {
                h ^= scope.getName().hashCode();
            }
            this.hashCode = h;
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }
            final IdKey other = (IdKey)o;
            return other.key.equals(this.key) && other.type == this.type && other.scope == this.scope;
        }
        
        @Override
        public String toString() {
            return String.format("[ObjectId: key=%s, type=%s, scope=%s]", this.key, (this.type == null) ? "NONE" : this.type.getName(), (this.scope == null) ? "NONE" : this.scope.getName());
        }
    }
}
