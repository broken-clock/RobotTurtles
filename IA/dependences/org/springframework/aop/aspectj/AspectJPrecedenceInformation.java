// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;

public interface AspectJPrecedenceInformation extends Ordered
{
    String getAspectName();
    
    int getDeclarationOrder();
    
    boolean isBeforeAdvice();
    
    boolean isAfterAdvice();
}
