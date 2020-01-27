// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.BeanProperty;
import java.lang.reflect.InvocationTargetException;
import java.io.Closeable;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

@JacksonStdImpl
public class StdValueInstantiator extends ValueInstantiator implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final String _valueTypeDesc;
    protected final Class<?> _valueClass;
    protected AnnotatedWithParams _defaultCreator;
    protected AnnotatedWithParams _withArgsCreator;
    protected SettableBeanProperty[] _constructorArguments;
    protected JavaType _delegateType;
    protected AnnotatedWithParams _delegateCreator;
    protected SettableBeanProperty[] _delegateArguments;
    protected JavaType _arrayDelegateType;
    protected AnnotatedWithParams _arrayDelegateCreator;
    protected SettableBeanProperty[] _arrayDelegateArguments;
    protected AnnotatedWithParams _fromStringCreator;
    protected AnnotatedWithParams _fromIntCreator;
    protected AnnotatedWithParams _fromLongCreator;
    protected AnnotatedWithParams _fromDoubleCreator;
    protected AnnotatedWithParams _fromBooleanCreator;
    protected AnnotatedParameter _incompleteParameter;
    
    @Deprecated
    public StdValueInstantiator(final DeserializationConfig config, final Class<?> valueType) {
        this._valueTypeDesc = ClassUtil.nameOf(valueType);
        this._valueClass = ((valueType == null) ? Object.class : valueType);
    }
    
    public StdValueInstantiator(final DeserializationConfig config, final JavaType valueType) {
        this._valueTypeDesc = ((valueType == null) ? "UNKNOWN TYPE" : valueType.toString());
        this._valueClass = ((valueType == null) ? Object.class : valueType.getRawClass());
    }
    
    protected StdValueInstantiator(final StdValueInstantiator src) {
        this._valueTypeDesc = src._valueTypeDesc;
        this._valueClass = src._valueClass;
        this._defaultCreator = src._defaultCreator;
        this._constructorArguments = src._constructorArguments;
        this._withArgsCreator = src._withArgsCreator;
        this._delegateType = src._delegateType;
        this._delegateCreator = src._delegateCreator;
        this._delegateArguments = src._delegateArguments;
        this._arrayDelegateType = src._arrayDelegateType;
        this._arrayDelegateCreator = src._arrayDelegateCreator;
        this._arrayDelegateArguments = src._arrayDelegateArguments;
        this._fromStringCreator = src._fromStringCreator;
        this._fromIntCreator = src._fromIntCreator;
        this._fromLongCreator = src._fromLongCreator;
        this._fromDoubleCreator = src._fromDoubleCreator;
        this._fromBooleanCreator = src._fromBooleanCreator;
    }
    
    public void configureFromObjectSettings(final AnnotatedWithParams defaultCreator, final AnnotatedWithParams delegateCreator, final JavaType delegateType, final SettableBeanProperty[] delegateArgs, final AnnotatedWithParams withArgsCreator, final SettableBeanProperty[] constructorArgs) {
        this._defaultCreator = defaultCreator;
        this._delegateCreator = delegateCreator;
        this._delegateType = delegateType;
        this._delegateArguments = delegateArgs;
        this._withArgsCreator = withArgsCreator;
        this._constructorArguments = constructorArgs;
    }
    
    public void configureFromArraySettings(final AnnotatedWithParams arrayDelegateCreator, final JavaType arrayDelegateType, final SettableBeanProperty[] arrayDelegateArgs) {
        this._arrayDelegateCreator = arrayDelegateCreator;
        this._arrayDelegateType = arrayDelegateType;
        this._arrayDelegateArguments = arrayDelegateArgs;
    }
    
    public void configureFromStringCreator(final AnnotatedWithParams creator) {
        this._fromStringCreator = creator;
    }
    
    public void configureFromIntCreator(final AnnotatedWithParams creator) {
        this._fromIntCreator = creator;
    }
    
    public void configureFromLongCreator(final AnnotatedWithParams creator) {
        this._fromLongCreator = creator;
    }
    
    public void configureFromDoubleCreator(final AnnotatedWithParams creator) {
        this._fromDoubleCreator = creator;
    }
    
    public void configureFromBooleanCreator(final AnnotatedWithParams creator) {
        this._fromBooleanCreator = creator;
    }
    
    public void configureIncompleteParameter(final AnnotatedParameter parameter) {
        this._incompleteParameter = parameter;
    }
    
    @Override
    public String getValueTypeDesc() {
        return this._valueTypeDesc;
    }
    
    @Override
    public Class<?> getValueClass() {
        return this._valueClass;
    }
    
    @Override
    public boolean canCreateFromString() {
        return this._fromStringCreator != null;
    }
    
    @Override
    public boolean canCreateFromInt() {
        return this._fromIntCreator != null;
    }
    
    @Override
    public boolean canCreateFromLong() {
        return this._fromLongCreator != null;
    }
    
    @Override
    public boolean canCreateFromDouble() {
        return this._fromDoubleCreator != null;
    }
    
    @Override
    public boolean canCreateFromBoolean() {
        return this._fromBooleanCreator != null;
    }
    
    @Override
    public boolean canCreateUsingDefault() {
        return this._defaultCreator != null;
    }
    
    @Override
    public boolean canCreateUsingDelegate() {
        return this._delegateType != null;
    }
    
    @Override
    public boolean canCreateUsingArrayDelegate() {
        return this._arrayDelegateType != null;
    }
    
    @Override
    public boolean canCreateFromObjectWith() {
        return this._withArgsCreator != null;
    }
    
    @Override
    public boolean canInstantiate() {
        return this.canCreateUsingDefault() || this.canCreateUsingDelegate() || this.canCreateUsingArrayDelegate() || this.canCreateFromObjectWith() || this.canCreateFromString() || this.canCreateFromInt() || this.canCreateFromLong() || this.canCreateFromDouble() || this.canCreateFromBoolean();
    }
    
    @Override
    public JavaType getDelegateType(final DeserializationConfig config) {
        return this._delegateType;
    }
    
    @Override
    public JavaType getArrayDelegateType(final DeserializationConfig config) {
        return this._arrayDelegateType;
    }
    
    @Override
    public SettableBeanProperty[] getFromObjectArguments(final DeserializationConfig config) {
        return this._constructorArguments;
    }
    
    @Override
    public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
        if (this._defaultCreator == null) {
            return super.createUsingDefault(ctxt);
        }
        try {
            return this._defaultCreator.call();
        }
        catch (Exception e) {
            return ctxt.handleInstantiationProblem(this._valueClass, null, this.rewrapCtorProblem(ctxt, e));
        }
    }
    
    @Override
    public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) throws IOException {
        if (this._withArgsCreator == null) {
            return super.createFromObjectWith(ctxt, args);
        }
        try {
            return this._withArgsCreator.call(args);
        }
        catch (Exception e) {
            return ctxt.handleInstantiationProblem(this._valueClass, args, this.rewrapCtorProblem(ctxt, e));
        }
    }
    
    @Override
    public Object createUsingDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
        if (this._delegateCreator == null && this._arrayDelegateCreator != null) {
            return this._createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
        }
        return this._createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
    }
    
    @Override
    public Object createUsingArrayDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
        if (this._arrayDelegateCreator == null && this._delegateCreator != null) {
            return this.createUsingDelegate(ctxt, delegate);
        }
        return this._createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
    }
    
    @Override
    public Object createFromString(final DeserializationContext ctxt, final String value) throws IOException {
        if (this._fromStringCreator == null) {
            return this._createFromStringFallbacks(ctxt, value);
        }
        try {
            return this._fromStringCreator.call1(value);
        }
        catch (Throwable t) {
            return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, this.rewrapCtorProblem(ctxt, t));
        }
    }
    
    @Override
    public Object createFromInt(final DeserializationContext ctxt, final int value) throws IOException {
        if (this._fromIntCreator != null) {
            final Object arg = value;
            try {
                return this._fromIntCreator.call1(arg);
            }
            catch (Throwable t0) {
                return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, t0));
            }
        }
        if (this._fromLongCreator != null) {
            final Object arg = (long)value;
            try {
                return this._fromLongCreator.call1(arg);
            }
            catch (Throwable t0) {
                return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, t0));
            }
        }
        return super.createFromInt(ctxt, value);
    }
    
    @Override
    public Object createFromLong(final DeserializationContext ctxt, final long value) throws IOException {
        if (this._fromLongCreator == null) {
            return super.createFromLong(ctxt, value);
        }
        final Object arg = value;
        try {
            return this._fromLongCreator.call1(arg);
        }
        catch (Throwable t0) {
            return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, t0));
        }
    }
    
    @Override
    public Object createFromDouble(final DeserializationContext ctxt, final double value) throws IOException {
        if (this._fromDoubleCreator == null) {
            return super.createFromDouble(ctxt, value);
        }
        final Object arg = value;
        try {
            return this._fromDoubleCreator.call1(arg);
        }
        catch (Throwable t0) {
            return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, t0));
        }
    }
    
    @Override
    public Object createFromBoolean(final DeserializationContext ctxt, final boolean value) throws IOException {
        if (this._fromBooleanCreator == null) {
            return super.createFromBoolean(ctxt, value);
        }
        final Boolean arg = value;
        try {
            return this._fromBooleanCreator.call1(arg);
        }
        catch (Throwable t0) {
            return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, t0));
        }
    }
    
    @Override
    public AnnotatedWithParams getDelegateCreator() {
        return this._delegateCreator;
    }
    
    @Override
    public AnnotatedWithParams getArrayDelegateCreator() {
        return this._arrayDelegateCreator;
    }
    
    @Override
    public AnnotatedWithParams getDefaultCreator() {
        return this._defaultCreator;
    }
    
    @Override
    public AnnotatedWithParams getWithArgsCreator() {
        return this._withArgsCreator;
    }
    
    @Override
    public AnnotatedParameter getIncompleteParameter() {
        return this._incompleteParameter;
    }
    
    @Deprecated
    protected JsonMappingException wrapException(final Throwable t) {
        for (Throwable curr = t; curr != null; curr = curr.getCause()) {
            if (curr instanceof JsonMappingException) {
                return (JsonMappingException)curr;
            }
        }
        return new JsonMappingException(null, "Instantiation of " + this.getValueTypeDesc() + " value failed: " + ClassUtil.exceptionMessage(t), t);
    }
    
    protected JsonMappingException unwrapAndWrapException(final DeserializationContext ctxt, final Throwable t) {
        for (Throwable curr = t; curr != null; curr = curr.getCause()) {
            if (curr instanceof JsonMappingException) {
                return (JsonMappingException)curr;
            }
        }
        return ctxt.instantiationException(this.getValueClass(), t);
    }
    
    protected JsonMappingException wrapAsJsonMappingException(final DeserializationContext ctxt, final Throwable t) {
        if (t instanceof JsonMappingException) {
            return (JsonMappingException)t;
        }
        return ctxt.instantiationException(this.getValueClass(), t);
    }
    
    protected JsonMappingException rewrapCtorProblem(final DeserializationContext ctxt, Throwable t) {
        if (t instanceof ExceptionInInitializerError || t instanceof InvocationTargetException) {
            final Throwable cause = t.getCause();
            if (cause != null) {
                t = cause;
            }
        }
        return this.wrapAsJsonMappingException(ctxt, t);
    }
    
    private Object _createUsingDelegate(final AnnotatedWithParams delegateCreator, final SettableBeanProperty[] delegateArguments, final DeserializationContext ctxt, final Object delegate) throws IOException {
        if (delegateCreator == null) {
            throw new IllegalStateException("No delegate constructor for " + this.getValueTypeDesc());
        }
        try {
            if (delegateArguments == null) {
                return delegateCreator.call1(delegate);
            }
            final int len = delegateArguments.length;
            final Object[] args = new Object[len];
            for (int i = 0; i < len; ++i) {
                final SettableBeanProperty prop = delegateArguments[i];
                if (prop == null) {
                    args[i] = delegate;
                }
                else {
                    args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
                }
            }
            return delegateCreator.call(args);
        }
        catch (Throwable t) {
            throw this.rewrapCtorProblem(ctxt, t);
        }
    }
}
