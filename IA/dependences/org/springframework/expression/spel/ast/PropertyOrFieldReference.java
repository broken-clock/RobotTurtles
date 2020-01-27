// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import java.util.Collection;
import java.util.Iterator;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.AccessException;
import org.springframework.core.convert.TypeDescriptor;
import java.util.HashMap;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.PropertyAccessor;

public class PropertyOrFieldReference extends SpelNodeImpl
{
    private final boolean nullSafe;
    private final String name;
    private volatile PropertyAccessor cachedReadAccessor;
    private volatile PropertyAccessor cachedWriteAccessor;
    
    public PropertyOrFieldReference(final boolean nullSafe, final String propertyOrFieldName, final int pos) {
        super(pos, new SpelNodeImpl[0]);
        this.nullSafe = nullSafe;
        this.name = propertyOrFieldName;
    }
    
    public boolean isNullSafe() {
        return this.nullSafe;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ValueRef getValueRef(final ExpressionState state) throws EvaluationException {
        return new AccessorLValue(this, state.getActiveContextObject(), state.getEvaluationContext(), state.getConfiguration().isAutoGrowNullReferences());
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        return this.getValueInternal(state.getActiveContextObject(), state.getEvaluationContext(), state.getConfiguration().isAutoGrowNullReferences());
    }
    
    private TypedValue getValueInternal(final TypedValue contextObject, final EvaluationContext eContext, final boolean isAutoGrowNullReferences) throws EvaluationException {
        TypedValue result = this.readProperty(contextObject, eContext, this.name);
        if (result.getValue() == null && isAutoGrowNullReferences && this.nextChildIs(Indexer.class, PropertyOrFieldReference.class)) {
            final TypeDescriptor resultDescriptor = result.getTypeDescriptor();
            if (resultDescriptor.getType().equals(List.class) || resultDescriptor.getType().equals(Map.class)) {
                if (resultDescriptor.getType().equals(List.class)) {
                    try {
                        if (this.isWritableProperty(this.name, contextObject, eContext)) {
                            final List<?> newList = ArrayList.class.newInstance();
                            this.writeProperty(contextObject, eContext, this.name, newList);
                            result = this.readProperty(contextObject, eContext, this.name);
                        }
                        return result;
                    }
                    catch (InstantiationException ex) {
                        throw new SpelEvaluationException(this.getStartPosition(), ex, SpelMessage.UNABLE_TO_CREATE_LIST_FOR_INDEXING, new Object[0]);
                    }
                    catch (IllegalAccessException ex2) {
                        throw new SpelEvaluationException(this.getStartPosition(), ex2, SpelMessage.UNABLE_TO_CREATE_LIST_FOR_INDEXING, new Object[0]);
                    }
                }
                try {
                    if (this.isWritableProperty(this.name, contextObject, eContext)) {
                        final Map<?, ?> newMap = HashMap.class.newInstance();
                        this.writeProperty(contextObject, eContext, this.name, newMap);
                        result = this.readProperty(contextObject, eContext, this.name);
                    }
                    return result;
                }
                catch (InstantiationException ex) {
                    throw new SpelEvaluationException(this.getStartPosition(), ex, SpelMessage.UNABLE_TO_CREATE_MAP_FOR_INDEXING, new Object[0]);
                }
                catch (IllegalAccessException ex2) {
                    throw new SpelEvaluationException(this.getStartPosition(), ex2, SpelMessage.UNABLE_TO_CREATE_MAP_FOR_INDEXING, new Object[0]);
                }
            }
            try {
                if (this.isWritableProperty(this.name, contextObject, eContext)) {
                    final Object newObject = result.getTypeDescriptor().getType().newInstance();
                    this.writeProperty(contextObject, eContext, this.name, newObject);
                    result = this.readProperty(contextObject, eContext, this.name);
                }
            }
            catch (InstantiationException ex) {
                throw new SpelEvaluationException(this.getStartPosition(), ex, SpelMessage.UNABLE_TO_DYNAMICALLY_CREATE_OBJECT, new Object[] { result.getTypeDescriptor().getType() });
            }
            catch (IllegalAccessException ex2) {
                throw new SpelEvaluationException(this.getStartPosition(), ex2, SpelMessage.UNABLE_TO_DYNAMICALLY_CREATE_OBJECT, new Object[] { result.getTypeDescriptor().getType() });
            }
        }
        return result;
    }
    
    @Override
    public void setValue(final ExpressionState state, final Object newValue) throws SpelEvaluationException {
        this.writeProperty(state.getActiveContextObject(), state.getEvaluationContext(), this.name, newValue);
    }
    
    @Override
    public boolean isWritable(final ExpressionState state) throws SpelEvaluationException {
        return this.isWritableProperty(this.name, state.getActiveContextObject(), state.getEvaluationContext());
    }
    
    @Override
    public String toStringAST() {
        return this.name;
    }
    
    private TypedValue readProperty(final TypedValue contextObject, final EvaluationContext eContext, final String name) throws EvaluationException {
        final Object targetObject = contextObject.getValue();
        if (targetObject == null && this.nullSafe) {
            return TypedValue.NULL;
        }
        final PropertyAccessor accessorToUse = this.cachedReadAccessor;
        if (accessorToUse != null) {
            try {
                return accessorToUse.read(eContext, contextObject.getValue(), name);
            }
            catch (AccessException ae2) {
                this.cachedReadAccessor = null;
            }
        }
        final Class<?> contextObjectClass = this.getObjectClass(contextObject.getValue());
        final List<PropertyAccessor> accessorsToTry = this.getPropertyAccessorsToTry(contextObjectClass, eContext.getPropertyAccessors());
        if (accessorsToTry != null) {
            try {
                for (PropertyAccessor accessor : accessorsToTry) {
                    if (accessor.canRead(eContext, contextObject.getValue(), name)) {
                        if (accessor instanceof ReflectivePropertyAccessor) {
                            accessor = ((ReflectivePropertyAccessor)accessor).createOptimalAccessor(eContext, contextObject.getValue(), name);
                        }
                        this.cachedReadAccessor = accessor;
                        return accessor.read(eContext, contextObject.getValue(), name);
                    }
                }
            }
            catch (AccessException ae) {
                throw new SpelEvaluationException(ae, SpelMessage.EXCEPTION_DURING_PROPERTY_READ, new Object[] { name, ae.getMessage() });
            }
        }
        if (contextObject.getValue() == null) {
            throw new SpelEvaluationException(SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE_ON_NULL, new Object[] { name });
        }
        throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE, new Object[] { name, FormatHelper.formatClassNameForMessage(contextObjectClass) });
    }
    
