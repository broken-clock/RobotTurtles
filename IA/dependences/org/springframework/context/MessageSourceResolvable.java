// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

public interface MessageSourceResolvable
{
    String[] getCodes();
    
    Object[] getArguments();
    
    String getDefaultMessage();
}
