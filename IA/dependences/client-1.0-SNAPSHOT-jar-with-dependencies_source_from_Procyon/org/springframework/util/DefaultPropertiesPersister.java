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

public class DefaultPropertiesPersister implements PropertiesPersister
{
    @Override
    public void load(final Properties props, final InputStream is) throws IOException {
        props.load(is);
    }
    
    @Override
    public void load(final Properties props, final Reader reader) throws IOException {
        props.load(reader);
    }
    
    @Override
    public void store(final Properties props, final OutputStream os, final String header) throws IOException {
        props.store(os, header);
    }
    
    @Override
    public void store(final Properties props, final Writer writer, final String header) throws IOException {
        props.store(writer, header);
    }
    
    @Override
    public void loadFromXml(final Properties props, final InputStream is) throws IOException {
        props.loadFromXML(is);
    }
    
    @Override
    public void storeToXml(final Properties props, final OutputStream os, final String header) throws IOException {
        props.storeToXML(os, header);
    }
    
    @Override
    public void storeToXml(final Properties props, final OutputStream os, final String header, final String encoding) throws IOException {
        props.storeToXML(os, header, encoding);
    }
}