    private void writeProperty(final TypedValue contextObject, final EvaluationContext eContext, final String name, final Object newValue) throws SpelEvaluationException {
        if (contextObject.getValue() == null && this.nullSafe) {
            return;
        }
        final PropertyAccessor accessorToUse = this.cachedWriteAccessor;
        if (accessorToUse != null) {
            try {
                accessorToUse.write(eContext, contextObject.getValue(), name, newValue);
                return;
            }
            catch (AccessException ae2) {
                this.cachedWriteAccessor = null;
            }
        }
        final Class<?> contextObjectClass = this.getObjectClass(contextObject.getValue());
        final List<PropertyAccessor> accessorsToTry = this.getPropertyAccessorsToTry(contextObjectClass, eContext.getPropertyAccessors());
        if (accessorsToTry != null) {
            try {
                for (final PropertyAccessor accessor : accessorsToTry) {
                    if (accessor.canWrite(eContext, contextObject.getValue(), name)) {
                        (this.cachedWriteAccessor = accessor).write(eContext, contextObject.getValue(), name, newValue);
                        return;
                    }
                }
            }
            catch (AccessException ae) {
                throw new SpelEvaluationException(this.getStartPosition(), ae, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, new Object[] { name, ae.getMessage() });
            }
        }
        if (contextObject.getValue() == null) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE_ON_NULL, new Object[] { name });
        }
        throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE, new Object[] { name, FormatHelper.formatClassNameForMessage(contextObjectClass) });
    }
    
    public boolean isWritableProperty(final String name, final TypedValue contextObject, final EvaluationContext eContext) throws SpelEvaluationException {
        final Object contextObjectValue = contextObject.getValue();
        final List<PropertyAccessor> resolversToTry = this.getPropertyAccessorsToTry(this.getObjectClass(contextObjectValue), eContext.getPropertyAccessors());
        if (resolversToTry != null) {
            for (final PropertyAccessor pfResolver : resolversToTry) {
                try {
                    if (pfResolver.canWrite(eContext, contextObjectValue, name)) {
                        return true;
                    }
                    continue;
                }
                catch (AccessException ex) {}
            }
        }
        return false;
    }
    
    private List<PropertyAccessor> getPropertyAccessorsToTry(final Class<?> targetType, final List<PropertyAccessor> propertyAccessors) {
        final List<PropertyAccessor> specificAccessors = new ArrayList<PropertyAccessor>();
        final List<PropertyAccessor> generalAccessors = new ArrayList<PropertyAccessor>();
        for (final PropertyAccessor resolver : propertyAccessors) {
            final Class<?>[] targets = resolver.getSpecificTargetClasses();
            if (targets == null) {
                generalAccessors.add(resolver);
            }
            else {
                if (targetType == null) {
                    continue;
                }
                for (final Class<?> clazz : targets) {
                    if (clazz == targetType) {
                        specificAccessors.add(resolver);
                        break;
                    }
                    if (clazz.isAssignableFrom(targetType)) {
                        generalAccessors.add(resolver);
                    }
                }
            }
        }
        final List<PropertyAccessor> resolvers = new ArrayList<PropertyAccessor>();
        resolvers.addAll(specificAccessors);
        generalAccessors.removeAll(specificAccessors);
        resolvers.addAll(generalAccessors);
        return resolvers;
    }
    
    private static class AccessorLValue implements ValueRef
    {
        private final PropertyOrFieldReference ref;
        private final TypedValue contextObject;
        private final EvaluationContext eContext;
        private final boolean autoGrowNullReferences;
        
        public AccessorLValue(final PropertyOrFieldReference propertyOrFieldReference, final TypedValue activeContextObject, final EvaluationContext evaluationContext, final boolean autoGrowNullReferences) {
            this.ref = propertyOrFieldReference;
            this.contextObject = activeContextObject;
            this.eContext = evaluationContext;
            this.autoGrowNullReferences = autoGrowNullReferences;
        }
        
        @Override
        public TypedValue getValue() {
            return this.ref.getValueInternal(this.contextObject, this.eContext, this.autoGrowNullReferences);
        }
        
        @Override
        public void setValue(final Object newValue) {
            this.ref.writeProperty(this.contextObject, this.eContext, this.ref.name, newValue);
        }
        
        @Override
        public boolean isWritable() {
            return true;
        }
    }
}
