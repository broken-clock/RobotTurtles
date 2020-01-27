// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.httpinvoker;

import java.rmi.RemoteException;
import org.springframework.remoting.rmi.CodebaseAwareObjectInputStream;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanClassLoaderAware;

public abstract class AbstractHttpInvokerRequestExecutor implements HttpInvokerRequestExecutor, BeanClassLoaderAware
{
    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
    protected static final String HTTP_METHOD_POST = "POST";
    protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";
    protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
    protected static final String ENCODING_GZIP = "gzip";
    private static final int SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE = 1024;
    protected final Log logger;
    private String contentType;
    private boolean acceptGzipEncoding;
    private ClassLoader beanClassLoader;
    
    public AbstractHttpInvokerRequestExecutor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.contentType = "application/x-java-serialized-object";
        this.acceptGzipEncoding = true;
    }
    
    public void setContentType(final String contentType) {
        Assert.notNull(contentType, "'contentType' must not be null");
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void setAcceptGzipEncoding(final boolean acceptGzipEncoding) {
        this.acceptGzipEncoding = acceptGzipEncoding;
    }
    
    public boolean isAcceptGzipEncoding() {
        return this.acceptGzipEncoding;
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
    
    @Override
    public final RemoteInvocationResult executeRequest(final HttpInvokerClientConfiguration config, final RemoteInvocation invocation) throws Exception {
        final ByteArrayOutputStream baos = this.getByteArrayOutputStream(invocation);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Sending HTTP invoker request for service at [" + config.getServiceUrl() + "], with size " + baos.size());
        }
        return this.doExecuteRequest(config, baos);
    }
    
    protected ByteArrayOutputStream getByteArrayOutputStream(final RemoteInvocation invocation) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        this.writeRemoteInvocation(invocation, baos);
        return baos;
    }
    
    protected void writeRemoteInvocation(final RemoteInvocation invocation, final OutputStream os) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream(this.decorateOutputStream(os));
        try {
            this.doWriteRemoteInvocation(invocation, oos);
        }
        finally {
            oos.close();
        }
    }
    
    protected OutputStream decorateOutputStream(final OutputStream os) throws IOException {
        return os;
    }
    
    protected void doWriteRemoteInvocation(final RemoteInvocation invocation, final ObjectOutputStream oos) throws IOException {
        oos.writeObject(invocation);
    }
    
    protected abstract RemoteInvocationResult doExecuteRequest(final HttpInvokerClientConfiguration p0, final ByteArrayOutputStream p1) throws Exception;
    
    protected RemoteInvocationResult readRemoteInvocationResult(final InputStream is, final String codebaseUrl) throws IOException, ClassNotFoundException {
        final ObjectInputStream ois = this.createObjectInputStream(this.decorateInputStream(is), codebaseUrl);
        try {
            return this.doReadRemoteInvocationResult(ois);
        }
        finally {
            ois.close();
        }
    }
    
    protected InputStream decorateInputStream(final InputStream is) throws IOException {
        return is;
    }
    
    protected ObjectInputStream createObjectInputStream(final InputStream is, final String codebaseUrl) throws IOException {
        return new CodebaseAwareObjectInputStream(is, this.getBeanClassLoader(), codebaseUrl);
    }
    
    protected RemoteInvocationResult doReadRemoteInvocationResult(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        final Object obj = ois.readObject();
        if (!(obj instanceof RemoteInvocationResult)) {
            throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocationResult.class.getName() + "]: " + obj);
        }
        return (RemoteInvocationResult)obj;
    }
}
