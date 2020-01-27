// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.IntrospectionException;
import java.beans.BeanInfo;

public interface BeanInfoFactory
{
    BeanInfo getBeanInfo(final Class<?> p0) throws IntrospectionException;
}
