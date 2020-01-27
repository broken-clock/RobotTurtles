// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.httpinvoker;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.web.util.NestedServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.HttpRequestHandler;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;

public class HttpInvokerServiceExporter extends RemoteInvocationSerializingExporter implements HttpRequestHandler
{
    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final RemoteInvocation invocation = this.readRemoteInvocation(request);
            final RemoteInvocationResult result = this.invokeAndCreateResult(invocation, this.getProxy());
            this.writeRemoteInvocationResult(request, response, result);
        }
        catch (ClassNotFoundException ex) {
            throw new NestedServletException("Class not found during deserialization", ex);
        }
    }
    
    protected RemoteInvocation readRemoteInvocation(final HttpServletRequest request) throws IOException, ClassNotFoundException {
        return this.readRemoteInvocation(request, (InputStream)request.getInputStream());
    }
    
    protected RemoteInvocation readRemoteInvocation(final HttpServletRequest request, final InputStream is) throws IOException, ClassNotFoundException {
        final ObjectInputStream ois = this.createObjectInputStream(this.decorateInputStream(request, is));
        try {
            return this.doReadRemoteInvocation(ois);
        }
        finally {
            ois.close();
        }
    }
    
    protected InputStream decorateInputStream(final HttpServletRequest request, final InputStream is) throws IOException {
        return is;
    }
    
    protected void writeRemoteInvocationResult(final HttpServletRequest request, final HttpServletResponse response, final RemoteInvocationResult result) throws IOException {
        response.setContentType(this.getContentType());
        this.writeRemoteInvocationResult(request, response, result, (OutputStream)response.getOutputStream());
    }
    
    protected void writeRemoteInvocationResult(final HttpServletRequest request, final HttpServletResponse response, final RemoteInvocationResult result, final OutputStream os) throws IOException {
        final ObjectOutputStream oos = this.createObjectOutputStream(this.decorateOutputStream(request, response, os));
        try {
            this.doWriteRemoteInvocationResult(result, oos);
        }
        finally {
            oos.close();
        }
    }
    
    protected OutputStream decorateOutputStream(final HttpServletRequest request, final HttpServletResponse response, final OutputStream os) throws IOException {
        return os;
    }
}
