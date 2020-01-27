// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.net.URLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class ResourceUtils
{
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String FILE_URL_PREFIX = "file:";
    public static final String URL_PROTOCOL_FILE = "file";
    public static final String URL_PROTOCOL_JAR = "jar";
    public static final String URL_PROTOCOL_ZIP = "zip";
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    public static final String URL_PROTOCOL_VFS = "vfs";
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    public static final String JAR_URL_SEPARATOR = "!/";
    
    public static boolean isUrl(final String resourceLocation) {
        if (resourceLocation == null) {
            return false;
        }
        if (resourceLocation.startsWith("classpath:")) {
            return true;
        }
        try {
            new URL(resourceLocation);
            return true;
        }
        catch (MalformedURLException ex) {
            return false;
        }
    }
    
    public static URL getURL(final String resourceLocation) throws FileNotFoundException {
        Assert.notNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith("classpath:")) {
            final String path = resourceLocation.substring("classpath:".length());
            final URL url = ClassUtils.getDefaultClassLoader().getResource(path);
            if (url == null) {
                final String description = "class path resource [" + path + "]";
                throw new FileNotFoundException(description + " cannot be resolved to URL because it does not exist");
            }
            return url;
        }
        else {
            try {
                return new URL(resourceLocation);
            }
            catch (MalformedURLException ex) {
                try {
                    return new File(resourceLocation).toURI().toURL();
                }
                catch (MalformedURLException ex2) {
                    throw new FileNotFoundException("Resource location [" + resourceLocation + "] is neither a URL not a well-formed file path");
                }
            }
        }
    }
    
    public static File getFile(final String resourceLocation) throws FileNotFoundException {
        Assert.notNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith("classpath:")) {
            final String path = resourceLocation.substring("classpath:".length());
            final String description = "class path resource [" + path + "]";
            final URL url = ClassUtils.getDefaultClassLoader().getResource(path);
            if (url == null) {
                throw new FileNotFoundException(description + " cannot be resolved to absolute file path " + "because it does not reside in the file system");
            }
            return getFile(url, description);
        }
        else {
            try {
                return getFile(new URL(resourceLocation));
            }
            catch (MalformedURLException ex) {
                return new File(resourceLocation);
            }
        }
    }
    
    public static File getFile(final URL resourceUrl) throws FileNotFoundException {
        return getFile(resourceUrl, "URL");
    }
    
    public static File getFile(final URL resourceUrl, final String description) throws FileNotFoundException {
        Assert.notNull(resourceUrl, "Resource URL must not be null");
        if (!"file".equals(resourceUrl.getProtocol())) {
            throw new FileNotFoundException(description + " cannot be resolved to absolute file path " + "because it does not reside in the file system: " + resourceUrl);
        }
        try {
            return new File(toURI(resourceUrl).getSchemeSpecificPart());
        }
        catch (URISyntaxException ex) {
            return new File(resourceUrl.getFile());
        }
    }
    
    public static File getFile(final URI resourceUri) throws FileNotFoundException {
        return getFile(resourceUri, "URI");
    }
    
    public static File getFile(final URI resourceUri, final String description) throws FileNotFoundException {
        Assert.notNull(resourceUri, "Resource URI must not be null");
        if (!"file".equals(resourceUri.getScheme())) {
            throw new FileNotFoundException(description + " cannot be resolved to absolute file path " + "because it does not reside in the file system: " + resourceUri);
        }
        return new File(resourceUri.getSchemeSpecificPart());
    }
    
    public static boolean isFileURL(final URL url) {
        final String protocol = url.getProtocol();
        return "file".equals(protocol) || protocol.startsWith("vfs");
    }
    
    public static boolean isJarURL(final URL url) {
        final String up = url.getProtocol();
        return "jar".equals(up) || "zip".equals(up) || "wsjar".equals(up);
    }
    
    public static URL extractJarFileURL(final URL jarUrl) throws MalformedURLException {
        final String urlFile = jarUrl.getFile();
        final int separatorIndex = urlFile.indexOf("!/");
        if (separatorIndex != -1) {
            String jarFile = urlFile.substring(0, separatorIndex);
            try {
                return new URL(jarFile);
            }
            catch (MalformedURLException ex) {
                if (!jarFile.startsWith("/")) {
                    jarFile = "/" + jarFile;
                }
                return new URL("file:" + jarFile);
            }
        }
        return jarUrl;
    }
    
    public static URI toURI(final URL url) throws URISyntaxException {
        return toURI(url.toString());
    }
    
    public static URI toURI(final String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }
    
    public static void useCachesIfNecessary(final URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }
}
