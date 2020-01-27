// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.xml;

import org.springframework.oxm.MarshallingFailureException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import javax.xml.transform.Result;
import java.io.IOException;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;
import javax.xml.transform.Source;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.Marshaller;

public class MarshallingHttpMessageConverter extends AbstractXmlHttpMessageConverter<Object>
{
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    
    public MarshallingHttpMessageConverter() {
    }
    
    public MarshallingHttpMessageConverter(final Marshaller marshaller) {
        Assert.notNull(marshaller, "Marshaller must not be null");
        this.marshaller = marshaller;
        if (marshaller instanceof Unmarshaller) {
            this.unmarshaller = (Unmarshaller)marshaller;
        }
    }
    
    public MarshallingHttpMessageConverter(final Marshaller marshaller, final Unmarshaller unmarshaller) {
        Assert.notNull(marshaller, "Marshaller must not be null");
        Assert.notNull(unmarshaller, "Unmarshaller must not be null");
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }
    
    public void setMarshaller(final Marshaller marshaller) {
        this.marshaller = marshaller;
    }
    
    public void setUnmarshaller(final Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }
    
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return this.canRead(mediaType) && this.unmarshaller != null && this.unmarshaller.supports((Class)clazz);
    }
    
    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        return this.canWrite(mediaType) && this.marshaller != null && this.marshaller.supports((Class)clazz);
    }
    
    @Override
    protected boolean supports(final Class<?> clazz) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Object readFromSource(final Class<?> clazz, final HttpHeaders headers, final Source source) throws IOException {
        Assert.notNull(this.unmarshaller, "Property 'unmarshaller' is required");
        try {
            final Object result = this.unmarshaller.unmarshal(source);
            if (!clazz.isInstance(result)) {
                throw new TypeMismatchException(result, clazz);
            }
            return result;
        }
        catch (UnmarshallingFailureException ex) {
            throw new HttpMessageNotReadableException("Could not read [" + clazz + "]", (Throwable)ex);
        }
    }
    
    @Override
    protected void writeToResult(final Object o, final HttpHeaders headers, final Result result) throws IOException {
        Assert.notNull(this.marshaller, "Property 'marshaller' is required");
        try {
            this.marshaller.marshal(o, result);
        }
        catch (MarshallingFailureException ex) {
            throw new HttpMessageNotWritableException("Could not write [" + o + "]", (Throwable)ex);
        }
    }
}
