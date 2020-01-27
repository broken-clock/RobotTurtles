// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.beans.PropertyEditorRegistry;
import java.beans.PropertyEditor;
import java.util.Map;
import java.util.List;
import org.springframework.util.Assert;

public class BindException extends Exception implements BindingResult
{
    private final BindingResult bindingResult;
    
    public BindException(final BindingResult bindingResult) {
        Assert.notNull(bindingResult, "BindingResult must not be null");
        this.bindingResult = bindingResult;
    }
    
    public BindException(final Object target, final String objectName) {
        Assert.notNull(target, "Target object must not be null");
        this.bindingResult = new BeanPropertyBindingResult(target, objectName);
    }
    
    public final BindingResult getBindingResult() {
        return this.bindingResult;
    }
    
    @Override
    public String getObjectName() {
        return this.bindingResult.getObjectName();
    }
    
    @Override
    public void setNestedPath(final String nestedPath) {
        this.bindingResult.setNestedPath(nestedPath);
    }
    
    @Override
    public String getNestedPath() {
        return this.bindingResult.getNestedPath();
    }
    
    @Override
    public void pushNestedPath(final String subPath) {
        this.bindingResult.pushNestedPath(subPath);
    }
    
    @Override
    public void popNestedPath() throws IllegalStateException {
        this.bindingResult.popNestedPath();
    }
    
    @Override
    public void reject(final String errorCode) {
        this.bindingResult.reject(errorCode);
    }
    
    @Override
    public void reject(final String errorCode, final String defaultMessage) {
        this.bindingResult.reject(errorCode, defaultMessage);
    }
    
    @Override
    public void reject(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        this.bindingResult.reject(errorCode, errorArgs, defaultMessage);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode) {
        this.bindingResult.rejectValue(field, errorCode);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode, final String defaultMessage) {
        this.bindingResult.rejectValue(field, errorCode, defaultMessage);
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        this.bindingResult.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }
    
    @Override
    public void addAllErrors(final Errors errors) {
        this.bindingResult.addAllErrors(errors);
    }
    
    @Override
    public boolean hasErrors() {
        return this.bindingResult.hasErrors();
    }
    
    @Override
    public int getErrorCount() {
        return this.bindingResult.getErrorCount();
    }
    
    @Override
    public List<ObjectError> getAllErrors() {
        return this.bindingResult.getAllErrors();
    }
    
    @Override
    public boolean hasGlobalErrors() {
        return this.bindingResult.hasGlobalErrors();
    }
    
    @Override
    public int getGlobalErrorCount() {
        return this.bindingResult.getGlobalErrorCount();
    }
    
    @Override
    public List<ObjectError> getGlobalErrors() {
        return this.bindingResult.getGlobalErrors();
    }
    
    @Override
    public ObjectError getGlobalError() {
        return this.bindingResult.getGlobalError();
    }
    
    @Override
    public boolean hasFieldErrors() {
        return this.bindingResult.hasFieldErrors();
    }
    
    @Override
    public int getFieldErrorCount() {
        return this.bindingResult.getFieldErrorCount();
    }
    
    @Override
    public List<FieldError> getFieldErrors() {
        return this.bindingResult.getFieldErrors();
    }
    
    @Override
    public FieldError getFieldError() {
        return this.bindingResult.getFieldError();
    }
    
    @Override
    public boolean hasFieldErrors(final String field) {
        return this.bindingResult.hasFieldErrors(field);
    }
    
    @Override
    public int getFieldErrorCount(final String field) {
        return this.bindingResult.getFieldErrorCount(field);
    }
    
    @Override
    public List<FieldError> getFieldErrors(final String field) {
        return this.bindingResult.getFieldErrors(field);
    }
    
    @Override
    public FieldError getFieldError(final String field) {
        return this.bindingResult.getFieldError(field);
    }
    
    @Override
    public Object getFieldValue(final String field) {
        return this.bindingResult.getFieldValue(field);
    }
    
    @Override
    public Class<?> getFieldType(final String field) {
        return this.bindingResult.getFieldType(field);
    }
    
    @Override
    public Object getTarget() {
        return this.bindingResult.getTarget();
    }
    
    @Override
    public Map<String, Object> getModel() {
        return this.bindingResult.getModel();
    }
    
    @Override
    public Object getRawFieldValue(final String field) {
        return this.bindingResult.getRawFieldValue(field);
    }
    
    @Override
    public PropertyEditor findEditor(final String field, final Class valueType) {
        return this.bindingResult.findEditor(field, valueType);
    }
    
    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return this.bindingResult.getPropertyEditorRegistry();
    }
    
    @Override
    public void addError(final ObjectError error) {
        this.bindingResult.addError(error);
    }
    
    @Override
    public String[] resolveMessageCodes(final String errorCode) {
        return this.bindingResult.resolveMessageCodes(errorCode);
    }
    
    @Override
    public String[] resolveMessageCodes(final String errorCode, final String field) {
        return this.bindingResult.resolveMessageCodes(errorCode, field);
    }
    
    @Override
    public void recordSuppressedField(final String field) {
        this.bindingResult.recordSuppressedField(field);
    }
    
    @Override
    public String[] getSuppressedFields() {
        return this.bindingResult.getSuppressedFields();
    }
    
    @Override
    public String getMessage() {
        return this.bindingResult.toString();
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || this.bindingResult.equals(other);
    }
    
    @Override
    public int hashCode() {
        return this.bindingResult.hashCode();
    }
}
