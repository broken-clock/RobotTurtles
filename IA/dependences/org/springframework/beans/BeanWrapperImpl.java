// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import org.springframework.core.GenericCollectionTypeResolver;
import java.util.Set;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.lang.reflect.Modifier;
import java.util.List;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import org.springframework.core.CollectionFactory;
import java.util.Collection;
import java.lang.reflect.Array;
import java.util.HashMap;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import java.beans.PropertyChangeEvent;
import org.springframework.core.convert.TypeDescriptor;
import java.beans.PropertyDescriptor;
import org.springframework.util.Assert;
import java.util.Map;
import java.security.AccessControlContext;
import org.apache.commons.logging.Log;

public class BeanWrapperImpl extends AbstractPropertyAccessor implements BeanWrapper
{
    private static final Log logger;
    private Object object;
    private String nestedPath;
    private Object rootObject;
    private AccessControlContext acc;
    private CachedIntrospectionResults cachedIntrospectionResults;
    private Map<String, BeanWrapperImpl> nestedBeanWrappers;
    private boolean autoGrowNestedPaths;
    private int autoGrowCollectionLimit;
    
    public BeanWrapperImpl() {
        this(true);
    }
    
    public BeanWrapperImpl(final boolean registerDefaultEditors) {
        this.nestedPath = "";
        this.autoGrowNestedPaths = false;
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        if (registerDefaultEditors) {
            this.registerDefaultEditors();
        }
        this.typeConverterDelegate = new TypeConverterDelegate(this);
    }
    
    public BeanWrapperImpl(final Object object) {
        this.nestedPath = "";
        this.autoGrowNestedPaths = false;
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.registerDefaultEditors();
        this.setWrappedInstance(object);
    }
    
    public BeanWrapperImpl(final Class<?> clazz) {
        this.nestedPath = "";
        this.autoGrowNestedPaths = false;
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.registerDefaultEditors();
        this.setWrappedInstance(BeanUtils.instantiateClass(clazz));
    }
    
    public BeanWrapperImpl(final Object object, final String nestedPath, final Object rootObject) {
        this.nestedPath = "";
        this.autoGrowNestedPaths = false;
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.registerDefaultEditors();
        this.setWrappedInstance(object, nestedPath, rootObject);
    }
    
    private BeanWrapperImpl(final Object object, final String nestedPath, final BeanWrapperImpl superBw) {
        this.nestedPath = "";
        this.autoGrowNestedPaths = false;
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.setWrappedInstance(object, nestedPath, superBw.getWrappedInstance());
        this.setExtractOldValueForEditor(superBw.isExtractOldValueForEditor());
        this.setAutoGrowNestedPaths(superBw.isAutoGrowNestedPaths());
        this.setAutoGrowCollectionLimit(superBw.getAutoGrowCollectionLimit());
        this.setConversionService(superBw.getConversionService());
        this.setSecurityContext(superBw.acc);
    }
    
    public void setWrappedInstance(final Object object) {
        this.setWrappedInstance(object, "", null);
    }
    
    public void setWrappedInstance(final Object object, final String nestedPath, final Object rootObject) {
        Assert.notNull(object, "Bean object must not be null");
        this.object = object;
        this.nestedPath = ((nestedPath != null) ? nestedPath : "");
        this.rootObject = ("".equals(this.nestedPath) ? object : rootObject);
        this.nestedBeanWrappers = null;
        this.typeConverterDelegate = new TypeConverterDelegate(this, object);
        this.setIntrospectionClass(object.getClass());
    }
    
    @Override
    public final Object getWrappedInstance() {
        return this.object;
    }
    
    @Override
    public final Class<?> getWrappedClass() {
        return (this.object != null) ? this.object.getClass() : null;
    }
    
    public final String getNestedPath() {
        return this.nestedPath;
    }
    
    public final Object getRootInstance() {
        return this.rootObject;
    }
    
    public final Class<?> getRootClass() {
        return (this.rootObject != null) ? this.rootObject.getClass() : null;
    }
    
