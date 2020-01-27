// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.server;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import java.io.Closeable;
import java.io.Flushable;
import org.springframework.http.HttpOutputMessage;

public interface ServerHttpResponse extends HttpOutputMessage, Flushable, Closeable
{
    void setStatusCode(final HttpStatus p0);
    
    void flush() throws IOException;
    
    void close();
}
