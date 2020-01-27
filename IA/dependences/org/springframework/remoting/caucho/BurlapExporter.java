// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.caucho;

import java.io.IOException;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.burlap.io.BurlapInput;
import org.springframework.util.Assert;
import java.io.OutputStream;
import java.io.InputStream;
import com.caucho.burlap.server.BurlapSkeleton;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;

@Deprecated
public class BurlapExporter extends RemoteExporter implements InitializingBean
{
    private BurlapSkeleton skeleton;
    
    @Override
    public void afterPropertiesSet() {
        this.prepare();
    }
    
    public void prepare() {
        this.checkService();
        this.checkServiceInterface();
        this.skeleton = new BurlapSkeleton(this.getProxyForService(), (Class)this.getServiceInterface());
    }
    
    public void invoke(final InputStream inputStream, final OutputStream outputStream) throws Throwable {
        Assert.notNull(this.skeleton, "Burlap exporter has not been initialized");
        final ClassLoader originalClassLoader = this.overrideThreadContextClassLoader();
        try {
            this.skeleton.invoke(new BurlapInput(inputStream), new BurlapOutput(outputStream));
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException ex) {}
            try {
                outputStream.close();
            }
            catch (IOException ex2) {}
            this.resetThreadContextClassLoader(originalClassLoader);
        }
    }
}
