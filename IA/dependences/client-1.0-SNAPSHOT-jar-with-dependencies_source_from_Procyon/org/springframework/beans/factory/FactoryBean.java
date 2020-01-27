// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

public interface FactoryBean<T>
{
    T getObject() throws Exception;
    
    Class<?> getObjectType();
    
    boolean isSingleton();
}
