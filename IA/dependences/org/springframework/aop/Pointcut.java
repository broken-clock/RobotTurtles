// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

public interface Pointcut
{
    public static final Pointcut TRUE = TruePointcut.INSTANCE;
    
    ClassFilter getClassFilter();
    
    MethodMatcher getMethodMatcher();
}
