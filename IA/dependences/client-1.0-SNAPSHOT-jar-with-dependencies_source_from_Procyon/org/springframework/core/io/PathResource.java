// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io;

import java.io.OutputStream;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.net.URI;
import java.nio.file.Paths;
import org.springframework.util.Assert;
import java.nio.file.Path;

public class PathResource extends AbstractResource implements WritableResource
{
    private final Path path;
    
    public PathResource(final Path path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path.normalize();
    }
    
    public PathResource(final String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = Paths.get(path, new String[0]).normalize();
    }
    
    public PathResource(final URI uri) {
        Assert.notNull(uri, "URI must not be null");
        this.path = Paths.get(uri).normalize();
    }
    
    public final String getPath() {
        return this.path.toString();
    }
    
    @Override
    public boolean exists() {
        return Files.exists(this.path, new LinkOption[0]);
    }
    
    @Override
    public boolean isReadable() {
        return Files.isReadable(this.path) && !Files.isDirectory(this.path, new LinkOption[0]);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.exists()) {
            throw new FileNotFoundException(this.getPath() + " (No such file or directory)");
        }
        if (Files.isDirectory(this.path, new LinkOption[0])) {
            throw new FileNotFoundException(this.getPath() + " (Is a directory)");
        }
        return Files.newInputStream(this.path, new OpenOption[0]);
    }
    
    @Override
    public URL getURL() throws IOException {
        return this.path.toUri().toURL();
    }
    
    @Override
    public URI getURI() throws IOException {
        return this.path.toUri();
    }
    
    @Override
    public File getFile() throws IOException {
        try {
            return this.path.toFile();
        }
        catch (UnsupportedOperationException ex) {
            throw new FileNotFoundException(this.path + " cannot be resolved to " + "absolute file path");
        }
    }
    
    @Override
    public long contentLength() throws IOException {
        return Files.size(this.path);
    }
    
    @Override
    public long lastModified() throws IOException {
        return Files.getLastModifiedTime(this.path, new LinkOption[0]).toMillis();
    }
    
    @Override
    public Resource createRelative(final String relativePath) throws IOException {
        return new PathResource(this.path.resolve(relativePath));
    }
    
    @Override
    public String getFilename() {
        return this.path.getFileName().toString();
    }
    
    @Override
    public String getDescription() {
        return "path [" + this.path.toAbsolutePath() + "]";
    }
    
    @Override
    public boolean isWritable() {
        return Files.isWritable(this.path) && !Files.isDirectory(this.path, new LinkOption[0]);
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (Files.isDirectory(this.path, new LinkOption[0])) {
            throw new FileNotFoundException(this.getPath() + " (Is a directory)");
        }
        return Files.newOutputStream(this.path, new OpenOption[0]);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof PathResource && this.path.equals(((PathResource)obj).path));
    }
    
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }
}
