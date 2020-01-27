// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpRequest;

public interface RequestCallback
{
    void doWithRequest(final ClientHttpRequest p0) throws IOException;
}
