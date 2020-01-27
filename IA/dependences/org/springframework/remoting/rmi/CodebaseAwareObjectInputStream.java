// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.rmi;

import java.rmi.server.RMIClassLoader;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.ConfigurableObjectInputStream;

public class CodebaseAwareObjectInputStream extends ConfigurableObjectInputStream
{
    private final String codebaseUrl;
    
    public CodebaseAwareObjectInputStream(final InputStream in, final String codebaseUrl) throws IOException {
        this(in, null, codebaseUrl);
    }
    
    public CodebaseAwareObjectInputStream(final InputStream in, final ClassLoader classLoader, final String codebaseUrl) throws IOException {
        super(in, classLoader);
        this.codebaseUrl = codebaseUrl;
    }
    
    public CodebaseAwareObjectInputStream(final InputStream in, final ClassLoader classLoader, final boolean acceptProxyClasses) throws IOException {
        super(in, classLoader, acceptProxyClasses);
        this.codebaseUrl = null;
    }
    
    @Override
    protected Class<?> resolveFallbackIfPossible(final String className, final ClassNotFoundException ex) throws IOException, ClassNotFoundException {
        if (this.codebaseUrl == null) {
            throw ex;
        }
        return RMIClassLoader.loadClass(this.codebaseUrl, className);
    }
    
    @Override
    protected ClassLoader getFallbackClassLoader() throws IOException {
        return RMIClassLoader.getClassLoader(this.codebaseUrl);
    }
}
