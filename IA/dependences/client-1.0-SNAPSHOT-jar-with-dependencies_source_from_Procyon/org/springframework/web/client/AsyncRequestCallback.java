// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.AsyncClientHttpRequest;

public interface AsyncRequestCallback
{
    void doWithRequest(final AsyncClientHttpRequest p0) throws IOException;
}
