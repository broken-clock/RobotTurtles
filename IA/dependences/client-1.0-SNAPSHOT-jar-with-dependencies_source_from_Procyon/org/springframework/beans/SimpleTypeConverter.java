// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public class SimpleTypeConverter extends TypeConverterSupport
{
    public SimpleTypeConverter() {
        this.typeConverterDelegate = new TypeConverterDelegate(this);
        this.registerDefaultEditors();
    }
}
