// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

public enum PropertyAccessor
{
    GETTER, 
    SETTER, 
    CREATOR, 
    FIELD, 
    IS_GETTER, 
    NONE, 
    ALL;
    
    public boolean creatorEnabled() {
        return this == PropertyAccessor.CREATOR || this == PropertyAccessor.ALL;
    }
    
    public boolean getterEnabled() {
        return this == PropertyAccessor.GETTER || this == PropertyAccessor.ALL;
    }
    
    public boolean isGetterEnabled() {
        return this == PropertyAccessor.IS_GETTER || this == PropertyAccessor.ALL;
    }
    
    public boolean setterEnabled() {
        return this == PropertyAccessor.SETTER || this == PropertyAccessor.ALL;
    }
    
    public boolean fieldEnabled() {
        return this == PropertyAccessor.FIELD || this == PropertyAccessor.ALL;
    }
}
