// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.util.ObjectUtils;
import org.springframework.beans.PropertyEditorRegistry;
import java.beans.PropertyEditor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.io.Serializable;

public abstract class AbstractBindingResult extends AbstractErrors implements BindingResult, Serializable
{
    private final String objectName;
    private MessageCodesResolver messageCodesResolver;
    private final List<ObjectError> errors;
    private final Set<String> suppressedFields;
    
    protected AbstractBindingResult(final String objectName) {
        this.messageCodesResolver = new DefaultMessageCodesResolver();
        this.errors = new LinkedList<ObjectError>();
        this.suppressedFields = new HashSet<String>();
        this.objectName = objectName;
    }
    
    public void setMessageCodesResolver(final MessageCodesResolver messageCodesResolver) {
        Assert.notNull(messageCodesResolver, "MessageCodesResolver must not be null");
        this.messageCodesResolver = messageCodesResolver;
    }
    
    public MessageCodesResolver getMessageCodesResolver() {
        return this.messageCodesResolver;
    }
    
    @Override
    public String getObjectName() {
        return this.objectName;
    }
    
    @Override
    public void reject(final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        this.addError(new ObjectError(this.getObjectName(), this.resolveMessageCodes(errorCode), errorArgs, defaultMessage));
    }
    
    @Override
    public void rejectValue(final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        if ("".equals(this.getNestedPath()) && !StringUtils.hasLength(field)) {
            this.reject(errorCode, errorArgs, defaultMessage);
            return;
        }
        final String fixedField = this.fixedField(field);
        final Object newVal = this.getActualFieldValue(fixedField);
        final FieldError fe = new FieldError(this.getObjectName(), fixedField, newVal, false, this.resolveMessageCodes(errorCode, field), errorArgs, defaultMessage);
        this.addError(fe);
    }
    
    @Override
    public void addError(final ObjectError error) {
        this.errors.add(error);
    }
    
    @Override
    public void addAllErrors(final Errors errors) {
        if (!errors.getObjectName().equals(this.getObjectName())) {
            throw new IllegalArgumentException("Errors object needs to have same object name");
        }
        this.errors.addAll(errors.getAllErrors());
    }
    
    @Override
    public String[] resolveMessageCodes(final String errorCode) {
        return this.getMessageCodesResolver().resolveMessageCodes(errorCode, this.getObjectName());
    }
    
    @Override
    public String[] resolveMessageCodes(final String errorCode, final String field) {
        final Class<?> fieldType = this.getFieldType(field);
        return this.getMessageCodesResolver().resolveMessageCodes(errorCode, this.getObjectName(), this.fixedField(field), fieldType);
    }
    
    @Override
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
    
    @Override
    public int getErrorCount() {
        return this.errors.size();
    }
    
    @Override
    public List<ObjectError> getAllErrors() {
        return Collections.unmodifiableList((List<? extends ObjectError>)this.errors);
    }
    
    @Override
    public List<ObjectError> getGlobalErrors() {
        final List<ObjectError> result = new LinkedList<ObjectError>();
        for (final ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError)) {
                result.add(objectError);
            }
        }
        return Collections.unmodifiableList((List<? extends ObjectError>)result);
    }
    
    @Override
    public ObjectError getGlobalError() {
        for (final ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError)) {
                return objectError;
            }
        }
        return null;
    }
    
    @Override
    public List<FieldError> getFieldErrors() {
        final List<FieldError> result = new LinkedList<FieldError>();
        for (final ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                result.add((FieldError)objectError);
            }
        }
        return Collections.unmodifiableList((List<? extends FieldError>)result);
    }
    
    @Override
    public FieldError getFieldError() {
        for (final ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                return (FieldError)objectError;
            }
        }
        return null;
    }
    
    @Override
    public List<FieldError> getFieldErrors(final String field) {
        final List<FieldError> result = new LinkedList<FieldError>();
        final String fixedField = this.fixedField(field);
        for (final ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError && this.isMatchingFieldError(fixedField, (FieldError)objectError)) {
                result.add((FieldError)objectError);
            }
        }
        return Collections.unmodifiableList((List<? extends FieldError>)result);
    }
    
    @Override
    public FieldError getFieldError(final String field) {
        final String fixedField = this.fixedField(field);
        for (final ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                final FieldError fieldError = (FieldError)objectError;
                if (this.isMatchingFieldError(fixedField, fieldError)) {
                    return fieldError;
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public Object getFieldValue(final String field) {
        final FieldError fieldError = this.getFieldError(field);
        Object value = (fieldError != null) ? fieldError.getRejectedValue() : this.getActualFieldValue(this.fixedField(field));
        if (fieldError == null || !fieldError.isBindingFailure()) {
            value = this.formatFieldValue(field, value);
        }
        return value;
    }
    
    @Override
    public Class<?> getFieldType(final String field) {
        final Object value = this.getActualFieldValue(this.fixedField(field));
        if (value != null) {
            return value.getClass();
        }
        return null;
    }
    
    @Override
    public Map<String, Object> getModel() {
        final Map<String, Object> model = new LinkedHashMap<String, Object>(2);
        model.put(this.getObjectName(), this.getTarget());
        model.put(AbstractBindingResult.MODEL_KEY_PREFIX + this.getObjectName(), this);
        return model;
    }
    
    @Override
    public Object getRawFieldValue(final String field) {
        return this.getActualFieldValue(this.fixedField(field));
    }
    
    @Override
    public PropertyEditor findEditor(final String field, final Class<?> valueType) {
        final PropertyEditorRegistry editorRegistry = this.getPropertyEditorRegistry();
        if (editorRegistry != null) {
            Class<?> valueTypeToUse = valueType;
            if (valueTypeToUse == null) {
                valueTypeToUse = this.getFieldType(field);
            }
            return editorRegistry.findCustomEditor(valueTypeToUse, this.fixedField(field));
        }
        return null;
    }
    
    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return null;
    }
    
    @Override
    public void recordSuppressedField(final String field) {
        this.suppressedFields.add(field);
    }
    
    @Override
    public String[] getSuppressedFields() {
        return StringUtils.toStringArray(this.suppressedFields);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BindingResult)) {
            return false;
        }
        final BindingResult otherResult = (BindingResult)other;
        return this.getObjectName().equals(otherResult.getObjectName()) && ObjectUtils.nullSafeEquals(this.getTarget(), otherResult.getTarget()) && this.getAllErrors().equals(otherResult.getAllErrors());
    }
    
    @Override
    public int hashCode() {
        return this.getObjectName().hashCode();
    }
    
    @Override
    public abstract Object getTarget();
    
    protected abstract Object getActualFieldValue(final String p0);
    
    protected Object formatFieldValue(final String field, final Object value) {
        return value;
    }
}
