// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.http.HttpHeaders;
import java.nio.charset.Charset;
import org.springframework.http.HttpStatus;

public class HttpServerErrorException extends HttpStatusCodeException
{
    private static final long serialVersionUID = -2915754006618138282L;
    
    public HttpServerErrorException(final HttpStatus statusCode) {
        super(statusCode);
    }
    
    public HttpServerErrorException(final HttpStatus statusCode, final String statusText) {
        super(statusCode, statusText);
    }
    
    public HttpServerErrorException(final HttpStatus statusCode, final String statusText, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }
    
    public HttpServerErrorException(final HttpStatus statusCode, final String statusText, final HttpHeaders responseHeaders, final byte[] responseBody, final Charset responseCharset) {
        super(statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}
