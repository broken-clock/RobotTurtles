// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.springframework.beans.factory.BeanDefinitionStoreException;

public class XmlBeanDefinitionStoreException extends BeanDefinitionStoreException
{
    public XmlBeanDefinitionStoreException(final String resourceDescription, final String msg, final SAXException cause) {
        super(resourceDescription, msg, cause);
    }
    
    public int getLineNumber() {
        final Throwable cause = this.getCause();
        if (cause instanceof SAXParseException) {
            return ((SAXParseException)cause).getLineNumber();
        }
        return -1;
    }
}
