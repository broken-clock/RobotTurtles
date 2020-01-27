// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;

public class UnknownHttpStatusCodeException extends RestClientException
{
    private static final long serialVersionUID = 4702443689088991600L;
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private final int rawStatusCode;
    private final String statusText;
    private final byte[] responseBody;
    private final HttpHeaders responseHeaders;
    private final String responseCharset;
    
    public UnknownHttpStatusCodeException(final int rawStatusCode, final String statusText, final HttpHeaders responseHeaders, final byte[] responseBody, final Charset responseCharset) {
        super("Unknown status code [" + String.valueOf(rawStatusCode) + "]" + " " + statusText);
        this.rawStatusCode = rawStatusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        this.responseBody = ((responseBody != null) ? responseBody : new byte[0]);
        this.responseCharset = ((responseCharset != null) ? responseCharset.name() : "ISO-8859-1");
    }
    
    public int getRawStatusCode() {
        return this.rawStatusCode;
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
