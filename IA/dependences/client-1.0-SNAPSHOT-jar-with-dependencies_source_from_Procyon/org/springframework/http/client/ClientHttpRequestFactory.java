// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpMethod;
import java.net.URI;

public interface ClientHttpRequestFactory
{
    ClientHttpRequest createRequest(final URI p0, final HttpMethod p1) throws IOException;
}
