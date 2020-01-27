// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import java.lang.reflect.Method;

public interface IntroductionAwareMethodMatcher extends MethodMatcher
{
    boolean matches(final Method p0, final Class<?> p1, final boolean p2);
}
