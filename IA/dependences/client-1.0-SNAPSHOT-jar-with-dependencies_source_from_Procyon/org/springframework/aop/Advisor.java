// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import org.aopalliance.aop.Advice;

public interface Advisor
{
    Advice getAdvice();
    
    boolean isPerInstance();
}
