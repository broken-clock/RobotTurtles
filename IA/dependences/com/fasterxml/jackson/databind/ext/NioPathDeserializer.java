// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import java.io.File;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Iterator;
import java.nio.file.FileSystemNotFoundException;
import java.util.ServiceLoader;
import java.nio.file.spi.FileSystemProvider;
import java.net.URISyntaxException;
import java.net.URI;
import java.nio.file.Paths;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class NioPathDeserializer extends StdScalarDeserializer<Path>
{
    private static final long serialVersionUID = 1L;
    private static final boolean areWindowsFilePathsSupported;
    
    public NioPathDeserializer() {
        super(Path.class);
    }
    
    @Override
    public Path deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            return (Path)ctxt.handleUnexpectedToken(Path.class, p);
        }
        final String value = p.getText();
        if (value.indexOf(58) < 0) {
            return Paths.get(value, new String[0]);
        }
        if (NioPathDeserializer.areWindowsFilePathsSupported && value.length() >= 2 && Character.isLetter(value.charAt(0)) && value.charAt(1) == ':') {
            return Paths.get(value, new String[0]);
        }
        URI uri;
        try {
            uri = new URI(value);
        }
        catch (URISyntaxException e) {
            return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, e);
        }
        try {
            return Paths.get(uri);
        }
        catch (FileSystemNotFoundException cause) {
            try {
                final String scheme = uri.getScheme();
                for (final FileSystemProvider provider : ServiceLoader.load(FileSystemProvider.class)) {
                    if (provider.getScheme().equalsIgnoreCase(scheme)) {
                        return provider.getPath(uri);
                    }
                }
                return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, cause);
            }
            catch (Throwable e2) {
                e2.addSuppressed(cause);
                return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, e2);
            }
        }
        catch (Throwable e3) {
            return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, e3);
        }
    }
    
    static {
        boolean isWindowsRootFound = false;
        for (final File file : File.listRoots()) {
            final String path = file.getPath();
            if (path.length() >= 2 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':') {
                isWindowsRootFound = true;
                break;
            }
        }
        areWindowsFilePathsSupported = isWindowsRootFound;
    }
}
