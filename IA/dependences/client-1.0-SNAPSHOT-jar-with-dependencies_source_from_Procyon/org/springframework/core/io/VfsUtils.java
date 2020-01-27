// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class VfsUtils
{
    private static final String VFS3_PKG = "org.jboss.vfs.";
    private static final String VFS_NAME = "VFS";
    private static Method VFS_METHOD_GET_ROOT_URL;
    private static Method VFS_METHOD_GET_ROOT_URI;
    private static Method VIRTUAL_FILE_METHOD_EXISTS;
    private static Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;
    private static Method VIRTUAL_FILE_METHOD_GET_SIZE;
    private static Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;
    private static Method VIRTUAL_FILE_METHOD_TO_URL;
    private static Method VIRTUAL_FILE_METHOD_TO_URI;
    private static Method VIRTUAL_FILE_METHOD_GET_NAME;
    private static Method VIRTUAL_FILE_METHOD_GET_PATH_NAME;
    private static Method VIRTUAL_FILE_METHOD_GET_CHILD;
    protected static Class<?> VIRTUAL_FILE_VISITOR_INTERFACE;
    protected static Method VIRTUAL_FILE_METHOD_VISIT;
    private static Field VISITOR_ATTRIBUTES_FIELD_RECURSE;
    private static Method GET_PHYSICAL_FILE;
    
    protected static Object invokeVfsMethod(final Method method, final Object target, final Object... args) throws IOException {
        try {
            return method.invoke(target, args);
        }
        catch (InvocationTargetException ex) {
            final Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof IOException) {
                throw (IOException)targetEx;
            }
            ReflectionUtils.handleInvocationTargetException(ex);
        }
        catch (Exception ex2) {
            ReflectionUtils.handleReflectionException(ex2);
        }
        throw new IllegalStateException("Invalid code path reached");
    }
    
    static boolean exists(final Object vfsResource) {
        try {
            return (boolean)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_EXISTS, vfsResource, new Object[0]);
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    static boolean isReadable(final Object vfsResource) {
        try {
            return (long)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_GET_SIZE, vfsResource, new Object[0]) > 0L;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    static long getSize(final Object vfsResource) throws IOException {
        return (long)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_GET_SIZE, vfsResource, new Object[0]);
    }
    
    static long getLastModified(final Object vfsResource) throws IOException {
        return (long)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED, vfsResource, new Object[0]);
    }
    
    static InputStream getInputStream(final Object vfsResource) throws IOException {
        return (InputStream)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_GET_INPUT_STREAM, vfsResource, new Object[0]);
    }
    
    static URL getURL(final Object vfsResource) throws IOException {
        return (URL)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_TO_URL, vfsResource, new Object[0]);
    }
    
    static URI getURI(final Object vfsResource) throws IOException {
        return (URI)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_TO_URI, vfsResource, new Object[0]);
    }
    
    static String getName(final Object vfsResource) {
        try {
            return (String)invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_GET_NAME, vfsResource, new Object[0]);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Cannot get resource name", ex);
        }
    }
    
    static Object getRelative(final URL url) throws IOException {
        return invokeVfsMethod(VfsUtils.VFS_METHOD_GET_ROOT_URL, null, url);
    }
    
    static Object getChild(final Object vfsResource, final String path) throws IOException {
        return invokeVfsMethod(VfsUtils.VIRTUAL_FILE_METHOD_GET_CHILD, vfsResource, path);
    }
    
    static File getFile(final Object vfsResource) throws IOException {
        return (File)invokeVfsMethod(VfsUtils.GET_PHYSICAL_FILE, vfsResource, new Object[0]);
    }
    
    static Object getRoot(final URI url) throws IOException {
        return invokeVfsMethod(VfsUtils.VFS_METHOD_GET_ROOT_URI, null, url);
    }
    
    protected static Object getRoot(final URL url) throws IOException {
        return invokeVfsMethod(VfsUtils.VFS_METHOD_GET_ROOT_URL, null, url);
    }
    
    protected static Object doGetVisitorAttribute() {
        return ReflectionUtils.getField(VfsUtils.VISITOR_ATTRIBUTES_FIELD_RECURSE, null);
    }
    
    protected static String doGetPath(final Object resource) {
        return (String)ReflectionUtils.invokeMethod(VfsUtils.VIRTUAL_FILE_METHOD_GET_PATH_NAME, resource);
    }
    
    static {
        VfsUtils.VFS_METHOD_GET_ROOT_URL = null;
        VfsUtils.VFS_METHOD_GET_ROOT_URI = null;
        VfsUtils.VIRTUAL_FILE_METHOD_EXISTS = null;
        VfsUtils.VISITOR_ATTRIBUTES_FIELD_RECURSE = null;
        VfsUtils.GET_PHYSICAL_FILE = null;
        final ClassLoader loader = VfsUtils.class.getClassLoader();
        try {
            final Class<?> vfsClass = loader.loadClass("org.jboss.vfs.VFS");
            VfsUtils.VFS_METHOD_GET_ROOT_URL = ReflectionUtils.findMethod(vfsClass, "getChild", URL.class);
            VfsUtils.VFS_METHOD_GET_ROOT_URI = ReflectionUtils.findMethod(vfsClass, "getChild", URI.class);
            final Class<?> virtualFile = loader.loadClass("org.jboss.vfs.VirtualFile");
            VfsUtils.VIRTUAL_FILE_METHOD_EXISTS = ReflectionUtils.findMethod(virtualFile, "exists");
            VfsUtils.VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = ReflectionUtils.findMethod(virtualFile, "openStream");
            VfsUtils.VIRTUAL_FILE_METHOD_GET_SIZE = ReflectionUtils.findMethod(virtualFile, "getSize");
            VfsUtils.VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = ReflectionUtils.findMethod(virtualFile, "getLastModified");
            VfsUtils.VIRTUAL_FILE_METHOD_TO_URI = ReflectionUtils.findMethod(virtualFile, "toURI");
            VfsUtils.VIRTUAL_FILE_METHOD_TO_URL = ReflectionUtils.findMethod(virtualFile, "toURL");
            VfsUtils.VIRTUAL_FILE_METHOD_GET_NAME = ReflectionUtils.findMethod(virtualFile, "getName");
            VfsUtils.VIRTUAL_FILE_METHOD_GET_PATH_NAME = ReflectionUtils.findMethod(virtualFile, "getPathName");
            VfsUtils.GET_PHYSICAL_FILE = ReflectionUtils.findMethod(virtualFile, "getPhysicalFile");
            VfsUtils.VIRTUAL_FILE_METHOD_GET_CHILD = ReflectionUtils.findMethod(virtualFile, "getChild", String.class);
            VfsUtils.VIRTUAL_FILE_VISITOR_INTERFACE = loader.loadClass("org.jboss.vfs.VirtualFileVisitor");
            VfsUtils.VIRTUAL_FILE_METHOD_VISIT = ReflectionUtils.findMethod(virtualFile, "visit", VfsUtils.VIRTUAL_FILE_VISITOR_INTERFACE);
            final Class<?> visitorAttributesClass = loader.loadClass("org.jboss.vfs.VisitorAttributes");
            VfsUtils.VISITOR_ATTRIBUTES_FIELD_RECURSE = ReflectionUtils.findField(visitorAttributesClass, "RECURSE");
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
        }
    }
}
