// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import java.io.Serializable;

class TruePointcut implements Pointcut, Serializable
{
    public static final TruePointcut INSTANCE;
    
    private TruePointcut() {
    }
    
    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }
    
    @Override
    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.TRUE;
    }
    
    private Object readResolve() {
        return TruePointcut.INSTANCE;
    }
    
    @Override
    public String toString() {
        return "Pointcut.TRUE";
    }
    
    static {
        INSTANCE = new TruePointcut();
    }
}
