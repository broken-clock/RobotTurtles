// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.xml;

import org.springframework.util.Assert;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import org.springframework.http.converter.HttpMessageConversionException;
import javax.xml.bind.Marshaller;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.bind.JAXBContext;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractJaxb2HttpMessageConverter<T> extends AbstractXmlHttpMessageConverter<T>
{
    private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts;
    
    public AbstractJaxb2HttpMessageConverter() {
        this.jaxbContexts = new ConcurrentHashMap<Class<?>, JAXBContext>(64);
    }
    
    protected final Marshaller createMarshaller(final Class<?> clazz) {
        try {
            final JAXBContext jaxbContext = this.getJaxbContext(clazz);
            return jaxbContext.createMarshaller();
        }
        catch (JAXBException ex) {
            throw new HttpMessageConversionException("Could not create Marshaller for class [" + clazz + "]: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    protected final Unmarshaller createUnmarshaller(final Class<?> clazz) throws JAXBException {
        try {
            final JAXBContext jaxbContext = this.getJaxbContext(clazz);
            return jaxbContext.createUnmarshaller();
        }
        catch (JAXBException ex) {
            throw new HttpMessageConversionException("Could not create Unmarshaller for class [" + clazz + "]: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    protected final JAXBContext getJaxbContext(final Class<?> clazz) {
        Assert.notNull(clazz, "'clazz' must not be null");
        JAXBContext jaxbContext = this.jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(new Class[] { clazz });
                this.jaxbContexts.putIfAbsent(clazz, jaxbContext);
            }
            catch (JAXBException ex) {
                throw new HttpMessageConversionException("Could not instantiate JAXBContext for class [" + clazz + "]: " + ex.getMessage(), (Throwable)ex);
            }
        }
        return jaxbContext;
    }
}
