// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import org.springframework.core.convert.converter.Converter;

final class PropertiesToStringConverter implements Converter<Properties, String>
{
    @Override
    public String convert(final Properties source) {
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            source.store(os, null);
            return os.toString("ISO-8859-1");
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Failed to store [" + source + "] into String", ex);
        }
    }
}
