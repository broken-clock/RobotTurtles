// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import org.springframework.util.ObjectUtils;
import org.springframework.util.MultiValueMap;

public class ResponseEntity<T> extends HttpEntity<T>
{
    private final HttpStatus statusCode;
    
    public ResponseEntity(final HttpStatus statusCode) {
        this.statusCode = statusCode;
    }
    
    public ResponseEntity(final T body, final HttpStatus statusCode) {
        super(body);
        this.statusCode = statusCode;
    }
    
    public ResponseEntity(final MultiValueMap<String, String> headers, final HttpStatus statusCode) {
        super(headers);
        this.statusCode = statusCode;
    }
    
    public ResponseEntity(final T body, final MultiValueMap<String, String> headers, final HttpStatus statusCode) {
        super(body, headers);
        this.statusCode = statusCode;
    }
    
    public HttpStatus getStatusCode() {
        return this.statusCode;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResponseEntity)) {
            return false;
        }
        final ResponseEntity<?> otherEntity = (ResponseEntity<?>)other;
        return ObjectUtils.nullSafeEquals(this.statusCode, otherEntity.statusCode) && super.equals(other);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.statusCode);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("<");
        builder.append(this.statusCode.toString());
        builder.append(' ');
        builder.append(this.statusCode.getReasonPhrase());
        builder.append(',');
        final T body = this.getBody();
        final HttpHeaders headers = this.getHeaders();
        if (body != null) {
            builder.append(body);
            if (headers != null) {
                builder.append(',');
            }
        }
        if (headers != null) {
            builder.append(headers);
        }
        builder.append('>');
        return builder.toString();
    }
}
