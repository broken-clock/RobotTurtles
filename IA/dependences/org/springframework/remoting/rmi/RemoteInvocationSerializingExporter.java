// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import org.springframework.remoting.support.RemoteInvocationResult;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import org.springframework.remoting.support.RemoteInvocation;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InputStream;
import org.springframework.util.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;

public abstract class RemoteInvocationSerializingExporter extends RemoteInvocationBasedExporter implements InitializingBean
{
    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
    private String contentType;
    private boolean acceptProxyClasses;
    private Object proxy;
    
    public RemoteInvocationSerializingExporter() {
        this.contentType = "application/x-java-serialized-object";
        this.acceptProxyClasses = true;
    }
    
    public void setContentType(final String contentType) {
        Assert.notNull(contentType, "'contentType' must not be null");
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void setAcceptProxyClasses(final boolean acceptProxyClasses) {
        this.acceptProxyClasses = acceptProxyClasses;
    }
    
    public boolean isAcceptProxyClasses() {
        return this.acceptProxyClasses;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.prepare();
    }
    
    public void prepare() {
        this.proxy = this.getProxyForService();
    }
    
    protected final Object getProxy() {
        Assert.notNull(this.proxy, ClassUtils.getShortName(this.getClass()) + " has not been initialized");
        return this.proxy;
    }
    
    protected ObjectInputStream createObjectInputStream(final InputStream is) throws IOException {
        return new CodebaseAwareObjectInputStream(is, this.getBeanClassLoader(), this.isAcceptProxyClasses());
    }
    
    protected RemoteInvocation doReadRemoteInvocation(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        final Object obj = ois.readObject();
        if (!(obj instanceof RemoteInvocation)) {
            throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocation.class.getName() + "]: " + obj);
        }
        return (RemoteInvocation)obj;
    }
    
    protected ObjectOutputStream createObjectOutputStream(final OutputStream os) throws IOException {
        return new ObjectOutputStream(os);
    }
    
    protected void doWriteRemoteInvocationResult(final RemoteInvocationResult result, final ObjectOutputStream oos) throws IOException {
        oos.writeObject(result);
    }
}
