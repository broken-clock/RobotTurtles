// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import java.util.ArrayList;
import org.springframework.expression.MethodFilter;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import org.springframework.core.convert.TypeDescriptor;
import java.util.HashMap;
import org.springframework.expression.BeanResolver;
import java.util.Map;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.ConstructorResolver;
import java.util.List;
import org.springframework.expression.TypedValue;
import org.springframework.expression.EvaluationContext;

public class StandardEvaluationContext implements EvaluationContext
{
    private TypedValue rootObject;
    private List<ConstructorResolver> constructorResolvers;
    private List<MethodResolver> methodResolvers;
    private ReflectiveMethodResolver reflectiveMethodResolver;
    private List<PropertyAccessor> propertyAccessors;
    private TypeLocator typeLocator;
    private TypeConverter typeConverter;
    private TypeComparator typeComparator;
    private OperatorOverloader operatorOverloader;
    private final Map<String, Object> variables;
    private BeanResolver beanResolver;
    
    public StandardEvaluationContext() {
        this.typeComparator = new StandardTypeComparator();
        this.operatorOverloader = new StandardOperatorOverloader();
        this.variables = new HashMap<String, Object>();
        this.setRootObject(null);
    }
    
    public StandardEvaluationContext(final Object rootObject) {
        this();
        this.setRootObject(rootObject);
    }
    
    public void setRootObject(final Object rootObject, final TypeDescriptor typeDescriptor) {
        this.rootObject = new TypedValue(rootObject, typeDescriptor);
    }
    
    public void setRootObject(final Object rootObject) {
        this.rootObject = ((rootObject != null) ? new TypedValue(rootObject) : TypedValue.NULL);
    }
    
    @Override
    public TypedValue getRootObject() {
        return this.rootObject;
    }
    
    public void addConstructorResolver(final ConstructorResolver resolver) {
        this.ensureConstructorResolversInitialized();
        this.constructorResolvers.add(this.constructorResolvers.size() - 1, resolver);
    }
    
    public boolean removeConstructorResolver(final ConstructorResolver resolver) {
        this.ensureConstructorResolversInitialized();
        return this.constructorResolvers.remove(resolver);
    }
    
    public void setConstructorResolvers(final List<ConstructorResolver> constructorResolvers) {
        this.constructorResolvers = constructorResolvers;
    }
    
    @Override
    public List<ConstructorResolver> getConstructorResolvers() {
        this.ensureConstructorResolversInitialized();
        return this.constructorResolvers;
    }
    
    public void addMethodResolver(final MethodResolver resolver) {
        this.ensureMethodResolversInitialized();
        this.methodResolvers.add(this.methodResolvers.size() - 1, resolver);
    }
    
    public boolean removeMethodResolver(final MethodResolver methodResolver) {
        this.ensureMethodResolversInitialized();
        return this.methodResolvers.remove(methodResolver);
    }
    
    public void setMethodResolvers(final List<MethodResolver> methodResolvers) {
        this.methodResolvers = methodResolvers;
    }
    
    @Override
    public List<MethodResolver> getMethodResolvers() {
        this.ensureMethodResolversInitialized();
        return this.methodResolvers;
    }
    
    public void setBeanResolver(final BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }
    
    @Override
    public BeanResolver getBeanResolver() {
        return this.beanResolver;
    }
    
    public void addPropertyAccessor(final PropertyAccessor accessor) {
        this.ensurePropertyAccessorsInitialized();
        this.propertyAccessors.add(this.propertyAccessors.size() - 1, accessor);
    }
    
    public boolean removePropertyAccessor(final PropertyAccessor accessor) {
        return this.propertyAccessors.remove(accessor);
    }
    
    public void setPropertyAccessors(final List<PropertyAccessor> propertyAccessors) {
        this.propertyAccessors = propertyAccessors;
    }
    
    @Override
    public List<PropertyAccessor> getPropertyAccessors() {
        this.ensurePropertyAccessorsInitialized();
        return this.propertyAccessors;
    }
    
    public void setTypeLocator(final TypeLocator typeLocator) {
        Assert.notNull(typeLocator, "TypeLocator must not be null");
        this.typeLocator = typeLocator;
    }
    
    @Override
    public TypeLocator getTypeLocator() {
        if (this.typeLocator == null) {
            this.typeLocator = new StandardTypeLocator();
        }
        return this.typeLocator;
    }
    
    public void setTypeConverter(final TypeConverter typeConverter) {
        Assert.notNull(typeConverter, "TypeConverter must not be null");
        this.typeConverter = typeConverter;
    }
    
    @Override
    public TypeConverter getTypeConverter() {
        if (this.typeConverter == null) {
            this.typeConverter = new StandardTypeConverter();
        }
        return this.typeConverter;
    }
    
    public void setTypeComparator(final TypeComparator typeComparator) {
        Assert.notNull(typeComparator, "TypeComparator must not be null");
        this.typeComparator = typeComparator;
    }
    
    @Override
    public TypeComparator getTypeComparator() {
        return this.typeComparator;
    }
    
    public void setOperatorOverloader(final OperatorOverloader operatorOverloader) {
        Assert.notNull(operatorOverloader, "OperatorOverloader must not be null");
        this.operatorOverloader = operatorOverloader;
    }
    
    @Override
    public OperatorOverloader getOperatorOverloader() {
        return this.operatorOverloader;
    }
    
    @Override
    public void setVariable(final String name, final Object value) {
        this.variables.put(name, value);
    }
    
    public void setVariables(final Map<String, Object> variables) {
        this.variables.putAll(variables);
    }
    
    public void registerFunction(final String name, final Method method) {
        this.variables.put(name, method);
    }
    
    @Override
    public Object lookupVariable(final String name) {
        return this.variables.get(name);
    }
    
    public void registerMethodFilter(final Class<?> type, final MethodFilter filter) throws IllegalStateException {
        this.ensureMethodResolversInitialized();
        if (this.reflectiveMethodResolver != null) {
            this.reflectiveMethodResolver.registerMethodFilter(type, filter);
            return;
        }
        throw new IllegalStateException("Method filter cannot be set as the reflective method resolver is not in use");
    }
    
    private void ensurePropertyAccessorsInitialized() {
        if (this.propertyAccessors == null) {
            this.initializePropertyAccessors();
        }
    }
    
    private synchronized void initializePropertyAccessors() {
        if (this.propertyAccessors == null) {
            final List<PropertyAccessor> defaultAccessors = new ArrayList<PropertyAccessor>();
            defaultAccessors.add(new ReflectivePropertyAccessor());
            this.propertyAccessors = defaultAccessors;
        }
    }
    
    private void ensureMethodResolversInitialized() {
        if (this.methodResolvers == null) {
            this.initializeMethodResolvers();
        }
    }
    
    private synchronized void initializeMethodResolvers() {
        if (this.methodResolvers == null) {
            final List<MethodResolver> defaultResolvers = new ArrayList<MethodResolver>();
            defaultResolvers.add(this.reflectiveMethodResolver = new ReflectiveMethodResolver());
            this.methodResolvers = defaultResolvers;
        }
    }
    
    private void ensureConstructorResolversInitialized() {
        if (this.constructorResolvers == null) {
            this.initializeConstructorResolvers();
        }
    }
    
    private synchronized void initializeConstructorResolvers() {
        if (this.constructorResolvers == null) {
            final List<ConstructorResolver> defaultResolvers = new ArrayList<ConstructorResolver>();
            defaultResolvers.add(new ReflectiveConstructorResolver());
            this.constructorResolvers = defaultResolvers;
        }
    }
}
