// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.io.Writer;
import java.io.OutputStream;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface PropertiesPersister
{
    void load(final Properties p0, final InputStream p1) throws IOException;
    
    void load(final Properties p0, final Reader p1) throws IOException;
    
    void store(final Properties p0, final OutputStream p1, final String p2) throws IOException;
    
    void store(final Properties p0, final Writer p1, final String p2) throws IOException;
    
    void loadFromXml(final Properties p0, final InputStream p1) throws IOException;
    
    void storeToXml(final Properties p0, final OutputStream p1, final String p2) throws IOException;
    
    void storeToXml(final Properties p0, final OutputStream p1, final String p2, final String p3) throws IOException;
}
