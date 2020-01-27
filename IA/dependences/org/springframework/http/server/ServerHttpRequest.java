// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.server;

import java.net.InetSocketAddress;
import java.security.Principal;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpRequest;

public interface ServerHttpRequest extends HttpRequest, HttpInputMessage
{
    Principal getPrincipal();
    
    InetSocketAddress getLocalAddress();
    
    InetSocketAddress getRemoteAddress();
    
    ServerHttpAsyncRequestControl getAsyncRequestControl(final ServerHttpResponse p0);
}
