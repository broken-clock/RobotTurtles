// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import java.util.List;
import java.lang.reflect.Method;

public interface AdvisorChainFactory
{
    List<Object> getInterceptorsAndDynamicInterceptionAdvice(final Advised p0, final Method p1, final Class<?> p2);
}
