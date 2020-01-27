// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

public interface Validator
{
    boolean supports(final Class<?> p0);
    
    void validate(final Object p0, final Errors p1);
}
