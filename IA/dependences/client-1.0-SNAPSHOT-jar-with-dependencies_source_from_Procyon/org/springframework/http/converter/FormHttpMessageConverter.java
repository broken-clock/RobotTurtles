// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import java.io.UnsupportedEncodingException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import java.io.OutputStream;
import java.util.Map;
import java.net.URLEncoder;
import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import java.net.URLDecoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.util.StreamUtils;
import org.springframework.http.HttpInputMessage;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.ArrayList;
import org.springframework.http.MediaType;
import java.util.List;
import java.nio.charset.Charset;
import java.util.Random;
import org.springframework.util.MultiValueMap;

public class FormHttpMessageConverter implements HttpMessageConverter<MultiValueMap<String, ?>>
{
    private static final byte[] BOUNDARY_CHARS;
    private final Random rnd;
    private Charset charset;
    private List<MediaType> supportedMediaTypes;
    private List<HttpMessageConverter<?>> partConverters;
    
    public FormHttpMessageConverter() {
        this.rnd = new Random();
        this.charset = Charset.forName("UTF-8");
        this.supportedMediaTypes = new ArrayList<MediaType>();
        this.partConverters = new ArrayList<HttpMessageConverter<?>>();
        this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        this.partConverters.add(new ByteArrayHttpMessageConverter());
        final StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        this.partConverters.add(stringHttpMessageConverter);
        this.partConverters.add(new ResourceHttpMessageConverter());
    }
    
    public final void setPartConverters(final List<HttpMessageConverter<?>> partConverters) {
        Assert.notEmpty(partConverters, "'partConverters' must not be empty");
        this.partConverters = partConverters;
    }
    
