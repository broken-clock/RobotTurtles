// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.io.IOException;
import java.net.URL;
import org.springframework.core.io.VfsUtils;

abstract class VfsPatternUtils extends VfsUtils
{
    static Object getVisitorAttribute() {
        return VfsUtils.doGetVisitorAttribute();
    }
    
    static String getPath(final Object resource) {
        return VfsUtils.doGetPath(resource);
    }
    
    static Object findRoot(final URL url) throws IOException {
        return VfsUtils.getRoot(url);
    }
    
    static void visit(final Object resource, final InvocationHandler visitor) throws IOException {
        final Object visitorProxy = Proxy.newProxyInstance(VfsPatternUtils.VIRTUAL_FILE_VISITOR_INTERFACE.getClassLoader(), new Class[] { VfsPatternUtils.VIRTUAL_FILE_VISITOR_INTERFACE }, visitor);
        VfsUtils.invokeVfsMethod(VfsPatternUtils.VIRTUAL_FILE_METHOD_VISIT, resource, visitorProxy);
    }
}
