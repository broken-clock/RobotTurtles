// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import java.util.Iterator;
import java.util.List;
import org.springframework.expression.AccessException;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeConverter;
import org.springframework.core.convert.TypeDescriptor;
import java.util.Collection;
import org.springframework.expression.spel.SpelMessage;
import java.util.Map;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.PropertyAccessor;

public class Indexer extends SpelNodeImpl
{
    private String cachedReadName;
    private Class<?> cachedReadTargetType;
    private PropertyAccessor cachedReadAccessor;
    private String cachedWriteName;
    private Class<?> cachedWriteTargetType;
    private PropertyAccessor cachedWriteAccessor;
    
    public Indexer(final int pos, final SpelNodeImpl expr) {
        super(pos, new SpelNodeImpl[] { expr });
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        return this.getValueRef(state).getValue();
    }
    
    @Override
    public void setValue(final ExpressionState state, final Object newValue) throws EvaluationException {
        this.getValueRef(state).setValue(newValue);
    }
    
    @Override
    public boolean isWritable(final ExpressionState expressionState) throws SpelEvaluationException {
        return true;
    }
    
    @Override
    protected ValueRef getValueRef(final ExpressionState state) throws EvaluationException {
        final TypedValue context = state.getActiveContextObject();
        final Object targetObject = context.getValue();
        final TypeDescriptor targetObjectTypeDescriptor = context.getTypeDescriptor();
        TypedValue indexValue = null;
        Object index = null;
        if (targetObject instanceof Map && this.children[0] instanceof PropertyOrFieldReference) {
            final PropertyOrFieldReference reference = (PropertyOrFieldReference)this.children[0];
            index = reference.getName();
            indexValue = new TypedValue(index);
        }
        else {
            try {
                state.pushActiveContextObject(state.getRootContextObject());
                indexValue = this.children[0].getValueInternal(state);
                index = indexValue.getValue();
            }
            finally {
                state.popActiveContextObject();
            }
        }
        if (targetObject instanceof Map) {
            Object key = index;
            if (targetObjectTypeDescriptor.getMapKeyTypeDescriptor() != null) {
                key = state.convertValue(key, targetObjectTypeDescriptor.getMapKeyTypeDescriptor());
            }
            return new MapIndexingValueRef(state.getTypeConverter(), (Map)targetObject, key, targetObjectTypeDescriptor);
        }
        if (targetObject == null) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.CANNOT_INDEX_INTO_NULL_VALUE, new Object[0]);
        }
        if (targetObject.getClass().isArray() || targetObject instanceof Collection || targetObject instanceof String) {
            final int idx = (int)state.convertValue(index, TypeDescriptor.valueOf(Integer.class));
            if (targetObject.getClass().isArray()) {
                return new ArrayIndexingValueRef(state.getTypeConverter(), targetObject, idx, targetObjectTypeDescriptor);
            }
            if (targetObject instanceof Collection) {
                return new CollectionIndexingValueRef((Collection)targetObject, idx, targetObjectTypeDescriptor, state.getTypeConverter(), state.getConfiguration().isAutoGrowCollections(), state.getConfiguration().getMaximumAutoGrowSize());
            }
            if (targetObject instanceof String) {
                return new StringIndexingLValue((String)targetObject, idx, targetObjectTypeDescriptor);
            }
        }
        if (indexValue.getTypeDescriptor().getType() == String.class) {
            return new PropertyIndexingValueRef(targetObject, (String)indexValue.getValue(), state.getEvaluationContext(), targetObjectTypeDescriptor);
        }
        throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { targetObjectTypeDescriptor.toString() });
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(this.getChild(i).toStringAST());
        }
        sb.append("]");
        return sb.toString();
    }
    
    private void setArrayElement(final TypeConverter converter, final Object ctx, final int idx, final Object newValue, final Class<?> arrayComponentType) throws EvaluationException {
        if (arrayComponentType == Integer.TYPE) {
            final int[] array = (int[])ctx;
            this.checkAccess(array.length, idx);
            array[idx] = (int)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Integer.class));
        }
        else if (arrayComponentType == Boolean.TYPE) {
            final boolean[] array2 = (boolean[])ctx;
            this.checkAccess(array2.length, idx);
            array2[idx] = (boolean)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Boolean.class));
        }
        else if (arrayComponentType == Character.TYPE) {
            final char[] array3 = (char[])ctx;
            this.checkAccess(array3.length, idx);
            array3[idx] = (char)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Character.class));
        }
        else if (arrayComponentType == Long.TYPE) {
            final long[] array4 = (long[])ctx;
            this.checkAccess(array4.length, idx);
            array4[idx] = (long)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Long.class));
        }
        else if (arrayComponentType == Short.TYPE) {
            final short[] array5 = (short[])ctx;
            this.checkAccess(array5.length, idx);
            array5[idx] = (short)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Short.class));
        }
        else if (arrayComponentType == Double.TYPE) {
            final double[] array6 = (double[])ctx;
            this.checkAccess(array6.length, idx);
            array6[idx] = (double)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Double.class));
        }
        else if (arrayComponentType == Float.TYPE) {
            final float[] array7 = (float[])ctx;
            this.checkAccess(array7.length, idx);
            array7[idx] = (float)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Float.class));
        }
        else if (arrayComponentType == Byte.TYPE) {
            final byte[] array8 = (byte[])ctx;
            this.checkAccess(array8.length, idx);
            array8[idx] = (byte)converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(Byte.class));
        }
        else {
            final Object[] array9 = (Object[])ctx;
            this.checkAccess(array9.length, idx);
            array9[idx] = converter.convertValue(newValue, TypeDescriptor.forObject(newValue), TypeDescriptor.valueOf(arrayComponentType));
        }
    }
    
    private Object accessArrayElement(final Object ctx, final int idx) throws SpelEvaluationException {
        final Class<?> arrayComponentType = ctx.getClass().getComponentType();
        if (arrayComponentType == Integer.TYPE) {
            final int[] array = (int[])ctx;
            this.checkAccess(array.length, idx);
            return array[idx];
        }
        if (arrayComponentType == Boolean.TYPE) {
            final boolean[] array2 = (boolean[])ctx;
            this.checkAccess(array2.length, idx);
            return array2[idx];
        }
        if (arrayComponentType == Character.TYPE) {
            final char[] array3 = (char[])ctx;
            this.checkAccess(array3.length, idx);
            return array3[idx];
        }
        if (arrayComponentType == Long.TYPE) {
            final long[] array4 = (long[])ctx;
            this.checkAccess(array4.length, idx);
            return array4[idx];
        }
        if (arrayComponentType == Short.TYPE) {
            final short[] array5 = (short[])ctx;
            this.checkAccess(array5.length, idx);
            return array5[idx];
        }
        if (arrayComponentType == Double.TYPE) {
            final double[] array6 = (double[])ctx;
            this.checkAccess(array6.length, idx);
            return array6[idx];
        }
        if (arrayComponentType == Float.TYPE) {
            final float[] array7 = (float[])ctx;
            this.checkAccess(array7.length, idx);
            return array7[idx];
        }
        if (arrayComponentType == Byte.TYPE) {
            final byte[] array8 = (byte[])ctx;
            this.checkAccess(array8.length, idx);
            return array8[idx];
        }
        final Object[] array9 = (Object[])ctx;
        this.checkAccess(array9.length, idx);
        return array9[idx];
    }
    
    private void checkAccess(final int arrayLength, final int index) throws SpelEvaluationException {
        if (index > arrayLength) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.ARRAY_INDEX_OUT_OF_BOUNDS, new Object[] { arrayLength, index });
        }
    }
    
    private class ArrayIndexingValueRef implements ValueRef
    {
        private final TypeConverter typeConverter;
        private final Object array;
        private final int index;
        private final TypeDescriptor typeDescriptor;
        
        ArrayIndexingValueRef(final TypeConverter typeConverter, final Object array, final int index, final TypeDescriptor typeDescriptor) {
            this.typeConverter = typeConverter;
            this.array = array;
            this.index = index;
            this.typeDescriptor = typeDescriptor;
        }
        
        @Override
        public TypedValue getValue() {
            final Object arrayElement = Indexer.this.accessArrayElement(this.array, this.index);
            return new TypedValue(arrayElement, this.typeDescriptor.elementTypeDescriptor(arrayElement));
        }
        
        @Override
        public void setValue(final Object newValue) {
            Indexer.this.setArrayElement(this.typeConverter, this.array, this.index, newValue, this.typeDescriptor.getElementTypeDescriptor().getType());
        }
        
        @Override
        public boolean isWritable() {
            return true;
        }
    }
    
    private static class MapIndexingValueRef implements ValueRef
    {
        private final TypeConverter typeConverter;
        private final Map map;
        private final Object key;
        private final TypeDescriptor mapEntryTypeDescriptor;
        
        public MapIndexingValueRef(final TypeConverter typeConverter, final Map map, final Object key, final TypeDescriptor mapEntryTypeDescriptor) {
            this.typeConverter = typeConverter;
            this.map = map;
            this.key = key;
            this.mapEntryTypeDescriptor = mapEntryTypeDescriptor;
        }
        
        @Override
        public TypedValue getValue() {
            final Object value = this.map.get(this.key);
            return new TypedValue(value, this.mapEntryTypeDescriptor.getMapValueTypeDescriptor(value));
        }
        
        @Override
        public void setValue(Object newValue) {
            if (this.mapEntryTypeDescriptor.getMapValueTypeDescriptor() != null) {
                newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.mapEntryTypeDescriptor.getMapValueTypeDescriptor());
            }
            this.map.put(this.key, newValue);
        }
        
        @Override
        public boolean isWritable() {
            return true;
        }
    }
    
    private class PropertyIndexingValueRef implements ValueRef
    {
        private final Object targetObject;
        private final String name;
        private final EvaluationContext evaluationContext;
        private final TypeDescriptor targetObjectTypeDescriptor;
        
        public PropertyIndexingValueRef(final Object targetObject, final String value, final EvaluationContext evaluationContext, final TypeDescriptor targetObjectTypeDescriptor) {
            this.targetObject = targetObject;
            this.name = value;
            this.evaluationContext = evaluationContext;
            this.targetObjectTypeDescriptor = targetObjectTypeDescriptor;
        }
        
        @Override
        public TypedValue getValue() {
            final Class<?> targetObjectRuntimeClass = Indexer.this.getObjectClass(this.targetObject);
            try {
                if (Indexer.this.cachedReadName != null && Indexer.this.cachedReadName.equals(this.name) && Indexer.this.cachedReadTargetType != null && Indexer.this.cachedReadTargetType.equals(targetObjectRuntimeClass)) {
                    return Indexer.this.cachedReadAccessor.read(this.evaluationContext, this.targetObject, this.name);
                }
                final List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(targetObjectRuntimeClass, this.evaluationContext.getPropertyAccessors());
                if (accessorsToTry != null) {
                    for (PropertyAccessor accessor : accessorsToTry) {
                        if (accessor.canRead(this.evaluationContext, this.targetObject, this.name)) {
                            if (accessor instanceof ReflectivePropertyAccessor) {
                                accessor = ((ReflectivePropertyAccessor)accessor).createOptimalAccessor(this.evaluationContext, this.targetObject, this.name);
                            }
                            Indexer.this.cachedReadAccessor = accessor;
                            Indexer.this.cachedReadName = this.name;
                            Indexer.this.cachedReadTargetType = targetObjectRuntimeClass;
                            return accessor.read(this.evaluationContext, this.targetObject, this.name);
                        }
                    }
                }
            }
            catch (AccessException ex) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.targetObjectTypeDescriptor.toString() });
            }
            throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.targetObjectTypeDescriptor.toString() });
        }
        
        @Override
        public void setValue(final Object newValue) {
            final Class<?> contextObjectClass = Indexer.this.getObjectClass(this.targetObject);
            try {
                if (Indexer.this.cachedWriteName != null && Indexer.this.cachedWriteName.equals(this.name) && Indexer.this.cachedWriteTargetType != null && Indexer.this.cachedWriteTargetType.equals(contextObjectClass)) {
                    Indexer.this.cachedWriteAccessor.write(this.evaluationContext, this.targetObject, this.name, newValue);
                    return;
                }
                final List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(contextObjectClass, this.evaluationContext.getPropertyAccessors());
                if (accessorsToTry != null) {
                    for (final PropertyAccessor accessor : accessorsToTry) {
                        if (accessor.canWrite(this.evaluationContext, this.targetObject, this.name)) {
                            Indexer.this.cachedWriteName = this.name;
                            Indexer.this.cachedWriteTargetType = contextObjectClass;
                            Indexer.this.cachedWriteAccessor = accessor;
                            accessor.write(this.evaluationContext, this.targetObject, this.name, newValue);
                        }
                    }
                }
            }
            catch (AccessException ex) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, new Object[] { this.name, ex.getMessage() });
            }
        }
        
        @Override
        public boolean isWritable() {
            return true;
        }
    }
    
    private class CollectionIndexingValueRef implements ValueRef
    {
        private final Collection collection;
        private final int index;
        private final TypeDescriptor collectionEntryDescriptor;
        private final TypeConverter typeConverter;
        private final boolean growCollection;
        private final int maximumSize;
        
        public CollectionIndexingValueRef(final Collection collection, final int index, final TypeDescriptor collectionEntryTypeDescriptor, final TypeConverter typeConverter, final boolean growCollection, final int maximumSize) {
            this.collection = collection;
            this.index = index;
            this.collectionEntryDescriptor = collectionEntryTypeDescriptor;
            this.typeConverter = typeConverter;
            this.growCollection = growCollection;
            this.maximumSize = maximumSize;
        }
        
        @Override
        public TypedValue getValue() {
            this.growCollectionIfNecessary();
            if (this.collection instanceof List) {
                final Object o = ((List)this.collection).get(this.index);
                return new TypedValue(o, this.collectionEntryDescriptor.elementTypeDescriptor(o));
            }
            int pos = 0;
            for (final Object o2 : this.collection) {
                if (pos == this.index) {
                    return new TypedValue(o2, this.collectionEntryDescriptor.elementTypeDescriptor(o2));
                }
                ++pos;
            }
            throw new IllegalStateException("Failed to find indexed element " + this.index + ": " + this.collection);
        }
        
        @Override
        public void setValue(Object newValue) {
            this.growCollectionIfNecessary();
            if (this.collection instanceof List) {
                final List list = (List)this.collection;
                if (this.collectionEntryDescriptor.getElementTypeDescriptor() != null) {
                    newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.collectionEntryDescriptor.getElementTypeDescriptor());
                }
                list.set(this.index, newValue);
                return;
            }
            throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.collectionEntryDescriptor.toString() });
        }
        
        private void growCollectionIfNecessary() {
            if (this.index >= this.collection.size()) {
                if (!this.growCollection) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.COLLECTION_INDEX_OUT_OF_BOUNDS, new Object[] { this.collection.size(), this.index });
                }
                if (this.index >= this.maximumSize) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
                }
                if (this.collectionEntryDescriptor.getElementTypeDescriptor() == null) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION_UNKNOWN_ELEMENT_TYPE, new Object[0]);
                }
                final TypeDescriptor elementType = this.collectionEntryDescriptor.getElementTypeDescriptor();
                try {
                    for (int newElements = this.index - this.collection.size(); newElements >= 0; --newElements) {
                        this.collection.add(elementType.getType().newInstance());
                    }
                }
                catch (Exception ex) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
                }
            }
        }
        
        @Override
        public boolean isWritable() {
            return true;
        }
    }
    
    private class StringIndexingLValue implements ValueRef
    {
        private final String target;
        private final int index;
        private final TypeDescriptor typeDescriptor;
        
        public StringIndexingLValue(final String target, final int index, final TypeDescriptor typeDescriptor) {
            this.target = target;
            this.index = index;
            this.typeDescriptor = typeDescriptor;
        }
        
        @Override
        public TypedValue getValue() {
            if (this.index >= this.target.length()) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.STRING_INDEX_OUT_OF_BOUNDS, new Object[] { this.target.length(), this.index });
            }
            return new TypedValue(String.valueOf(this.target.charAt(this.index)));
        }
        
        @Override
        public void setValue(final Object newValue) {
            throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, new Object[] { this.typeDescriptor.toString() });
        }
        
        @Override
        public boolean isWritable() {
            return true;
        }
    }
}
