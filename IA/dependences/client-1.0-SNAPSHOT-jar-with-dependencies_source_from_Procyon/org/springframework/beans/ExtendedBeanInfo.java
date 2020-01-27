// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.awt.Image;
import java.beans.EventSetDescriptor;
import java.beans.BeanDescriptor;
import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import java.beans.IndexedPropertyDescriptor;
import java.util.Comparator;
import java.util.TreeSet;
import java.beans.PropertyDescriptor;
import java.util.Set;
import java.beans.BeanInfo;

class ExtendedBeanInfo implements BeanInfo
{
    private final BeanInfo delegate;
    private final Set<PropertyDescriptor> propertyDescriptors;
    
    public ExtendedBeanInfo(final BeanInfo delegate) throws IntrospectionException {
        this.propertyDescriptors = new TreeSet<PropertyDescriptor>(new PropertyDescriptorComparator());
        this.delegate = delegate;
        for (final PropertyDescriptor pd : delegate.getPropertyDescriptors()) {
            this.propertyDescriptors.add((pd instanceof IndexedPropertyDescriptor) ? new SimpleIndexedPropertyDescriptor((IndexedPropertyDescriptor)pd) : new SimplePropertyDescriptor(pd));
        }
        final MethodDescriptor[] methodDescriptors = delegate.getMethodDescriptors();
        if (methodDescriptors != null) {
            for (final Method method : this.findCandidateWriteMethods(methodDescriptors)) {
                this.handleCandidateWriteMethod(method);
            }
        }
    }
    
    private List<Method> findCandidateWriteMethods(final MethodDescriptor[] methodDescriptors) {
        final List<Method> matches = new ArrayList<Method>();
        for (final MethodDescriptor methodDescriptor : methodDescriptors) {
            final Method method = methodDescriptor.getMethod();
            if (isCandidateWriteMethod(method)) {
                matches.add(method);
            }
        }
        Collections.sort(matches, new Comparator<Method>() {
            @Override
            public int compare(final Method m1, final Method m2) {
                return m2.toString().compareTo(m1.toString());
            }
        });
        return matches;
    }
    
    public static boolean isCandidateWriteMethod(final Method method) {
        final String methodName = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final int nParams = parameterTypes.length;
        return methodName.length() > 3 && methodName.startsWith("set") && Modifier.isPublic(method.getModifiers()) && (!Void.TYPE.isAssignableFrom(method.getReturnType()) || Modifier.isStatic(method.getModifiers())) && (nParams == 1 || (nParams == 2 && parameterTypes[0].equals(Integer.TYPE)));
    }
    
    private void handleCandidateWriteMethod(final Method method) throws IntrospectionException {
        final int nParams = method.getParameterTypes().length;
        final String propertyName = this.propertyNameFor(method);
        final Class<?> propertyType = method.getParameterTypes()[nParams - 1];
        final PropertyDescriptor existingPd = this.findExistingPropertyDescriptor(propertyName, propertyType);
        if (nParams == 1) {
            if (existingPd == null) {
                this.propertyDescriptors.add(new SimplePropertyDescriptor(propertyName, null, method));
            }
            else {
                existingPd.setWriteMethod(method);
            }
        }
        else {
            if (nParams != 2) {
                throw new IllegalArgumentException("Write method must have exactly 1 or 2 parameters: " + method);
            }
            if (existingPd == null) {
                this.propertyDescriptors.add(new SimpleIndexedPropertyDescriptor(propertyName, null, null, null, method));
            }
            else if (existingPd instanceof IndexedPropertyDescriptor) {
                ((IndexedPropertyDescriptor)existingPd).setIndexedWriteMethod(method);
            }
            else {
                this.propertyDescriptors.remove(existingPd);
                this.propertyDescriptors.add(new SimpleIndexedPropertyDescriptor(propertyName, existingPd.getReadMethod(), existingPd.getWriteMethod(), null, method));
            }
        }
    }
    
    private PropertyDescriptor findExistingPropertyDescriptor(final String propertyName, final Class<?> propertyType) {
        for (final PropertyDescriptor pd : this.propertyDescriptors) {
            final String candidateName = pd.getName();
            if (pd instanceof IndexedPropertyDescriptor) {
                final IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;
                final Class<?> candidateType = ipd.getIndexedPropertyType();
                if (candidateName.equals(propertyName) && (candidateType.equals(propertyType) || candidateType.equals(propertyType.getComponentType()))) {
                    return pd;
                }
                continue;
            }
            else {
                final Class<?> candidateType = pd.getPropertyType();
                if (candidateName.equals(propertyName) && (candidateType.equals(propertyType) || propertyType.equals(candidateType.getComponentType()))) {
                    return pd;
                }
                continue;
            }
        }
        return null;
    }
    
    private String propertyNameFor(final Method method) {
        return Introspector.decapitalize(method.getName().substring(3, method.getName().length()));
    }
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.propertyDescriptors.toArray(new PropertyDescriptor[this.propertyDescriptors.size()]);
    }
    
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return this.delegate.getAdditionalBeanInfo();
    }
    
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return this.delegate.getBeanDescriptor();
    }
    
    @Override
    public int getDefaultEventIndex() {
        return this.delegate.getDefaultEventIndex();
    }
    
    @Override
    public int getDefaultPropertyIndex() {
        return this.delegate.getDefaultPropertyIndex();
    }
    
    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return this.delegate.getEventSetDescriptors();
    }
    
    @Override
    public Image getIcon(final int iconKind) {
        return this.delegate.getIcon(iconKind);
    }
    
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return this.delegate.getMethodDescriptors();
    }
}