    @Override
    public void setAutoGrowNestedPaths(final boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }
    
    @Override
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }
    
    @Override
    public void setAutoGrowCollectionLimit(final int autoGrowCollectionLimit) {
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }
    
    @Override
    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }
    
    public void setSecurityContext(final AccessControlContext acc) {
        this.acc = acc;
    }
    
    public AccessControlContext getSecurityContext() {
        return this.acc;
    }
    
    protected void setIntrospectionClass(final Class<?> clazz) {
        if (this.cachedIntrospectionResults != null && !clazz.equals(this.cachedIntrospectionResults.getBeanClass())) {
            this.cachedIntrospectionResults = null;
        }
    }
    
    private CachedIntrospectionResults getCachedIntrospectionResults() {
        Assert.state(this.object != null, "BeanWrapper does not hold a bean instance");
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(this.getWrappedClass());
        }
        return this.cachedIntrospectionResults;
    }
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.getCachedIntrospectionResults().getPropertyDescriptors();
    }
    
    @Override
    public PropertyDescriptor getPropertyDescriptor(final String propertyName) throws BeansException {
        final PropertyDescriptor pd = this.getPropertyDescriptorInternal(propertyName);
        if (pd == null) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "No property '" + propertyName + "' found");
        }
        return pd;
    }
    
    protected PropertyDescriptor getPropertyDescriptorInternal(final String propertyName) throws BeansException {
        Assert.notNull(propertyName, "Property name must not be null");
        final BeanWrapperImpl nestedBw = this.getBeanWrapperForPropertyPath(propertyName);
        return nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(this.getFinalPath(nestedBw, propertyName));
    }
    
    @Override
    public Class<?> getPropertyType(final String propertyName) throws BeansException {
        try {
            final PropertyDescriptor pd = this.getPropertyDescriptorInternal(propertyName);
            if (pd != null) {
                return pd.getPropertyType();
            }
            final Object value = this.getPropertyValue(propertyName);
            if (value != null) {
                return value.getClass();
            }
            final Class<?> editorType = this.guessPropertyTypeFromEditors(propertyName);
            if (editorType != null) {
                return editorType;
            }
        }
        catch (InvalidPropertyException ex) {}
        return null;
    }
    
    @Override
    public TypeDescriptor getPropertyTypeDescriptor(final String propertyName) throws BeansException {
        try {
            final BeanWrapperImpl nestedBw = this.getBeanWrapperForPropertyPath(propertyName);
            final String finalPath = this.getFinalPath(nestedBw, propertyName);
            final PropertyTokenHolder tokens = this.getPropertyNameTokens(finalPath);
            final PropertyDescriptor pd = nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(tokens.actualName);
            if (pd != null) {
                if (tokens.keys != null) {
                    if (pd.getReadMethod() != null || pd.getWriteMethod() != null) {
                        return TypeDescriptor.nested(this.property(pd), tokens.keys.length);
                    }
                }
                else if (pd.getReadMethod() != null || pd.getWriteMethod() != null) {
                    return new TypeDescriptor(this.property(pd));
                }
            }
        }
        catch (InvalidPropertyException ex) {}
        return null;
    }
    
    @Override
    public boolean isReadableProperty(final String propertyName) {
        try {
            final PropertyDescriptor pd = this.getPropertyDescriptorInternal(propertyName);
            if (pd == null) {
                this.getPropertyValue(propertyName);
                return true;
            }
            if (pd.getReadMethod() != null) {
                return true;
            }
        }
        catch (InvalidPropertyException ex) {}
        return false;
    }
    
    @Override
    public boolean isWritableProperty(final String propertyName) {
        try {
            final PropertyDescriptor pd = this.getPropertyDescriptorInternal(propertyName);
            if (pd == null) {
                this.getPropertyValue(propertyName);
                return true;
            }
            if (pd.getWriteMethod() != null) {
                return true;
            }
        }
        catch (InvalidPropertyException ex) {}
        return false;
    }
    
    private Object convertIfNecessary(final String propertyName, final Object oldValue, final Object newValue, final Class<?> requiredType, final TypeDescriptor td) throws TypeMismatchException {
        try {
            return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);
        }
        catch (ConverterNotFoundException ex) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce, td.getType(), ex);
        }
        catch (ConversionException ex2) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, requiredType, ex2);
        }
        catch (IllegalStateException ex3) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce, requiredType, ex3);
        }
        catch (IllegalArgumentException ex4) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, requiredType, ex4);
        }
    }
    
    public Object convertForProperty(final Object value, final String propertyName) throws TypeMismatchException {
        final CachedIntrospectionResults cachedIntrospectionResults = this.getCachedIntrospectionResults();
        final PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "No property '" + propertyName + "' found");
        }
        TypeDescriptor td = cachedIntrospectionResults.getTypeDescriptor(pd);
        if (td == null) {
            td = new TypeDescriptor(this.property(pd));
            cachedIntrospectionResults.addTypeDescriptor(pd, td);
        }
        return this.convertForProperty(propertyName, null, value, td);
    }
    
    private Object convertForProperty(final String propertyName, final Object oldValue, final Object newValue, final TypeDescriptor td) throws TypeMismatchException {
        return this.convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);
    }
    
    private Property property(final PropertyDescriptor pd) {
        final GenericTypeAwarePropertyDescriptor typeAware = (GenericTypeAwarePropertyDescriptor)pd;
        return new Property(typeAware.getBeanClass(), typeAware.getReadMethod(), typeAware.getWriteMethod(), typeAware.getName());
    }
    
    private String getFinalPath(final BeanWrapper bw, final String nestedPath) {
        if (bw == this) {
            return nestedPath;
        }
        return nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
    }
    
    protected BeanWrapperImpl getBeanWrapperForPropertyPath(final String propertyPath) {
        final int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
        if (pos > -1) {
            final String nestedProperty = propertyPath.substring(0, pos);
            final String nestedPath = propertyPath.substring(pos + 1);
            final BeanWrapperImpl nestedBw = this.getNestedBeanWrapper(nestedProperty);
            return nestedBw.getBeanWrapperForPropertyPath(nestedPath);
        }
        return this;
    }
    
    private BeanWrapperImpl getNestedBeanWrapper(final String nestedProperty) {
        if (this.nestedBeanWrappers == null) {
            this.nestedBeanWrappers = new HashMap<String, BeanWrapperImpl>();
        }
        final PropertyTokenHolder tokens = this.getPropertyNameTokens(nestedProperty);
        final String canonicalName = tokens.canonicalName;
        Object propertyValue = this.getPropertyValue(tokens);
        if (propertyValue == null) {
            if (!this.autoGrowNestedPaths) {
                throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + canonicalName);
            }
            propertyValue = this.setDefaultValue(tokens);
        }
        BeanWrapperImpl nestedBw = this.nestedBeanWrappers.get(canonicalName);
        if (nestedBw == null || nestedBw.getWrappedInstance() != propertyValue) {
            if (BeanWrapperImpl.logger.isTraceEnabled()) {
                BeanWrapperImpl.logger.trace("Creating new nested BeanWrapper for property '" + canonicalName + "'");
            }
            nestedBw = this.newNestedBeanWrapper(propertyValue, this.nestedPath + canonicalName + ".");
            this.copyDefaultEditorsTo(nestedBw);
            this.copyCustomEditorsTo(nestedBw, canonicalName);
            this.nestedBeanWrappers.put(canonicalName, nestedBw);
        }
        else if (BeanWrapperImpl.logger.isTraceEnabled()) {
            BeanWrapperImpl.logger.trace("Using cached nested BeanWrapper for property '" + canonicalName + "'");
        }
        return nestedBw;
    }
    
    private Object setDefaultValue(final String propertyName) {
        final PropertyTokenHolder tokens = new PropertyTokenHolder();
        tokens.actualName = propertyName;
        tokens.canonicalName = propertyName;
        return this.setDefaultValue(tokens);
    }
    
    private Object setDefaultValue(final PropertyTokenHolder tokens) {
        final PropertyValue pv = this.createDefaultPropertyValue(tokens);
        this.setPropertyValue(tokens, pv);
        return this.getPropertyValue(tokens);
    }
    
    private PropertyValue createDefaultPropertyValue(final PropertyTokenHolder tokens) {
        final Class<?> type = this.getPropertyTypeDescriptor(tokens.canonicalName).getType();
        if (type == null) {
            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Could not determine property type for auto-growing a default value");
        }
        final Object defaultValue = this.newValue(type, tokens.canonicalName);
        return new PropertyValue(tokens.canonicalName, defaultValue);
    }
    
    private Object newValue(final Class<?> type, final String name) {
        try {
            if (type.isArray()) {
                final Class<?> componentType = type.getComponentType();
                if (componentType.isArray()) {
                    final Object array = Array.newInstance(componentType, 1);
                    Array.set(array, 0, Array.newInstance(componentType.getComponentType(), 0));
                    return array;
                }
                return Array.newInstance(componentType, 0);
            }
            else {
                if (Collection.class.isAssignableFrom(type)) {
                    return CollectionFactory.createCollection(type, 16);
                }
                if (Map.class.isAssignableFrom(type)) {
                    return CollectionFactory.createMap(type, 16);
                }
                return type.newInstance();
            }
        }
        catch (Exception ex) {
            throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + name, "Could not instantiate property type [" + type.getName() + "] to auto-grow nested property path: " + ex);
        }
    }
    
    protected BeanWrapperImpl newNestedBeanWrapper(final Object object, final String nestedPath) {
        return new BeanWrapperImpl(object, nestedPath, this);
    }
    
    private PropertyTokenHolder getPropertyNameTokens(final String propertyName) {
        final PropertyTokenHolder tokens = new PropertyTokenHolder();
        String actualName = null;
        final List<String> keys = new ArrayList<String>(2);
        int searchIndex = 0;
        while (searchIndex != -1) {
            final int keyStart = propertyName.indexOf("[", searchIndex);
            searchIndex = -1;
            if (keyStart != -1) {
                final int keyEnd = propertyName.indexOf("]", keyStart + "[".length());
                if (keyEnd == -1) {
                    continue;
                }
                if (actualName == null) {
                    actualName = propertyName.substring(0, keyStart);
                }
                String key = propertyName.substring(keyStart + "[".length(), keyEnd);
                if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
                    key = key.substring(1, key.length() - 1);
                }
                keys.add(key);
                searchIndex = keyEnd + "]".length();
            }
        }
        tokens.actualName = ((actualName != null) ? actualName : propertyName);
        tokens.canonicalName = tokens.actualName;
        if (!keys.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            final PropertyTokenHolder propertyTokenHolder = tokens;
            propertyTokenHolder.canonicalName = sb.append(propertyTokenHolder.canonicalName).append("[").append(StringUtils.collectionToDelimitedString(keys, "][")).append("]").toString();
            tokens.keys = StringUtils.toStringArray(keys);
        }
        return tokens;
    }
    
    @Override
    public Object getPropertyValue(final String propertyName) throws BeansException {
        final BeanWrapperImpl nestedBw = this.getBeanWrapperForPropertyPath(propertyName);
        final PropertyTokenHolder tokens = this.getPropertyNameTokens(this.getFinalPath(nestedBw, propertyName));
        return nestedBw.getPropertyValue(tokens);
    }
    
    private Object getPropertyValue(final PropertyTokenHolder tokens) throws BeansException {
        final String propertyName = tokens.canonicalName;
        final String actualName = tokens.actualName;
        final PropertyDescriptor pd = this.getCachedIntrospectionResults().getPropertyDescriptor(actualName);
        if (pd == null || pd.getReadMethod() == null) {
            throw new NotReadablePropertyException(this.getRootClass(), this.nestedPath + propertyName);
        }
        final Method readMethod = pd.getReadMethod();
        try {
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()) && !readMethod.isAccessible()) {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                        @Override
                        public Object run() {
                            readMethod.setAccessible(true);
                            return null;
                        }
                    });
                }
                else {
                    readMethod.setAccessible(true);
                }
            }
            Object value = null;
            Label_0175: {
                if (System.getSecurityManager() != null) {
                    try {
                        value = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                            @Override
                            public Object run() throws Exception {
                                return readMethod.invoke(BeanWrapperImpl.this.object, (Object[])null);
                            }
                        }, this.acc);
                        break Label_0175;
                    }
                    catch (PrivilegedActionException pae) {
                        throw pae.getException();
                    }
                }
                value = readMethod.invoke(this.object, (Object[])null);
            }
            if (tokens.keys != null) {
                if (value == null) {
                    if (!this.autoGrowNestedPaths) {
                        throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                    }
                    value = this.setDefaultValue(tokens.actualName);
                }
                String indexedPropertyName = tokens.actualName;
                for (int i = 0; i < tokens.keys.length; ++i) {
                    final String key = tokens.keys[i];
                    if (value == null) {
                        throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                    }
                    if (value.getClass().isArray()) {
                        final int index = Integer.parseInt(key);
                        value = this.growArrayIfNecessary(value, index, indexedPropertyName);
                        value = Array.get(value, index);
                    }
                    else if (value instanceof List) {
                        final int index = Integer.parseInt(key);
                        final List<Object> list = (List<Object>)value;
                        this.growCollectionIfNecessary(list, index, indexedPropertyName, pd, i + 1);
                        value = list.get(index);
                    }
                    else if (value instanceof Set) {
                        final Set<Object> set = (Set<Object>)value;
                        final int index2 = Integer.parseInt(key);
                        if (index2 < 0 || index2 >= set.size()) {
                            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Cannot get element with index " + index2 + " from Set of size " + set.size() + ", accessed using property path '" + propertyName + "'");
                        }
                        final Iterator<Object> it = set.iterator();
                        int j = 0;
                        while (it.hasNext()) {
                            final Object elem = it.next();
                            if (j == index2) {
                                value = elem;
                                break;
                            }
                            ++j;
                        }
                    }
                    else {
                        if (!(value instanceof Map)) {
                            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Property referenced in indexed property path '" + propertyName + "' is neither an array nor a List nor a Set nor a Map; returned value was [" + value + "]");
                        }
                        final Map<Object, Object> map = (Map<Object, Object>)value;
                        final Class<?> mapKeyType = GenericCollectionTypeResolver.getMapKeyReturnType(pd.getReadMethod(), i + 1);
                        final TypeDescriptor typeDescriptor = (mapKeyType != null) ? TypeDescriptor.valueOf(mapKeyType) : TypeDescriptor.valueOf(Object.class);
                        final Object convertedMapKey = this.convertIfNecessary(null, null, key, mapKeyType, typeDescriptor);
                        value = map.get(convertedMapKey);
                    }
                    indexedPropertyName = indexedPropertyName + "[" + key + "]";
                }
            }
            return value;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Index of out of bounds in property path '" + propertyName + "'", ex);
        }
        catch (NumberFormatException ex2) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '" + propertyName + "'", ex2);
        }
        catch (TypeMismatchException ex3) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '" + propertyName + "'", ex3);
        }
        catch (InvocationTargetException ex4) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Getter for property '" + actualName + "' threw exception", ex4);
        }
        catch (Exception ex5) {
            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Illegal attempt to get property '" + actualName + "' threw exception", ex5);
        }
    }
    
    private Object growArrayIfNecessary(final Object array, final int index, final String name) {
        if (!this.autoGrowNestedPaths) {
            return array;
        }
        final int length = Array.getLength(array);
        if (index >= length && index < this.autoGrowCollectionLimit) {
            final Class<?> componentType = array.getClass().getComponentType();
            final Object newArray = Array.newInstance(componentType, index + 1);
            System.arraycopy(array, 0, newArray, 0, length);
            for (int i = length; i < Array.getLength(newArray); ++i) {
                Array.set(newArray, i, this.newValue(componentType, name));
            }
            this.setPropertyValue(name, newArray);
            return this.getPropertyValue(name);
        }
        return array;
    }
    
    private void growCollectionIfNecessary(final Collection<Object> collection, final int index, final String name, final PropertyDescriptor pd, final int nestingLevel) {
        if (!this.autoGrowNestedPaths) {
            return;
        }
        final int size = collection.size();
        if (index >= size && index < this.autoGrowCollectionLimit) {
            final Class<?> elementType = GenericCollectionTypeResolver.getCollectionReturnType(pd.getReadMethod(), nestingLevel);
            if (elementType != null) {
                for (int i = collection.size(); i < index + 1; ++i) {
                    collection.add(this.newValue(elementType, name));
                }
            }
        }
    }
    
    @Override
    public void setPropertyValue(final String propertyName, final Object value) throws BeansException {
        BeanWrapperImpl nestedBw;
        try {
            nestedBw = this.getBeanWrapperForPropertyPath(propertyName);
        }
        catch (NotReadablePropertyException ex) {
            throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
        }
        final PropertyTokenHolder tokens = this.getPropertyNameTokens(this.getFinalPath(nestedBw, propertyName));
        nestedBw.setPropertyValue(tokens, new PropertyValue(propertyName, value));
    }
    
    @Override
    public void setPropertyValue(final PropertyValue pv) throws BeansException {
        PropertyTokenHolder tokens = (PropertyTokenHolder)pv.resolvedTokens;
        if (tokens == null) {
            final String propertyName = pv.getName();
            BeanWrapperImpl nestedBw;
            try {
                nestedBw = this.getBeanWrapperForPropertyPath(propertyName);
            }
            catch (NotReadablePropertyException ex) {
                throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
            }
            tokens = this.getPropertyNameTokens(this.getFinalPath(nestedBw, propertyName));
            if (nestedBw == this) {
                pv.getOriginalPropertyValue().resolvedTokens = tokens;
            }
            nestedBw.setPropertyValue(tokens, pv);
        }
        else {
            this.setPropertyValue(tokens, pv);
        }
    }
    
    private void setPropertyValue(final PropertyTokenHolder tokens, final PropertyValue pv) throws BeansException {
        final String propertyName = tokens.canonicalName;
        final String actualName = tokens.actualName;
        if (tokens.keys != null) {
            final PropertyTokenHolder getterTokens = new PropertyTokenHolder();
            getterTokens.canonicalName = tokens.canonicalName;
            getterTokens.actualName = tokens.actualName;
            getterTokens.keys = new String[tokens.keys.length - 1];
            System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
            Object propValue;
            try {
                propValue = this.getPropertyValue(getterTokens);
            }
            catch (NotReadablePropertyException ex) {
                throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value in property referenced in indexed property path '" + propertyName + "'", ex);
            }
            final String key = tokens.keys[tokens.keys.length - 1];
            if (propValue == null) {
                if (!this.autoGrowNestedPaths) {
                    throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value in property referenced in indexed property path '" + propertyName + "': returned null");
                }
                final int lastKeyIndex = tokens.canonicalName.lastIndexOf(91);
                getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);
                propValue = this.setDefaultValue(getterTokens);
            }
            if (propValue.getClass().isArray()) {
                final PropertyDescriptor pd = this.getCachedIntrospectionResults().getPropertyDescriptor(actualName);
                final Class<?> requiredType = propValue.getClass().getComponentType();
                final int arrayIndex = Integer.parseInt(key);
                Object oldValue = null;
                try {
                    if (this.isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {
                        oldValue = Array.get(propValue, arrayIndex);
                    }
                    final Object convertedValue = this.convertIfNecessary(propertyName, oldValue, pv.getValue(), requiredType, TypeDescriptor.nested(this.property(pd), tokens.keys.length));
                    Array.set(propValue, arrayIndex, convertedValue);
                }
                catch (IndexOutOfBoundsException ex2) {
                    throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Invalid array index in property path '" + propertyName + "'", ex2);
                }
            }
            else if (propValue instanceof List) {
                final PropertyDescriptor pd = this.getCachedIntrospectionResults().getPropertyDescriptor(actualName);
                final Class<?> requiredType = GenericCollectionTypeResolver.getCollectionReturnType(pd.getReadMethod(), tokens.keys.length);
                final List<Object> list = (List<Object>)propValue;
                final int index = Integer.parseInt(key);
                Object oldValue2 = null;
                if (this.isExtractOldValueForEditor() && index < list.size()) {
                    oldValue2 = list.get(index);
                }
                final Object convertedValue2 = this.convertIfNecessary(propertyName, oldValue2, pv.getValue(), requiredType, TypeDescriptor.nested(this.property(pd), tokens.keys.length));
                final int size = list.size();
                if (index >= size && index < this.autoGrowCollectionLimit) {
                    for (int i = size; i < index; ++i) {
                        try {
                            list.add(null);
                        }
                        catch (NullPointerException ex9) {
                            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Cannot set element with index " + index + " in List of size " + size + ", accessed using property path '" + propertyName + "': List does not support filling up gaps with null elements");
                        }
                    }
                    list.add(convertedValue2);
                }
                else {
                    try {
                        list.set(index, convertedValue2);
                    }
                    catch (IndexOutOfBoundsException ex3) {
                        throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Invalid list index in property path '" + propertyName + "'", ex3);
                    }
                }
            }
            else {
                if (!(propValue instanceof Map)) {
                    throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + propertyName, "Property referenced in indexed property path '" + propertyName + "' is neither an array nor a List nor a Map; returned value was [" + pv.getValue() + "]");
                }
                final PropertyDescriptor pd = this.getCachedIntrospectionResults().getPropertyDescriptor(actualName);
                final Class<?> mapKeyType = GenericCollectionTypeResolver.getMapKeyReturnType(pd.getReadMethod(), tokens.keys.length);
                final Class<?> mapValueType = GenericCollectionTypeResolver.getMapValueReturnType(pd.getReadMethod(), tokens.keys.length);
                final Map<Object, Object> map = (Map<Object, Object>)propValue;
                final TypeDescriptor typeDescriptor = (mapKeyType != null) ? TypeDescriptor.valueOf(mapKeyType) : TypeDescriptor.valueOf(Object.class);
                final Object convertedMapKey = this.convertIfNecessary(null, null, key, mapKeyType, typeDescriptor);
                Object oldValue3 = null;
                if (this.isExtractOldValueForEditor()) {
                    oldValue3 = map.get(convertedMapKey);
                }
                final Object convertedMapValue = this.convertIfNecessary(propertyName, oldValue3, pv.getValue(), mapValueType, TypeDescriptor.nested(this.property(pd), tokens.keys.length));
                map.put(convertedMapKey, convertedMapValue);
            }
        }
        else {
            PropertyDescriptor pd2 = pv.resolvedDescriptor;
            if (pd2 == null || !pd2.getWriteMethod().getDeclaringClass().isInstance(this.object)) {
                pd2 = this.getCachedIntrospectionResults().getPropertyDescriptor(actualName);
                if (pd2 == null || pd2.getWriteMethod() == null) {
                    if (pv.isOptional()) {
                        BeanWrapperImpl.logger.debug("Ignoring optional value for property '" + actualName + "' - property not found on bean class [" + this.getRootClass().getName() + "]");
                        return;
                    }
                    final PropertyMatches matches = PropertyMatches.forProperty(propertyName, this.getRootClass());
                    throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, matches.buildErrorMessage(), matches.getPossibleMatches());
                }
                else {
                    pv.getOriginalPropertyValue().resolvedDescriptor = pd2;
                }
            }
            Object oldValue4 = null;
            try {
                Object valueToApply;
                final Object originalValue = valueToApply = pv.getValue();
                if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
                    if (pv.isConverted()) {
                        valueToApply = pv.getConvertedValue();
                    }
                    else {
                        if (this.isExtractOldValueForEditor() && pd2.getReadMethod() != null) {
                            final Method readMethod = pd2.getReadMethod();
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()) && !readMethod.isAccessible()) {
                                if (System.getSecurityManager() != null) {
                                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                                        @Override
                                        public Object run() {
                                            readMethod.setAccessible(true);
                                            return null;
                                        }
                                    });
                                }
                                else {
                                    readMethod.setAccessible(true);
                                }
                            }
                            try {
                                if (System.getSecurityManager() != null) {
                                    oldValue4 = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                                        @Override
                                        public Object run() throws Exception {
                                            return readMethod.invoke(BeanWrapperImpl.this.object, new Object[0]);
                                        }
                                    }, this.acc);
                                }
                                else {
                                    oldValue4 = readMethod.invoke(this.object, new Object[0]);
                                }
                            }
                            catch (Exception ex4) {
                                if (ex4 instanceof PrivilegedActionException) {
                                    ex4 = ((PrivilegedActionException)ex4).getException();
                                }
                                if (BeanWrapperImpl.logger.isDebugEnabled()) {
                                    BeanWrapperImpl.logger.debug("Could not read previous value of property '" + this.nestedPath + propertyName + "'", ex4);
                                }
                            }
                        }
                        valueToApply = this.convertForProperty(propertyName, oldValue4, originalValue, new TypeDescriptor(this.property(pd2)));
                    }
                    pv.getOriginalPropertyValue().conversionNecessary = (valueToApply != originalValue);
                }
                final Method writeMethod = (pd2 instanceof GenericTypeAwarePropertyDescriptor) ? ((GenericTypeAwarePropertyDescriptor)pd2).getWriteMethodForActualAccess() : pd2.getWriteMethod();
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers()) && !writeMethod.isAccessible()) {
                    if (System.getSecurityManager() != null) {
                        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                            @Override
                            public Object run() {
                                writeMethod.setAccessible(true);
                                return null;
                            }
                        });
                    }
                    else {
                        writeMethod.setAccessible(true);
                    }
                }
                final Object value = valueToApply;
                if (System.getSecurityManager() != null) {
                    try {
                        AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                            @Override
                            public Object run() throws Exception {
                                writeMethod.invoke(BeanWrapperImpl.this.object, value);
                                return null;
                            }
                        }, this.acc);
                        return;
                    }
                    catch (PrivilegedActionException ex5) {
                        throw ex5.getException();
                    }
                }
                writeMethod.invoke(this.object, value);
            }
            catch (TypeMismatchException ex6) {
                throw ex6;
            }
            catch (InvocationTargetException ex7) {
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue4, pv.getValue());
                if (ex7.getTargetException() instanceof ClassCastException) {
                    throw new TypeMismatchException(propertyChangeEvent, pd2.getPropertyType(), ex7.getTargetException());
                }
                throw new MethodInvocationException(propertyChangeEvent, ex7.getTargetException());
            }
            catch (Exception ex8) {
                final PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue4, pv.getValue());
                throw new MethodInvocationException(pce, ex8);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        if (this.object != null) {
            sb.append(": wrapping object [").append(ObjectUtils.identityToString(this.object)).append("]");
        }
        else {
            sb.append(": no wrapped object set");
        }
        return sb.toString();
    }
    
    static {
        logger = LogFactory.getLog(BeanWrapperImpl.class);
    }
    
    private static class PropertyTokenHolder
    {
        public String canonicalName;
        public String actualName;
        public String[] keys;
    }
}
