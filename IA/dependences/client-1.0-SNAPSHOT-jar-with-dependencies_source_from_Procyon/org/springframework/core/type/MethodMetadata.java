// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type;

public interface MethodMetadata extends AnnotatedTypeMetadata
{
    String getMethodName();
    
    String getDeclaringClassName();
    
    boolean isStatic();
    
    boolean isFinal();
    
    boolean isOverridable();
}
