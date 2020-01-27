// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import java.util.List;

public interface Errors
{
    public static final String NESTED_PATH_SEPARATOR = ".";
    
    String getObjectName();
    
    void setNestedPath(final String p0);
    
    String getNestedPath();
    
    void pushNestedPath(final String p0);
    
    void popNestedPath() throws IllegalStateException;
    
    void reject(final String p0);
    
    void reject(final String p0, final String p1);
    
    void reject(final String p0, final Object[] p1, final String p2);
    
    void rejectValue(final String p0, final String p1);
    
    void rejectValue(final String p0, final String p1, final String p2);
    
    void rejectValue(final String p0, final String p1, final Object[] p2, final String p3);
    
    void addAllErrors(final Errors p0);
    
    boolean hasErrors();
    
    int getErrorCount();
    
    List<ObjectError> getAllErrors();
    
    boolean hasGlobalErrors();
    
    int getGlobalErrorCount();
    
    List<ObjectError> getGlobalErrors();
    
    ObjectError getGlobalError();
    
    boolean hasFieldErrors();
    
    int getFieldErrorCount();
    
    List<FieldError> getFieldErrors();
    
    FieldError getFieldError();
    
    boolean hasFieldErrors(final String p0);
    
    int getFieldErrorCount(final String p0);
    
    List<FieldError> getFieldErrors(final String p0);
    
    FieldError getFieldError(final String p0);
    
    Object getFieldValue(final String p0);
    
    Class<?> getFieldType(final String p0);
}
