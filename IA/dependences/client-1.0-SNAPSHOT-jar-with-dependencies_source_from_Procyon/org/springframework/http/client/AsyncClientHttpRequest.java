// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRequest;

public interface AsyncClientHttpRequest extends HttpRequest, HttpOutputMessage
{
    ListenableFuture<ClientHttpResponse> executeAsync() throws IOException;
}
