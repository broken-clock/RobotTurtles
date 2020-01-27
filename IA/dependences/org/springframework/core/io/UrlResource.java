// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.File;
import java.net.URLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.springframework.util.ResourceUtils;
import java.io.InputStream;
import org.springframework.util.StringUtils;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import org.springframework.util.Assert;
import java.net.URL;
import java.net.URI;

public class UrlResource extends AbstractFileResolvingResource
{
    private final URI uri;
    private final URL url;
    private final URL cleanedUrl;
    
    public UrlResource(final URI uri) throws MalformedURLException {
        Assert.notNull(uri, "URI must not be null");
        this.uri = uri;
        this.url = uri.toURL();
        this.cleanedUrl = this.getCleanedUrl(this.url, uri.toString());
    }
    
    public UrlResource(final URL url) {
        Assert.notNull(url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = this.getCleanedUrl(this.url, url.toString());
        this.uri = null;
    }
    
    public UrlResource(final String path) throws MalformedURLException {
        Assert.notNull(path, "Path must not be null");
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = this.getCleanedUrl(this.url, path);
    }
    
    public UrlResource(final String protocol, final String location) throws MalformedURLException {
        this(protocol, location, null);
    }
    
    public UrlResource(final String protocol, final String location, final String fragment) throws MalformedURLException {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
            this.cleanedUrl = this.getCleanedUrl(this.url, this.uri.toString());
        }
        catch (URISyntaxException ex) {
            final MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }
    
    private URL getCleanedUrl(final URL originalUrl, final String originalPath) {
        try {
            return new URL(StringUtils.cleanPath(originalPath));
        }
        catch (MalformedURLException ex) {
            return originalUrl;
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        final URLConnection con = this.url.openConnection();
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        }
        catch (IOException ex) {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection)con).disconnect();
            }
            throw ex;
        }
    }
    
    @Override
    public URL getURL() throws IOException {
        return this.url;
    }
    
    @Override
    public URI getURI() throws IOException {
        if (this.uri != null) {
            return this.uri;
        }
        return super.getURI();
    }
    
    @Override
    public File getFile() throws IOException {
        if (this.uri != null) {
            return super.getFile(this.uri);
        }
        return super.getFile();
    }
    
    @Override
    public Resource createRelative(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return new UrlResource(new URL(this.url, relativePath));
    }
    
    @Override
    public String getFilename() {
        return new File(this.url.getFile()).getName();
    }
    
    @Override
    public String getDescription() {
        return "URL [" + this.url + "]";
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof UrlResource && this.cleanedUrl.equals(((UrlResource)obj).cleanedUrl));
    }
    
    @Override
    public int hashCode() {
        return this.cleanedUrl.hashCode();
    }
}
