// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import java.io.IOException;
import javax.servlet.ServletException;
import org.springframework.web.util.NestedServletException;
import java.io.OutputStream;
import java.io.InputStream;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.HttpRequestHandler;

@Deprecated
public class BurlapServiceExporter extends BurlapExporter implements HttpRequestHandler
{
    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (!"POST".equals(request.getMethod())) {
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { "POST" }, "BurlapServiceExporter only supports POST requests");
        }
        try {
            this.invoke((InputStream)request.getInputStream(), (OutputStream)response.getOutputStream());
        }
        catch (Throwable ex) {
            throw new NestedServletException("Burlap skeleton invocation failed", ex);
        }
    }
}
