// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.httpinvoker;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.remoting.support.RemoteInvocation;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;

public class SimpleHttpInvokerServiceExporter extends RemoteInvocationSerializingExporter implements HttpHandler
{
    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        try {
            final RemoteInvocation invocation = this.readRemoteInvocation(exchange);
            final RemoteInvocationResult result = this.invokeAndCreateResult(invocation, this.getProxy());
            this.writeRemoteInvocationResult(exchange, result);
            exchange.close();
        }
        catch (ClassNotFoundException ex) {
            exchange.sendResponseHeaders(500, -1L);
            this.logger.error("Class not found during deserialization", ex);
        }
    }
    
    protected RemoteInvocation readRemoteInvocation(final HttpExchange exchange) throws IOException, ClassNotFoundException {
        return this.readRemoteInvocation(exchange, exchange.getRequestBody());
    }
    
    protected RemoteInvocation readRemoteInvocation(final HttpExchange exchange, final InputStream is) throws IOException, ClassNotFoundException {
        final ObjectInputStream ois = this.createObjectInputStream(this.decorateInputStream(exchange, is));
        return this.doReadRemoteInvocation(ois);
    }
    
    protected InputStream decorateInputStream(final HttpExchange exchange, final InputStream is) throws IOException {
        return is;
    }
    
    protected void writeRemoteInvocationResult(final HttpExchange exchange, final RemoteInvocationResult result) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", this.getContentType());
        exchange.sendResponseHeaders(200, 0L);
        this.writeRemoteInvocationResult(exchange, result, exchange.getResponseBody());
    }
    
    protected void writeRemoteInvocationResult(final HttpExchange exchange, final RemoteInvocationResult result, final OutputStream os) throws IOException {
        final ObjectOutputStream oos = this.createObjectOutputStream(this.decorateOutputStream(exchange, os));
        this.doWriteRemoteInvocationResult(result, oos);
        oos.flush();
    }
    
    protected OutputStream decorateOutputStream(final HttpExchange exchange, final OutputStream os) throws IOException {
        return os;
    }
}
