// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.w3c.dom.Document;
import org.springframework.core.env.Environment;

public interface BeanDefinitionDocumentReader
{
    void setEnvironment(final Environment p0);
    
    void registerBeanDefinitions(final Document p0, final XmlReaderContext p1) throws BeanDefinitionStoreException;
}
