// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Modifier;
import org.springframework.core.CollectionFactory;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import org.springframework.core.convert.ConversionService;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.Map;
import org.springframework.util.StringUtils;
import java.util.Collection;
import org.springframework.util.ClassUtils;
import org.springframework.core.convert.ConversionFailedException;
import java.lang.reflect.Field;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.MethodParameter;
import org.apache.commons.logging.Log;

class TypeConverterDelegate
{
    private static final Log logger;
    private final PropertyEditorRegistrySupport propertyEditorRegistry;
    private final Object targetObject;
    
    public TypeConverterDelegate(final PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }
    
    public TypeConverterDelegate(final PropertyEditorRegistrySupport propertyEditorRegistry, final Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }
    
    public <T> T convertIfNecessary(final Object newValue, final Class<T> requiredType, final MethodParameter methodParam) throws IllegalArgumentException {
        return this.convertIfNecessary(null, null, newValue, requiredType, (methodParam != null) ? new TypeDescriptor(methodParam) : TypeDescriptor.valueOf(requiredType));
    }
    
    public <T> T convertIfNecessary(final Object newValue, final Class<T> requiredType, final Field field) throws IllegalArgumentException {
        return this.convertIfNecessary(null, null, newValue, requiredType, (field != null) ? new TypeDescriptor(field) : TypeDescriptor.valueOf(requiredType));
    }
    
    public <T> T convertIfNecessary(final String propertyName, final Object oldValue, final Object newValue, final Class<T> requiredType) throws IllegalArgumentException {
        return this.convertIfNecessary(propertyName, oldValue, newValue, requiredType, TypeDescriptor.valueOf(requiredType));
    }
    
