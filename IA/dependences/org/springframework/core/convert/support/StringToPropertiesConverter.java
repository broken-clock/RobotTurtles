// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import org.springframework.core.convert.converter.Converter;

final class StringToPropertiesConverter implements Converter<String, Properties>
{
    @Override
    public Properties convert(final String source) {
        try {
            final Properties props = new Properties();
            props.load(new ByteArrayInputStream(source.getBytes("ISO-8859-1")));
            return props;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse [" + source + "] into Properties", ex);
        }
    }
}
