// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.util.ObjectUtils;
import java.io.Serializable;
import org.springframework.aop.TargetSource;

public class EmptyTargetSource implements TargetSource, Serializable
{
    private static final long serialVersionUID = 3680494563553489691L;
    public static final EmptyTargetSource INSTANCE;
    private final Class<?> targetClass;
    private final boolean isStatic;
    
    public static EmptyTargetSource forClass(final Class<?> targetClass) {
        return forClass(targetClass, true);
    }
    
    public static EmptyTargetSource forClass(final Class<?> targetClass, final boolean isStatic) {
        return (targetClass == null && isStatic) ? EmptyTargetSource.INSTANCE : new EmptyTargetSource(targetClass, isStatic);
    }
    
    private EmptyTargetSource(final Class<?> targetClass, final boolean isStatic) {
        this.targetClass = targetClass;
        this.isStatic = isStatic;
    }
    
    @Override
    public Class<?> getTargetClass() {
        return this.targetClass;
    }
    
    @Override
    public boolean isStatic() {
        return this.isStatic;
    }
    
    @Override
    public Object getTarget() {
        return null;
    }
    
    @Override
    public void releaseTarget(final Object target) {
    }
    
    private Object readResolve() {
        return (this.targetClass == null && this.isStatic) ? EmptyTargetSource.INSTANCE : this;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmptyTargetSource)) {
            return false;
        }
        final EmptyTargetSource otherTs = (EmptyTargetSource)other;
        return ObjectUtils.nullSafeEquals(this.targetClass, otherTs.targetClass) && this.isStatic == otherTs.isStatic;
    }
    
    @Override
    public int hashCode() {
        return EmptyTargetSource.class.hashCode() * 13 + ObjectUtils.nullSafeHashCode(this.targetClass);
    }
    
    @Override
    public String toString() {
        return "EmptyTargetSource: " + ((this.targetClass != null) ? ("target class [" + this.targetClass.getName() + "]") : "no target class") + ", " + (this.isStatic ? "static" : "dynamic");
    }
    
    static {
        INSTANCE = new EmptyTargetSource(null, true);
    }
}
