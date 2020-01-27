// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.aop.PointcutAdvisor;

public interface InstantiationModelAwarePointcutAdvisor extends PointcutAdvisor
{
    boolean isLazy();
    
    boolean isAdviceInstantiated();
}
