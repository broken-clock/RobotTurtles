// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.Hessian2Input;
import java.io.IOException;
import java.io.BufferedInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianDebugInputStream;
import java.io.Writer;
import java.io.PrintWriter;
import org.springframework.util.CommonsLogWriter;
import org.springframework.util.Assert;
import java.io.OutputStream;
import java.io.InputStream;
import com.caucho.hessian.server.HessianSkeleton;
import org.apache.commons.logging.Log;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.SerializerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;

public class HessianExporter extends RemoteExporter implements InitializingBean
{
    public static final String CONTENT_TYPE_HESSIAN = "application/x-hessian";
    private SerializerFactory serializerFactory;
    private HessianRemoteResolver remoteResolver;
    private Log debugLogger;
    private HessianSkeleton skeleton;
    
    public HessianExporter() {
        this.serializerFactory = new SerializerFactory();
    }
    
    public void setSerializerFactory(final SerializerFactory serializerFactory) {
        this.serializerFactory = ((serializerFactory != null) ? serializerFactory : new SerializerFactory());
    }
    
    public void setSendCollectionType(final boolean sendCollectionType) {
        this.serializerFactory.setSendCollectionType(sendCollectionType);
    }
    
    public void setAllowNonSerializable(final boolean allowNonSerializable) {
        this.serializerFactory.setAllowNonSerializable(allowNonSerializable);
    }
    
    public void setRemoteResolver(final HessianRemoteResolver remoteResolver) {
        this.remoteResolver = remoteResolver;
    }
    
    public void setDebug(final boolean debug) {
        this.debugLogger = (debug ? this.logger : null);
    }
    
    @Override
    public void afterPropertiesSet() {
        this.prepare();
    }
    
    public void prepare() {
        this.checkService();
        this.checkServiceInterface();
        this.skeleton = new HessianSkeleton(this.getProxyForService(), (Class)this.getServiceInterface());
    }
    
    public void invoke(final InputStream inputStream, final OutputStream outputStream) throws Throwable {
        Assert.notNull(this.skeleton, "Hessian exporter has not been initialized");
        this.doInvoke(this.skeleton, inputStream, outputStream);
    }
    
    protected void doInvoke(final HessianSkeleton skeleton, final InputStream inputStream, final OutputStream outputStream) throws Throwable {
        final ClassLoader originalClassLoader = this.overrideThreadContextClassLoader();
        try {
            InputStream isToUse = inputStream;
            OutputStream osToUse = outputStream;
            if (this.debugLogger != null && this.debugLogger.isDebugEnabled()) {
                final PrintWriter debugWriter = new PrintWriter(new CommonsLogWriter(this.debugLogger));
                final HessianDebugInputStream dis = new HessianDebugInputStream(inputStream, debugWriter);
                final HessianDebugOutputStream dos = new HessianDebugOutputStream(outputStream, debugWriter);
                dis.startTop2();
                dos.startTop2();
                isToUse = (InputStream)dis;
                osToUse = (OutputStream)dos;
            }
            if (!isToUse.markSupported()) {
                isToUse = new BufferedInputStream(isToUse);
                isToUse.mark(1);
            }
            final int code = isToUse.read();
            AbstractHessianInput in;
            AbstractHessianOutput out;
            if (code == 72) {
                final int major = isToUse.read();
                final int minor = isToUse.read();
                if (major != 2) {
                    throw new IOException("Version " + major + "." + minor + " is not understood");
                }
                in = (AbstractHessianInput)new Hessian2Input(isToUse);
                out = (AbstractHessianOutput)new Hessian2Output(osToUse);
                in.readCall();
            }
            else if (code == 67) {
                isToUse.reset();
                in = (AbstractHessianInput)new Hessian2Input(isToUse);
                out = (AbstractHessianOutput)new Hessian2Output(osToUse);
                in.readCall();
            }
            else {
                if (code != 99) {
                    throw new IOException("Expected 'H'/'C' (Hessian 2.0) or 'c' (Hessian 1.0) in hessian input at " + code);
                }
                final int major = isToUse.read();
                final int minor = isToUse.read();
                in = (AbstractHessianInput)new HessianInput(isToUse);
                if (major >= 2) {
                    out = (AbstractHessianOutput)new Hessian2Output(osToUse);
                }
                else {
                    out = (AbstractHessianOutput)new HessianOutput(osToUse);
                }
            }
            if (this.serializerFactory != null) {
                in.setSerializerFactory(this.serializerFactory);
                out.setSerializerFactory(this.serializerFactory);
            }
            if (this.remoteResolver != null) {
                in.setRemoteResolver(this.remoteResolver);
            }
            try {
                skeleton.invoke(in, out);
            }
            finally {
                try {
                    in.close();
                    isToUse.close();
                }
                catch (IOException ex) {}
                try {
                    out.close();
                    osToUse.close();
                }
                catch (IOException ex2) {}
            }
        }
        finally {
            this.resetThreadContextClassLoader(originalClassLoader);
        }
    }
}
