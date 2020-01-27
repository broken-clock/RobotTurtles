// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.scope;

import org.springframework.aop.RawTargetAccess;

public interface ScopedObject extends RawTargetAccess
{
    Object getTargetObject();
    
    void removeFromScope();
}
