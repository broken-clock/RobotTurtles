// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;

public interface ResponseErrorHandler
{
    boolean hasError(final ClientHttpResponse p0) throws IOException;
    
    void handleError(final ClientHttpResponse p0) throws IOException;
}
