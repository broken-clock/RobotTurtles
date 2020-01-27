// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.xml;

import org.springframework.http.HttpHeaders;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import javax.xml.transform.TransformerFactory;
import org.springframework.http.converter.AbstractHttpMessageConverter;

public abstract class AbstractXmlHttpMessageConverter<T> extends AbstractHttpMessageConverter<T>
{
    private final TransformerFactory transformerFactory;
    
    protected AbstractXmlHttpMessageConverter() {
        super(new MediaType[] { MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml") });
        this.transformerFactory = TransformerFactory.newInstance();
    }
    
    public final T readInternal(final Class<? extends T> clazz, final HttpInputMessage inputMessage) throws IOException {
        return this.readFromSource(clazz, inputMessage.getHeaders(), new StreamSource(inputMessage.getBody()));
    }
    
    @Override
    protected final void writeInternal(final T t, final HttpOutputMessage outputMessage) throws IOException {
        this.writeToResult(t, outputMessage.getHeaders(), new StreamResult(outputMessage.getBody()));
    }
    
    protected void transform(final Source source, final Result result) throws TransformerException {
        this.transformerFactory.newTransformer().transform(source, result);
    }
    
    protected abstract T readFromSource(final Class<? extends T> p0, final HttpHeaders p1, final Source p2) throws IOException;
    
    protected abstract void writeToResult(final T p0, final HttpHeaders p1, final Result p2) throws IOException;
}
