// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import org.aopalliance.aop.Advice;

public interface DynamicIntroductionAdvice extends Advice
{
    boolean implementsInterface(final Class<?> p0);
}
