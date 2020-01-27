// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.json;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonProcessingException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.codehaus.jackson.type.JavaType;
import java.lang.reflect.Type;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.util.Assert;
import org.springframework.http.MediaType;
import org.codehaus.jackson.map.ObjectMapper;
import java.nio.charset.Charset;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.AbstractHttpMessageConverter;

@Deprecated
public class MappingJacksonHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object>
{
    public static final Charset DEFAULT_CHARSET;
    private ObjectMapper objectMapper;
    private String jsonPrefix;
    private Boolean prettyPrint;
    
    public MappingJacksonHttpMessageConverter() {
        super(new MediaType[] { new MediaType("application", "json", MappingJacksonHttpMessageConverter.DEFAULT_CHARSET), new MediaType("application", "*+json", MappingJacksonHttpMessageConverter.DEFAULT_CHARSET) });
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
            this.objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, (boolean)this.prettyPrint);
        }
    }
    
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return this.canRead(clazz, null, mediaType);
    }
    
    @Override
    public boolean canRead(final Type type, final Class<?> contextClass, final MediaType mediaType) {
        final JavaType javaType = this.getJavaType(type, contextClass);
        return this.objectMapper.canDeserialize(javaType) && this.canRead(mediaType);
    }
    
    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        return this.objectMapper.canSerialize((Class)clazz) && this.canWrite(mediaType);
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
        if (this.objectMapper.getSerializationConfig().isEnabled(SerializationConfig.Feature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }
        try {
            if (this.jsonPrefix != null) {
                jsonGenerator.writeRaw(this.jsonPrefix);
            }
            this.objectMapper.writeValue(jsonGenerator, object);
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    protected JavaType getJavaType(final Type type, final Class<?> contextClass) {
        return this.objectMapper.getTypeFactory().constructType(type, (Class)contextClass);
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
    }
}
