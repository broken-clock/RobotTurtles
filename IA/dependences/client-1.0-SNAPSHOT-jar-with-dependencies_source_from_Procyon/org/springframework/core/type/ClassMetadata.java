// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type;

public interface ClassMetadata
{
    String getClassName();
    
    boolean isInterface();
    
    boolean isAbstract();
    
    boolean isConcrete();
    
    boolean isFinal();
    
    boolean isIndependent();
    
    boolean hasEnclosingClass();
    
    String getEnclosingClassName();
    
    boolean hasSuperClass();
    
    String getSuperClassName();
    
    String[] getInterfaceNames();
    
    String[] getMemberClassNames();
}
