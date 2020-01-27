// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;

public interface ClientHttpRequestExecution
{
    ClientHttpResponse execute(final HttpRequest p0, final byte[] p1) throws IOException;
}
