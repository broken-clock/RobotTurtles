// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.transform.impl;

public interface FieldProvider
{
    String[] getFieldNames();
    
    Class[] getFieldTypes();
    
    void setField(final int p0, final Object p1);
    
    Object getField(final int p0);
    
    void setField(final String p0, final Object p1);
    
    Object getField(final String p0);
}
