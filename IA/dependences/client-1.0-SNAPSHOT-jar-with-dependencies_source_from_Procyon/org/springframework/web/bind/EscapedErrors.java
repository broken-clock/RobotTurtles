// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import java.util.Iterator;
import java.util.ArrayList;
import org.springframework.web.util.HtmlUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import java.util.List;
import org.springframework.validation.Errors;

public class EscapedErrors implements Errors
{
    private final Errors source;
    
    public EscapedErrors(final Errors source) {
        if (source == null) {
            throw new IllegalArgumentException("Cannot wrap a null instance");
        }
        this.source = source;
    }
    
    public Errors getSource() {
        return this.source;
    }
    
    @Override
    public String getObjectName() {
        return this.source.getObjectName();
    }
    
    @Override
    public void setNestedPath(final String nestedPath) {
        this.source.setNestedPath(nestedPath);
    }
    
    @Override
    public String getNestedPath() {
        return this.source.getNestedPath();
    }
    
    @Override
    public void pushNestedPath(final String subPath) {
        this.source.pushNestedPath(subPath);
    }
    
    @Override
    public void popNestedPath() throws IllegalStateException {
        this.source.popNestedPath();
    }
    
    @Override
    public void reject(final String errorCode) {
        this.source.reject(errorCode);
    }
    
    @Override
    public void reject(final String errorCode, final String defaultMessage) {
        this.source.reject(errorCode, defaultMessage);
    }
    
    @Override
    public void reject(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        this.source.reject(errorCode, errorArgs, defaultMessage);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode) {
        this.source.rejectValue(field, errorCode);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode, final String defaultMessage) {
        this.source.rejectValue(field, errorCode, defaultMessage);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        this.source.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }
    
    @Override
    public void addAllErrors(final Errors errors) {
        this.source.addAllErrors(errors);
    }
    
    @Override
    public boolean hasErrors() {
        return this.source.hasErrors();
    }
    
    @Override
    public int getErrorCount() {
        return this.source.getErrorCount();
    }
    
    @Override
    public List<ObjectError> getAllErrors() {
        return this.escapeObjectErrors(this.source.getAllErrors());
    }
    
    @Override
    public boolean hasGlobalErrors() {
        return this.source.hasGlobalErrors();
    }
    
    @Override
    public int getGlobalErrorCount() {
        return this.source.getGlobalErrorCount();
    }
    
    @Override
    public List<ObjectError> getGlobalErrors() {
        return this.escapeObjectErrors(this.source.getGlobalErrors());
    }
    
    @Override
    public ObjectError getGlobalError() {
        return this.escapeObjectError(this.source.getGlobalError());
    }
    
    @Override
    public boolean hasFieldErrors() {
        return this.source.hasFieldErrors();
    }
    
    @Override
    public int getFieldErrorCount() {
        return this.source.getFieldErrorCount();
    }
    
    @Override
    public List<FieldError> getFieldErrors() {
        return this.source.getFieldErrors();
    }
    
    @Override
    public FieldError getFieldError() {
        return this.source.getFieldError();
    }
    
    @Override
    public boolean hasFieldErrors(final String field) {
        return this.source.hasFieldErrors(field);
    }
    
    @Override
    public int getFieldErrorCount(final String field) {
        return this.source.getFieldErrorCount(field);
    }
    
    @Override
    public List<FieldError> getFieldErrors(final String field) {
        return this.escapeObjectErrors(this.source.getFieldErrors(field));
    }
    
    @Override
    public FieldError getFieldError(final String field) {
        return this.escapeObjectError(this.source.getFieldError(field));
    }
    
    @Override
    public Object getFieldValue(final String field) {
        final Object value = this.source.getFieldValue(field);
        return (value instanceof String) ? HtmlUtils.htmlEscape((String)value) : value;
    }
    
    @Override
    public Class<?> getFieldType(final String field) {
        return this.source.getFieldType(field);
    }
    
    private <T extends ObjectError> T escapeObjectError(final T source) {
        if (source == null) {
            return null;
        }
        if (source instanceof FieldError) {
            final FieldError fieldError = (FieldError)source;
            Object value = fieldError.getRejectedValue();
            if (value instanceof String) {
                value = HtmlUtils.htmlEscape((String)value);
            }
            return (T)new FieldError(fieldError.getObjectName(), fieldError.getField(), value, fieldError.isBindingFailure(), fieldError.getCodes(), fieldError.getArguments(), HtmlUtils.htmlEscape(fieldError.getDefaultMessage()));
        }
        return (T)new ObjectError(source.getObjectName(), source.getCodes(), source.getArguments(), HtmlUtils.htmlEscape(source.getDefaultMessage()));
    }
    
    private <T extends ObjectError> List<T> escapeObjectErrors(final List<T> source) {
        final List<T> escaped = new ArrayList<T>(source.size());
        for (final T objectError : source) {
            escaped.add(this.escapeObjectError(objectError));
        }
        return escaped;
    }
}
