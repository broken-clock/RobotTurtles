// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.http.HttpHeaders;
import java.nio.charset.Charset;
import org.springframework.http.HttpStatus;

public class HttpClientErrorException extends HttpStatusCodeException
{
    private static final long serialVersionUID = 5177019431887513952L;
    
    public HttpClientErrorException(final HttpStatus statusCode) {
        super(statusCode);
    }
    
    public HttpClientErrorException(final HttpStatus statusCode, final String statusText) {
        super(statusCode, statusText);
    }
    
    public HttpClientErrorException(final HttpStatus statusCode, final String statusText, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }
    
    public HttpClientErrorException(final HttpStatus statusCode, final String statusText, final HttpHeaders responseHeaders, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}
