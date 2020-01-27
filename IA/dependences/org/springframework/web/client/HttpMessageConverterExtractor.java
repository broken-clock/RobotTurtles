// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.Iterator;
import org.springframework.http.MediaType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.client.ClientHttpResponse;
import java.util.Collection;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.converter.HttpMessageConverter;
import java.util.List;
import java.lang.reflect.Type;

public class HttpMessageConverterExtractor<T> implements ResponseExtractor<T>
{
    private final Type responseType;
    private final Class<T> responseClass;
    private final List<HttpMessageConverter<?>> messageConverters;
    private final Log logger;
    
    public HttpMessageConverterExtractor(final Class<T> responseType, final List<HttpMessageConverter<?>> messageConverters) {
        this((Type)responseType, messageConverters);
    }
    
    public HttpMessageConverterExtractor(final Type responseType, final List<HttpMessageConverter<?>> messageConverters) {
        this(responseType, messageConverters, LogFactory.getLog(HttpMessageConverterExtractor.class));
    }
    
    HttpMessageConverterExtractor(final Type responseType, final List<HttpMessageConverter<?>> messageConverters, final Log logger) {
        Assert.notNull(responseType, "'responseType' must not be null");
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.responseType = responseType;
        this.responseClass = (Class<T>)((responseType instanceof Class) ? ((Class)responseType) : null);
        this.messageConverters = messageConverters;
        this.logger = logger;
    }
    
    @Override
    public T extractData(final ClientHttpResponse response) throws IOException {
        if (!this.hasMessageBody(response)) {
            return null;
        }
        final MediaType contentType = this.getContentType(response);
        for (final HttpMessageConverter<?> messageConverter : this.messageConverters) {
            if (messageConverter instanceof GenericHttpMessageConverter) {
                final GenericHttpMessageConverter<?> genericMessageConverter = (GenericHttpMessageConverter<?>)(GenericHttpMessageConverter)messageConverter;
                if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Reading [" + this.responseType + "] as \"" + contentType + "\" using [" + messageConverter + "]");
                    }
                    return (T)genericMessageConverter.read(this.responseType, null, response);
                }
            }
            if (this.responseClass != null && messageConverter.canRead(this.responseClass, contentType)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Reading [" + this.responseClass.getName() + "] as \"" + contentType + "\" using [" + messageConverter + "]");
                }
                return (T)messageConverter.read(this.responseClass, response);
            }
        }
        throw new RestClientException("Could not extract response: no suitable HttpMessageConverter found for response type [" + this.responseType + "] and content type [" + contentType + "]");
    }
    
    private MediaType getContentType(final ClientHttpResponse response) {
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No Content-Type header found, defaulting to application/octet-stream");
            }
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return contentType;
    }
    
    protected boolean hasMessageBody(final ClientHttpResponse response) throws IOException {
        final HttpStatus responseStatus = response.getStatusCode();
        if (responseStatus == HttpStatus.NO_CONTENT || responseStatus == HttpStatus.NOT_MODIFIED) {
            return false;
        }
        final long contentLength = response.getHeaders().getContentLength();
        return contentLength != 0L;
    }
}
