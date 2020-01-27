// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.access;

import org.springframework.beans.factory.BeanFactory;

public interface BeanFactoryReference
{
    BeanFactory getFactory();
    
    void release();
}
