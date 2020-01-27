// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.json;

import org.springframework.util.ClassUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import com.fasterxml.jackson.databind.JavaType;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.util.Assert;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.AbstractHttpMessageConverter;

public class MappingJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object>
{
    public static final Charset DEFAULT_CHARSET;
    private static final boolean jackson23Available;
    private ObjectMapper objectMapper;
    private String jsonPrefix;
    private Boolean prettyPrint;
    
    public MappingJackson2HttpMessageConverter() {
        super(new MediaType[] { new MediaType("application", "json", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET), new MediaType("application", "*+json", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET) });
        this.objectMapper = new ObjectMapper();
    }
    
    public void setObjectMapper(final ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        this.configurePrettyPrint();
    }
    
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }
    
    public void setJsonPrefix(final String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }
    
    public void setPrefixJson(final boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? "{} && " : null);
    }
    
    public void setPrettyPrint(final boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.configurePrettyPrint();
    }
    
    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
        }
    }
    
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return this.canRead(clazz, null, mediaType);
    }
    
    @Override
    public boolean canRead(final Type type, final Class<?> contextClass, final MediaType mediaType) {
        final JavaType javaType = this.getJavaType(type, contextClass);
        if (!MappingJackson2HttpMessageConverter.jackson23Available || !this.logger.isWarnEnabled()) {
            return this.objectMapper.canDeserialize(javaType) && this.canRead(mediaType);
        }
        final AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        if (this.objectMapper.canDeserialize(javaType, causeRef) && this.canRead(mediaType)) {
            return true;
        }
        final Throwable cause = causeRef.get();
        if (cause != null) {
            final String msg = "Failed to evaluate deserialization for type " + javaType;
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, cause);
            }
            else {
                this.logger.warn(msg + ": " + cause);
            }
        }
        return false;
    }
    
    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        if (!MappingJackson2HttpMessageConverter.jackson23Available || !this.logger.isWarnEnabled()) {
            return this.objectMapper.canSerialize(clazz) && this.canWrite(mediaType);
        }
        final AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        if (this.objectMapper.canSerialize(clazz, causeRef) && this.canWrite(mediaType)) {
            return true;
        }
        final Throwable cause = causeRef.get();
        if (cause != null) {
            final String msg = "Failed to evaluate serialization for type [" + clazz + "]";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, cause);
            }
            else {
                this.logger.warn(msg + ": " + cause);
            }
        }
        return false;
    }
    
    @Override
    protected boolean supports(final Class<?> clazz) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Object readInternal(final Class<?> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final JavaType javaType = this.getJavaType(clazz, null);
        return this.readJavaType(javaType, inputMessage);
    }
    
    @Override
    public Object read(final Type type, final Class<?> contextClass, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final JavaType javaType = this.getJavaType(type, contextClass);
        return this.readJavaType(javaType, inputMessage);
    }
    
    private Object readJavaType(final JavaType javaType, final HttpInputMessage inputMessage) {
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    protected void writeInternal(final Object object, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final JsonEncoding encoding = this.getJsonEncoding(outputMessage.getHeaders().getContentType());
        final JsonGenerator jsonGenerator = this.objectMapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);
        if (this.objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }
        try {
            if (this.jsonPrefix != null) {
                jsonGenerator.writeRaw(this.jsonPrefix);
            }
            this.objectMapper.writeValue(jsonGenerator, object);
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }
    
    protected JavaType getJavaType(final Type type, final Class<?> contextClass) {
        return this.objectMapper.getTypeFactory().constructType(type, contextClass);
    }
    
    protected JsonEncoding getJsonEncoding(final MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            final Charset charset = contentType.getCharSet();
            for (final JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }
    
    static {
        DEFAULT_CHARSET = Charset.forName("UTF-8");
        jackson23Available = ClassUtils.hasMethod(ObjectMapper.class, "canDeserialize", JavaType.class, AtomicReference.class);
    }
}
