// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.export;

import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.ReflectionException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.InstanceNotFoundException;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import javax.management.modelmbean.RequiredModelMBean;

public class SpringModelMBean extends RequiredModelMBean
{
    private ClassLoader managedResourceClassLoader;
    
    public SpringModelMBean() throws MBeanException, RuntimeOperationsException {
        this.managedResourceClassLoader = Thread.currentThread().getContextClassLoader();
    }
    
    public SpringModelMBean(final ModelMBeanInfo mbi) throws MBeanException, RuntimeOperationsException {
        super(mbi);
        this.managedResourceClassLoader = Thread.currentThread().getContextClassLoader();
    }
    
    @Override
    public void setManagedResource(final Object managedResource, final String managedResourceType) throws MBeanException, InstanceNotFoundException, InvalidTargetObjectTypeException {
        this.managedResourceClassLoader = managedResource.getClass().getClassLoader();
        super.setManagedResource(managedResource, managedResourceType);
    }
    
    @Override
    public Object invoke(final String opName, final Object[] opArgs, final String[] sig) throws MBeanException, ReflectionException {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            return super.invoke(opName, opArgs, sig);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
    
    @Override
    public Object getAttribute(final String attrName) throws AttributeNotFoundException, MBeanException, ReflectionException {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            return super.getAttribute(attrName);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
    
    @Override
    public AttributeList getAttributes(final String[] attrNames) {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            return super.getAttributes(attrNames);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            super.setAttribute(attribute);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList attributes) {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
            return super.setAttributes(attributes);
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
}
