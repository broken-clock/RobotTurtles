// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.beans.PropertyAccessException;

public interface BindingErrorProcessor
{
    void processMissingFieldError(final String p0, final BindingResult p1);
    
    void processPropertyAccessException(final PropertyAccessException p0, final BindingResult p1);
}
