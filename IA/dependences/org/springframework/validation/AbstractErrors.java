// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.springframework.util.StringUtils;
import java.util.EmptyStackException;
import java.util.Stack;
import java.io.Serializable;

public abstract class AbstractErrors implements Errors, Serializable
{
    private String nestedPath;
    private final Stack<String> nestedPathStack;
    
    public AbstractErrors() {
        this.nestedPath = "";
        this.nestedPathStack = new Stack<String>();
    }
    
    @Override
    public void setNestedPath(final String nestedPath) {
        this.doSetNestedPath(nestedPath);
        this.nestedPathStack.clear();
    }
    
    @Override
    public String getNestedPath() {
        return this.nestedPath;
    }
    
    @Override
    public void pushNestedPath(final String subPath) {
        this.nestedPathStack.push(this.getNestedPath());
        this.doSetNestedPath(this.getNestedPath() + subPath);
    }
    
    @Override
    public void popNestedPath() throws IllegalArgumentException {
        try {
            final String formerNestedPath = this.nestedPathStack.pop();
            this.doSetNestedPath(formerNestedPath);
        }
        catch (EmptyStackException ex) {
            throw new IllegalStateException("Cannot pop nested path: no nested path on stack");
        }
    }
    
    protected void doSetNestedPath(String nestedPath) {
        if (nestedPath == null) {
            nestedPath = "";
        }
        nestedPath = this.canonicalFieldName(nestedPath);
        if (nestedPath.length() > 0 && !nestedPath.endsWith(".")) {
            nestedPath += ".";
        }
        this.nestedPath = nestedPath;
    }
    
    protected String fixedField(final String field) {
        if (StringUtils.hasLength(field)) {
            return this.getNestedPath() + this.canonicalFieldName(field);
        }
        final String path = this.getNestedPath();
        return path.endsWith(".") ? path.substring(0, path.length() - ".".length()) : path;
    }
    
    protected String canonicalFieldName(final String field) {
        return field;
    }
    
    @Override
    public void reject(final String errorCode) {
        this.reject(errorCode, null, null);
    }
    
    @Override
    public void reject(final String errorCode, final String defaultMessage) {
        this.reject(errorCode, null, defaultMessage);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode) {
        this.rejectValue(field, errorCode, null, null);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode, final String defaultMessage) {
        this.rejectValue(field, errorCode, null, defaultMessage);
    }
    
    @Override
    public boolean hasErrors() {
        return !this.getAllErrors().isEmpty();
    }
    
    @Override
    public int getErrorCount() {
        return this.getAllErrors().size();
    }
    
    @Override
    public List<ObjectError> getAllErrors() {
        final List<ObjectError> result = new LinkedList<ObjectError>();
        result.addAll(this.getGlobalErrors());
        result.addAll(this.getFieldErrors());
        return Collections.unmodifiableList((List<? extends ObjectError>)result);
    }
    
    @Override
    public boolean hasGlobalErrors() {
        return this.getGlobalErrorCount() > 0;
    }
    
    @Override
    public int getGlobalErrorCount() {
        return this.getGlobalErrors().size();
    }
    
    @Override
    public ObjectError getGlobalError() {
        final List<ObjectError> globalErrors = this.getGlobalErrors();
        return globalErrors.isEmpty() ? null : globalErrors.get(0);
    }
    
    @Override
    public boolean hasFieldErrors() {
        return this.getFieldErrorCount() > 0;
    }
    
    @Override
    public int getFieldErrorCount() {
        return this.getFieldErrors().size();
    }
    
    @Override
    public FieldError getFieldError() {
        final List<FieldError> fieldErrors = this.getFieldErrors();
        return fieldErrors.isEmpty() ? null : fieldErrors.get(0);
    }
    
    @Override
    public boolean hasFieldErrors(final String field) {
        return this.getFieldErrorCount(field) > 0;
    }
    
    @Override
    public int getFieldErrorCount(final String field) {
        return this.getFieldErrors(field).size();
    }
    
    @Override
    public List<FieldError> getFieldErrors(final String field) {
        final List<FieldError> fieldErrors = this.getFieldErrors();
        final List<FieldError> result = new LinkedList<FieldError>();
        final String fixedField = this.fixedField(field);
        for (final FieldError error : fieldErrors) {
            if (this.isMatchingFieldError(fixedField, error)) {
                result.add(error);
            }
        }
        return Collections.unmodifiableList((List<? extends FieldError>)result);
    }
    
    @Override
    public FieldError getFieldError(final String field) {
        final List<FieldError> fieldErrors = this.getFieldErrors(field);
        return fieldErrors.isEmpty() ? null : fieldErrors.get(0);
    }
    
    @Override
    public Class<?> getFieldType(final String field) {
        final Object value = this.getFieldValue(field);
        return (value != null) ? value.getClass() : null;
    }
    
    protected boolean isMatchingFieldError(final String field, final FieldError fieldError) {
        if (field.equals(fieldError.getField())) {
            return true;
        }
        final int endIndex = field.length() - 1;
        return endIndex >= 0 && field.charAt(endIndex) == '*' && (endIndex == 0 || field.regionMatches(0, fieldError.getField(), 0, endIndex));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append(": ").append(this.getErrorCount()).append(" errors");
        for (final ObjectError error : this.getAllErrors()) {
            sb.append('\n').append(error);
        }
        return sb.toString();
    }
}