    public final void addPartConverter(final HttpMessageConverter<?> partConverter) {
        Assert.notNull(partConverter, "'partConverter' must not be NULL");
        this.partConverters.add(partConverter);
    }
    
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }
    
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null) {
            return true;
        }
        for (final MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (!supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) && supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (final MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }
    
    public void setSupportedMediaTypes(final List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }
    
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList((List<? extends MediaType>)this.supportedMediaTypes);
    }
    
    @Override
    public MultiValueMap<String, String> read(final Class<? extends MultiValueMap<String, ?>> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final MediaType contentType = inputMessage.getHeaders().getContentType();
        final Charset charset = (contentType.getCharSet() != null) ? contentType.getCharSet() : this.charset;
        final String body = StreamUtils.copyToString(inputMessage.getBody(), charset);
        final String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        final MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(pairs.length);
        for (final String pair : pairs) {
            final int idx = pair.indexOf(61);
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset.name()), null);
            }
            else {
                final String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                final String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                result.add(name, value);
            }
        }
        return result;
    }
    
    @Override
    public void write(final MultiValueMap<String, ?> map, final MediaType contentType, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (!this.isMultipart(map, contentType)) {
            this.writeForm((MultiValueMap<String, String>)map, contentType, outputMessage);
        }
        else {
            this.writeMultipart((MultiValueMap<String, Object>)map, outputMessage);
        }
    }
    
    private boolean isMultipart(final MultiValueMap<String, ?> map, final MediaType contentType) {
        if (contentType != null) {
            return MediaType.MULTIPART_FORM_DATA.equals(contentType);
        }
        for (final String name : map.keySet()) {
            for (final Object value : map.get(name)) {
                if (value != null && !(value instanceof String)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void writeForm(final MultiValueMap<String, String> form, final MediaType contentType, final HttpOutputMessage outputMessage) throws IOException {
        Charset charset;
        if (contentType != null) {
            outputMessage.getHeaders().setContentType(contentType);
            charset = ((contentType.getCharSet() != null) ? contentType.getCharSet() : this.charset);
        }
        else {
            outputMessage.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            charset = this.charset;
        }
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> nameIterator = form.keySet().iterator();
        while (nameIterator.hasNext()) {
            final String name = nameIterator.next();
            final Iterator<String> valueIterator = form.get(name).iterator();
            while (valueIterator.hasNext()) {
                final String value = valueIterator.next();
                builder.append(URLEncoder.encode(name, charset.name()));
                if (value != null) {
                    builder.append('=');
                    builder.append(URLEncoder.encode(value, charset.name()));
                    if (!valueIterator.hasNext()) {
                        continue;
                    }
                    builder.append('&');
                }
            }
            if (nameIterator.hasNext()) {
                builder.append('&');
            }
        }
        final byte[] bytes = builder.toString().getBytes(charset.name());
        outputMessage.getHeaders().setContentLength(bytes.length);
        StreamUtils.copy(bytes, outputMessage.getBody());
    }
    
    private void writeMultipart(final MultiValueMap<String, Object> parts, final HttpOutputMessage outputMessage) throws IOException {
        final byte[] boundary = this.generateMultipartBoundary();
        final Map<String, String> parameters = Collections.singletonMap("boundary", new String(boundary, "US-ASCII"));
        final MediaType contentType = new MediaType(MediaType.MULTIPART_FORM_DATA, parameters);
        outputMessage.getHeaders().setContentType(contentType);
        this.writeParts(outputMessage.getBody(), parts, boundary);
        this.writeEnd(boundary, outputMessage.getBody());
    }
    
    private void writeParts(final OutputStream os, final MultiValueMap<String, Object> parts, final byte[] boundary) throws IOException {
        for (final Map.Entry<String, List<Object>> entry : parts.entrySet()) {
            final String name = entry.getKey();
            for (final Object part : entry.getValue()) {
                if (part != null) {
                    this.writeBoundary(boundary, os);
                    final HttpEntity<?> entity = this.getEntity(part);
                    this.writePart(name, entity, os);
                    this.writeNewLine(os);
                }
            }
        }
    }
    
    private void writeBoundary(final byte[] boundary, final OutputStream os) throws IOException {
        os.write(45);
        os.write(45);
        os.write(boundary);
        this.writeNewLine(os);
    }
    
    private HttpEntity<?> getEntity(final Object part) {
        if (part instanceof HttpEntity) {
            return (HttpEntity<?>)part;
        }
        return new HttpEntity<Object>(part);
    }
    
    private void writePart(final String name, final HttpEntity<?> partEntity, final OutputStream os) throws IOException {
        final Object partBody = partEntity.getBody();
        final Class<?> partType = partBody.getClass();
        final HttpHeaders partHeaders = partEntity.getHeaders();
        final MediaType partContentType = partHeaders.getContentType();
        for (final HttpMessageConverter<?> messageConverter : this.partConverters) {
            if (messageConverter.canWrite(partType, partContentType)) {
                final HttpOutputMessage multipartOutputMessage = new MultipartHttpOutputMessage(os);
                multipartOutputMessage.getHeaders().setContentDispositionFormData(name, this.getFilename(partBody));
                if (!partHeaders.isEmpty()) {
                    multipartOutputMessage.getHeaders().putAll(partHeaders);
                }
                messageConverter.write(partBody, partContentType, multipartOutputMessage);
                return;
            }
        }
        throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter found for request type [" + partType.getName() + "]");
    }
    
    private void writeEnd(final byte[] boundary, final OutputStream os) throws IOException {
        os.write(45);
        os.write(45);
        os.write(boundary);
        os.write(45);
        os.write(45);
        this.writeNewLine(os);
    }
    
    private void writeNewLine(final OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }
    
    protected byte[] generateMultipartBoundary() {
        final byte[] boundary = new byte[this.rnd.nextInt(11) + 30];
        for (int i = 0; i < boundary.length; ++i) {
            boundary[i] = FormHttpMessageConverter.BOUNDARY_CHARS[this.rnd.nextInt(FormHttpMessageConverter.BOUNDARY_CHARS.length)];
        }
        return boundary;
    }
    
    protected String getFilename(final Object part) {
        if (part instanceof Resource) {
            final Resource resource = (Resource)part;
            return resource.getFilename();
        }
        return null;
    }
    
    static {
        BOUNDARY_CHARS = new byte[] { 45, 95, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90 };
    }
    
    private class MultipartHttpOutputMessage implements HttpOutputMessage
    {
        private final HttpHeaders headers;
        private final OutputStream os;
        private boolean headersWritten;
        
        public MultipartHttpOutputMessage(final OutputStream os) {
            this.headers = new HttpHeaders();
            this.headersWritten = false;
            this.os = os;
        }
        
        @Override
        public HttpHeaders getHeaders() {
            return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
        }
        
        @Override
        public OutputStream getBody() throws IOException {
            this.writeHeaders();
            return this.os;
        }
        
        private void writeHeaders() throws IOException {
            if (!this.headersWritten) {
                for (final Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                    final byte[] headerName = this.getAsciiBytes(entry.getKey());
                    for (final String headerValueString : entry.getValue()) {
                        final byte[] headerValue = this.getAsciiBytes(headerValueString);
                        this.os.write(headerName);
                        this.os.write(58);
                        this.os.write(32);
                        this.os.write(headerValue);
                        FormHttpMessageConverter.this.writeNewLine(this.os);
                    }
                }
                FormHttpMessageConverter.this.writeNewLine(this.os);
                this.headersWritten = true;
            }
        }
        
        protected byte[] getAsciiBytes(final String name) {
            try {
                return name.getBytes("US-ASCII");
            }
            catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
