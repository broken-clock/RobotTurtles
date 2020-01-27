// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform.impl;

import org.springframework.asm.Type;

public interface InterceptFieldFilter
{
    boolean acceptRead(final Type p0, final String p1);
    
    boolean acceptWrite(final Type p0, final String p1);
}
