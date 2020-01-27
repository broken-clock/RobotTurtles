// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import org.springframework.util.ObjectUtils;
import java.util.List;
import java.util.Map;
import org.springframework.util.MultiValueMap;

public class HttpEntity<T>
{
    public static final HttpEntity<?> EMPTY;
    private final HttpHeaders headers;
    private final T body;
    
    protected HttpEntity() {
        this(null, null);
    }
    
    public HttpEntity(final T body) {
        this(body, null);
    }
    
    public HttpEntity(final MultiValueMap<String, String> headers) {
        this(null, headers);
    }
    
    public HttpEntity(final T body, final MultiValueMap<String, String> headers) {
        this.body = body;
        final HttpHeaders tempHeaders = new HttpHeaders();
        if (headers != null) {
            tempHeaders.putAll(headers);
        }
        this.headers = HttpHeaders.readOnlyHttpHeaders(tempHeaders);
    }
    
    public HttpHeaders getHeaders() {
        return this.headers;
    }
    
    public T getBody() {
        return this.body;
    }
    
    public boolean hasBody() {
        return this.body != null;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HttpEntity)) {
            return false;
        }
        final HttpEntity<?> otherEntity = (HttpEntity<?>)other;
        return ObjectUtils.nullSafeEquals(this.headers, otherEntity.headers) && ObjectUtils.nullSafeEquals(this.body, otherEntity.body);
    }
    
    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.headers) * 29 + ObjectUtils.nullSafeHashCode(this.body);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("<");
        if (this.body != null) {
            builder.append(this.body);
            if (this.headers != null) {
                builder.append(',');
            }
        }
        if (this.headers != null) {
            builder.append(this.headers);
        }
        builder.append('>');
        return builder.toString();
    }
    
    static {
        EMPTY = new HttpEntity<Object>();
    }
}
