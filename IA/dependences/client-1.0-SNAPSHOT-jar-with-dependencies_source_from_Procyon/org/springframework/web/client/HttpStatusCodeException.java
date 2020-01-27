// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public abstract class HttpStatusCodeException extends RestClientException
{
    private static final long serialVersionUID = -5807494703720513267L;
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private final HttpStatus statusCode;
    private final String statusText;
    private final byte[] responseBody;
    private final HttpHeaders responseHeaders;
    private final String responseCharset;
    
    protected HttpStatusCodeException(final HttpStatus statusCode) {
        this(statusCode, statusCode.name(), null, null, null);
    }
    
    protected HttpStatusCodeException(final HttpStatus statusCode, final String statusText) {
        this(statusCode, statusText, null, null, null);
    }
    
    protected HttpStatusCodeException(final HttpStatus statusCode, final String statusText, final byte[] responseBody, final Charset responseCharset) {
        this(statusCode, statusText, null, responseBody, responseCharset);
    }
    
    protected HttpStatusCodeException(final HttpStatus statusCode, final String statusText, final HttpHeaders responseHeaders, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode.value() + " " + statusText);
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        this.responseBody = ((responseBody != null) ? responseBody : new byte[0]);
        this.responseCharset = ((responseCharset != null) ? responseCharset.name() : "ISO-8859-1");
    }
    
    public HttpStatus getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusText() {
        return this.statusText;
    }
    
    public HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }
    
    public byte[] getResponseBodyAsByteArray() {
        return this.responseBody;
    }
    
    public String getResponseBodyAsString() {
        try {
            return new String(this.responseBody, this.responseCharset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new InternalError(ex.getMessage());
        }
    }
}
