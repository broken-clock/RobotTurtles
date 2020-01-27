// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import java.io.IOException;
import org.springframework.util.FileCopyUtils;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SimpleHessianServiceExporter extends HessianExporter implements HttpHandler
{
    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            exchange.sendResponseHeaders(405, -1L);
            return;
        }
        final ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        try {
            this.invoke(exchange.getRequestBody(), output);
        }
        catch (Throwable ex) {
            exchange.sendResponseHeaders(500, -1L);
            this.logger.error("Hessian skeleton invocation failed", ex);
            return;
        }
        exchange.getResponseHeaders().set("Content-Type", "application/x-hessian");
        exchange.sendResponseHeaders(200, output.size());
        FileCopyUtils.copy(output.toByteArray(), exchange.getResponseBody());
    }
}
