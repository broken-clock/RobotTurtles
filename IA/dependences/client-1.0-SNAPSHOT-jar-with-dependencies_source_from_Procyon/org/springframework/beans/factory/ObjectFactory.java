// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

public interface ObjectFactory<T>
{
    T getObject() throws BeansException;
}
