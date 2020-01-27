// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import java.nio.charset.Charset;
import java.io.InputStream;
import org.springframework.util.FileCopyUtils;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;

public class DefaultResponseErrorHandler implements ResponseErrorHandler
{
    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return this.hasError(this.getHttpStatusCode(response));
    }
    
    private HttpStatus getHttpStatusCode(final ClientHttpResponse response) throws IOException {
        HttpStatus statusCode;
        try {
            statusCode = response.getStatusCode();
        }
        catch (IllegalArgumentException ex) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), this.getResponseBody(response), this.getCharset(response));
        }
        return statusCode;
    }
    
    protected boolean hasError(final HttpStatus statusCode) {
        return statusCode.series() == HttpStatus.Series.CLIENT_ERROR || statusCode.series() == HttpStatus.Series.SERVER_ERROR;
    }
    
    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        final HttpStatus statusCode = this.getHttpStatusCode(response);
        switch (statusCode.series()) {
            case CLIENT_ERROR: {
                throw new HttpClientErrorException(statusCode, response.getStatusText(), response.getHeaders(), this.getResponseBody(response), this.getCharset(response));
            }
            case SERVER_ERROR: {
                throw new HttpServerErrorException(statusCode, response.getStatusText(), response.getHeaders(), this.getResponseBody(response), this.getCharset(response));
            }
            default: {
                throw new RestClientException("Unknown status code [" + statusCode + "]");
            }
        }
    }
    
    private byte[] getResponseBody(final ClientHttpResponse response) {
        try {
            final InputStream responseBody = response.getBody();
            if (responseBody != null) {
                return FileCopyUtils.copyToByteArray(responseBody);
            }
        }
        catch (IOException ex) {}
        return new byte[0];
    }
    
    private Charset getCharset(final ClientHttpResponse response) {
        final HttpHeaders headers = response.getHeaders();
        final MediaType contentType = headers.getContentType();
        return (contentType != null) ? contentType.getCharSet() : null;
    }
}
