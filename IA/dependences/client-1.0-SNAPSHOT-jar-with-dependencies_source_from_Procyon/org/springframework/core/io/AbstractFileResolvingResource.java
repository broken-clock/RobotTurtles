// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.InputStream;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URI;
import java.io.IOException;
import java.net.URL;
import org.springframework.util.ResourceUtils;
import java.io.File;

public abstract class AbstractFileResolvingResource extends AbstractResource
{
    @Override
    public File getFile() throws IOException {
        final URL url = this.getURL();
        if (url.getProtocol().startsWith("vfs")) {
            return VfsResourceDelegate.getResource(url).getFile();
        }
        return ResourceUtils.getFile(url, this.getDescription());
    }
    
    @Override
    protected File getFileForLastModifiedCheck() throws IOException {
        final URL url = this.getURL();
        if (!ResourceUtils.isJarURL(url)) {
            return this.getFile();
        }
        final URL actualUrl = ResourceUtils.extractJarFileURL(url);
        if (actualUrl.getProtocol().startsWith("vfs")) {
            return VfsResourceDelegate.getResource(actualUrl).getFile();
        }
        return ResourceUtils.getFile(actualUrl, "Jar URL");
    }
    
    protected File getFile(final URI uri) throws IOException {
        if (uri.getScheme().startsWith("vfs")) {
            return VfsResourceDelegate.getResource(uri).getFile();
        }
        return ResourceUtils.getFile(uri, this.getDescription());
    }
    
    @Override
    public boolean exists() {
        try {
            final URL url = this.getURL();
            if (ResourceUtils.isFileURL(url)) {
                return this.getFile().exists();
            }
            final URLConnection con = url.openConnection();
            this.customizeConnection(con);
            final HttpURLConnection httpCon = (con instanceof HttpURLConnection) ? ((HttpURLConnection)con) : null;
            if (httpCon != null) {
                final int code = httpCon.getResponseCode();
                if (code == 200) {
                    return true;
                }
                if (code == 404) {
                    return false;
                }
            }
            if (con.getContentLength() >= 0) {
                return true;
            }
            if (httpCon != null) {
                httpCon.disconnect();
                return false;
            }
            final InputStream is = this.getInputStream();
            is.close();
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    @Override
    public boolean isReadable() {
        try {
            final URL url = this.getURL();
            if (ResourceUtils.isFileURL(url)) {
                final File file = this.getFile();
                return file.canRead() && !file.isDirectory();
            }
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    @Override
    public long contentLength() throws IOException {
        final URL url = this.getURL();
        if (ResourceUtils.isFileURL(url)) {
            return this.getFile().length();
        }
        final URLConnection con = url.openConnection();
        this.customizeConnection(con);
        return con.getContentLength();
    }
    
    @Override
    public long lastModified() throws IOException {
        final URL url = this.getURL();
        if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
            return super.lastModified();
        }
        final URLConnection con = url.openConnection();
        this.customizeConnection(con);
        return con.getLastModified();
    }
    
    protected void customizeConnection(final URLConnection con) throws IOException {
        ResourceUtils.useCachesIfNecessary(con);
        if (con instanceof HttpURLConnection) {
            this.customizeConnection((HttpURLConnection)con);
        }
    }
    
    protected void customizeConnection(final HttpURLConnection con) throws IOException {
        con.setRequestMethod("HEAD");
    }
    
    private static class VfsResourceDelegate
    {
        public static Resource getResource(final URL url) throws IOException {
            return new VfsResource(VfsUtils.getRoot(url));
        }
        
        public static Resource getResource(final URI uri) throws IOException {
            return new VfsResource(VfsUtils.getRoot(uri));
        }
    }
}