    public <T> T convertIfNecessary(final String propertyName, final Object oldValue, final Object newValue, final Class<T> requiredType, final TypeDescriptor typeDescriptor) throws IllegalArgumentException {
        Object convertedValue = newValue;
        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);
        ConversionFailedException firstAttemptEx = null;
        final ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && convertedValue != null && typeDescriptor != null) {
            final TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
            final TypeDescriptor targetTypeDesc = typeDescriptor;
            if (conversionService.canConvert(sourceTypeDesc, targetTypeDesc)) {
                try {
                    return (T)conversionService.convert(convertedValue, sourceTypeDesc, targetTypeDesc);
                }
                catch (ConversionFailedException ex) {
                    firstAttemptEx = ex;
                }
            }
        }
        if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
            if (requiredType != null && Collection.class.isAssignableFrom(requiredType) && convertedValue instanceof String) {
                final TypeDescriptor elementType = typeDescriptor.getElementTypeDescriptor();
                if (elementType != null && Enum.class.isAssignableFrom(elementType.getType())) {
                    convertedValue = StringUtils.commaDelimitedListToStringArray((String)convertedValue);
                }
            }
            if (editor == null) {
                editor = this.findDefaultEditor(requiredType);
            }
            convertedValue = this.doConvertValue(oldValue, convertedValue, requiredType, editor);
        }
        boolean standardConversion = false;
        if (requiredType != null) {
            if (convertedValue != null) {
                if (Object.class.equals(requiredType)) {
                    return (T)convertedValue;
                }
                if (requiredType.isArray()) {
                    if (convertedValue instanceof String && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray((String)convertedValue);
                    }
                    return (T)this.convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                }
                if (convertedValue instanceof Collection) {
                    convertedValue = this.convertToTypedCollection((Collection<?>)convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                }
                else if (convertedValue instanceof Map) {
                    convertedValue = this.convertToTypedMap((Map<?, ?>)convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                }
                if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                    convertedValue = Array.get(convertedValue, 0);
                    standardConversion = true;
                }
                if (String.class.equals(requiredType) && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    return (T)convertedValue.toString();
                }
                if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                    if (firstAttemptEx == null && !requiredType.isInterface() && !requiredType.isEnum()) {
                        try {
                            final Constructor<T> strCtor = requiredType.getConstructor(String.class);
                            return BeanUtils.instantiateClass(strCtor, convertedValue);
                        }
                        catch (NoSuchMethodException ex2) {
                            if (TypeConverterDelegate.logger.isTraceEnabled()) {
                                TypeConverterDelegate.logger.trace("No String constructor found on type [" + requiredType.getName() + "]", ex2);
                            }
                        }
                        catch (Exception ex3) {
                            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                                TypeConverterDelegate.logger.debug("Construction via String failed for type [" + requiredType.getName() + "]", ex3);
                            }
                        }
                    }
                    final String trimmedValue = ((String)convertedValue).trim();
                    if (requiredType.isEnum() && "".equals(trimmedValue)) {
                        return null;
                    }
                    convertedValue = this.attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                    standardConversion = true;
                }
            }
            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                if (firstAttemptEx != null) {
                    throw firstAttemptEx;
                }
                final StringBuilder msg = new StringBuilder();
                msg.append("Cannot convert value of type [").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("] to required type [").append(ClassUtils.getQualifiedName(requiredType)).append("]");
                if (propertyName != null) {
                    msg.append(" for property '").append(propertyName).append("'");
                }
                if (editor != null) {
                    msg.append(": PropertyEditor [").append(editor.getClass().getName()).append("] returned inappropriate value of type [").append(ClassUtils.getDescriptiveType(convertedValue)).append("]");
                    throw new IllegalArgumentException(msg.toString());
                }
                msg.append(": no matching editors or conversion strategy found");
                throw new IllegalStateException(msg.toString());
            }
        }
        if (firstAttemptEx != null) {
            if (editor == null && !standardConversion && requiredType != null && !Object.class.equals(requiredType)) {
                throw firstAttemptEx;
            }
            TypeConverterDelegate.logger.debug("Original ConversionService attempt failed - ignored since PropertyEditor based conversion eventually succeeded", firstAttemptEx);
        }
        return (T)convertedValue;
    }
    
    private Object attemptToConvertStringToEnum(final Class<?> requiredType, final String trimmedValue, final Object currentConvertedValue) {
        Object convertedValue = currentConvertedValue;
        if (Enum.class.equals(requiredType)) {
            final int index = trimmedValue.lastIndexOf(".");
            if (index > -1) {
                final String enumType = trimmedValue.substring(0, index);
                final String fieldName = trimmedValue.substring(index + 1);
                final ClassLoader loader = this.targetObject.getClass().getClassLoader();
                try {
                    final Class<?> enumValueType = loader.loadClass(enumType);
                    final Field enumField = enumValueType.getField(fieldName);
                    convertedValue = enumField.get(null);
                }
                catch (ClassNotFoundException ex) {
                    if (TypeConverterDelegate.logger.isTraceEnabled()) {
                        TypeConverterDelegate.logger.trace("Enum class [" + enumType + "] cannot be loaded from [" + loader + "]", ex);
                    }
                }
                catch (Throwable ex2) {
                    if (TypeConverterDelegate.logger.isTraceEnabled()) {
                        TypeConverterDelegate.logger.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", ex2);
                    }
                }
            }
        }
        if (convertedValue == currentConvertedValue) {
            try {
                final Field enumField2 = requiredType.getField(trimmedValue);
                convertedValue = enumField2.get(null);
            }
            catch (Throwable ex3) {
                if (TypeConverterDelegate.logger.isTraceEnabled()) {
                    TypeConverterDelegate.logger.trace("Field [" + convertedValue + "] isn't an enum value", ex3);
                }
            }
        }
        return convertedValue;
    }
    
    private PropertyEditor findDefaultEditor(final Class<?> requiredType) {
        PropertyEditor editor = null;
        if (requiredType != null) {
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
            if (editor == null && !String.class.equals(requiredType)) {
                editor = BeanUtils.findEditorByConvention(requiredType);
            }
        }
        return editor;
    }
    
    private Object doConvertValue(final Object oldValue, final Object newValue, final Class<?> requiredType, PropertyEditor editor) {
        Object convertedValue = newValue;
        if (editor != null && !(convertedValue instanceof String)) {
            try {
                editor.setValue(convertedValue);
                final Object newConvertedValue = editor.getValue();
                if (newConvertedValue != convertedValue) {
                    convertedValue = newConvertedValue;
                    editor = null;
                }
            }
            catch (Exception ex) {
                if (TypeConverterDelegate.logger.isDebugEnabled()) {
                    TypeConverterDelegate.logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
                }
            }
        }
        Object returnValue = convertedValue;
        if (requiredType != null && !requiredType.isArray() && convertedValue instanceof String[]) {
            if (TypeConverterDelegate.logger.isTraceEnabled()) {
                TypeConverterDelegate.logger.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
            }
            convertedValue = StringUtils.arrayToCommaDelimitedString((Object[])convertedValue);
        }
        if (convertedValue instanceof String) {
            if (editor != null) {
                if (TypeConverterDelegate.logger.isTraceEnabled()) {
                    TypeConverterDelegate.logger.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
                }
                final String newTextValue = (String)convertedValue;
                return this.doConvertTextValue(oldValue, newTextValue, editor);
            }
            if (String.class.equals(requiredType)) {
                returnValue = convertedValue;
            }
        }
        return returnValue;
    }
    
    private Object doConvertTextValue(final Object oldValue, final String newTextValue, final PropertyEditor editor) {
        try {
            editor.setValue(oldValue);
        }
        catch (Exception ex) {
            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                TypeConverterDelegate.logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
            }
        }
        editor.setAsText(newTextValue);
        return editor.getValue();
    }
    
    private Object convertToTypedArray(final Object input, final String propertyName, final Class<?> componentType) {
        if (input instanceof Collection) {
            final Collection<?> coll = (Collection<?>)input;
            final Object result = Array.newInstance(componentType, coll.size());
            int i = 0;
            final Iterator<?> it = coll.iterator();
            while (it.hasNext()) {
                final Object value = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
                Array.set(result, i, value);
                ++i;
            }
            return result;
        }
        if (!input.getClass().isArray()) {
            final Object result2 = Array.newInstance(componentType, 1);
            final Object value2 = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, 0), null, input, componentType);
            Array.set(result2, 0, value2);
            return result2;
        }
        if (componentType.equals(input.getClass().getComponentType()) && !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
            return input;
        }
        final int arrayLength = Array.getLength(input);
        final Object result = Array.newInstance(componentType, arrayLength);
        for (int i = 0; i < arrayLength; ++i) {
            final Object value3 = this.convertIfNecessary(this.buildIndexedPropertyName(propertyName, i), null, Array.get(input, i), componentType);
            Array.set(result, i, value3);
        }
        return result;
    }
    
    private Collection<?> convertToTypedCollection(final Collection<?> original, final String propertyName, final Class<?> requiredType, TypeDescriptor typeDescriptor) {
        if (!Collection.class.isAssignableFrom(requiredType)) {
            return original;
        }
        final boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
        if (!approximable && !this.canCreateCopy(requiredType)) {
            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                TypeConverterDelegate.logger.debug("Custom Collection type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Collection as-is");
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        typeDescriptor = typeDescriptor.narrow(original);
        final TypeDescriptor elementType = typeDescriptor.getElementTypeDescriptor();
        if (elementType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        Iterator<?> it;
        try {
            it = original.iterator();
            if (it == null) {
                if (TypeConverterDelegate.logger.isDebugEnabled()) {
                    TypeConverterDelegate.logger.debug("Collection of type [" + original.getClass().getName() + "] returned null Iterator - injecting original Collection as-is");
                }
                return original;
            }
        }
        catch (Throwable ex) {
            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                TypeConverterDelegate.logger.debug("Cannot access Collection of type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex);
            }
            return original;
        }
        Collection<Object> convertedCopy;
        try {
            if (approximable) {
                convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
            }
            else {
                convertedCopy = (Collection<Object>)requiredType.newInstance();
            }
        }
        catch (Throwable ex2) {
            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                TypeConverterDelegate.logger.debug("Cannot create copy of Collection type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex2);
            }
            return original;
        }
        int i = 0;
        while (it.hasNext()) {
            final Object element = it.next();
            final String indexedPropertyName = this.buildIndexedPropertyName(propertyName, i);
            final Object convertedElement = this.convertIfNecessary(indexedPropertyName, null, element, (elementType != null) ? elementType.getType() : null, elementType);
            try {
                convertedCopy.add(convertedElement);
            }
            catch (Throwable ex3) {
                if (TypeConverterDelegate.logger.isDebugEnabled()) {
                    TypeConverterDelegate.logger.debug("Collection type [" + original.getClass().getName() + "] seems to be read-only - injecting original Collection as-is: " + ex3);
                }
                return original;
            }
            originalAllowed = (originalAllowed && element == convertedElement);
            ++i;
        }
        return originalAllowed ? original : convertedCopy;
    }
    
    private Map<?, ?> convertToTypedMap(final Map<?, ?> original, final String propertyName, final Class<?> requiredType, TypeDescriptor typeDescriptor) {
        if (!Map.class.isAssignableFrom(requiredType)) {
            return original;
        }
        final boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
        if (!approximable && !this.canCreateCopy(requiredType)) {
            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                TypeConverterDelegate.logger.debug("Custom Map type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Map as-is");
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        typeDescriptor = typeDescriptor.narrow(original);
        final TypeDescriptor keyType = typeDescriptor.getMapKeyTypeDescriptor();
        final TypeDescriptor valueType = typeDescriptor.getMapValueTypeDescriptor();
        if (keyType == null && valueType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        Iterator<?> it;
        try {
            it = original.entrySet().iterator();
            if (it == null) {
                if (TypeConverterDelegate.logger.isDebugEnabled()) {
                    TypeConverterDelegate.logger.debug("Map of type [" + original.getClass().getName() + "] returned null Iterator - injecting original Map as-is");
                }
                return original;
            }
        }
        catch (Throwable ex) {
            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                TypeConverterDelegate.logger.debug("Cannot access Map of type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex);
            }
            return original;
        }
        Map<Object, Object> convertedCopy;
        try {
            if (approximable) {
                convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
            }
            else {
                convertedCopy = (Map<Object, Object>)requiredType.newInstance();
            }
        }
        catch (Throwable ex2) {
            if (TypeConverterDelegate.logger.isDebugEnabled()) {
                TypeConverterDelegate.logger.debug("Cannot create copy of Map type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex2);
            }
            return original;
        }
        while (it.hasNext()) {
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)it.next();
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            final String keyedPropertyName = this.buildKeyedPropertyName(propertyName, key);
            final Object convertedKey = this.convertIfNecessary(keyedPropertyName, null, key, (keyType != null) ? keyType.getType() : null, keyType);
            final Object convertedValue = this.convertIfNecessary(keyedPropertyName, null, value, (valueType != null) ? valueType.getType() : null, valueType);
            try {
                convertedCopy.put(convertedKey, convertedValue);
            }
            catch (Throwable ex3) {
                if (TypeConverterDelegate.logger.isDebugEnabled()) {
                    TypeConverterDelegate.logger.debug("Map type [" + original.getClass().getName() + "] seems to be read-only - injecting original Map as-is: " + ex3);
                }
                return original;
            }
            originalAllowed = (originalAllowed && key == convertedKey && value == convertedValue);
        }
        return originalAllowed ? original : convertedCopy;
    }
    
    private String buildIndexedPropertyName(final String propertyName, final int index) {
        return (propertyName != null) ? (propertyName + "[" + index + "]") : null;
    }
    
    private String buildKeyedPropertyName(final String propertyName, final Object key) {
        return (propertyName != null) ? (propertyName + "[" + key + "]") : null;
    }
    
    private boolean canCreateCopy(final Class<?> requiredType) {
        return !requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) && Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType, (Class<?>[])new Class[0]);
    }
    
    static {
        logger = LogFactory.getLog(TypeConverterDelegate.class);
    }
}
