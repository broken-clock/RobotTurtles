// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;

public interface ResponseExtractor<T>
{
    T extractData(final ClientHttpResponse p0) throws IOException;
}
