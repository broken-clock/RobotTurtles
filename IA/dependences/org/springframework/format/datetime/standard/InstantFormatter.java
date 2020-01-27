// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.util.Locale;
import java.time.Instant;
import org.springframework.format.Formatter;

public class InstantFormatter implements Formatter<Instant>
{
    @Override
    public Instant parse(final String text, final Locale locale) throws ParseException {
        return Instant.parse(text);
    }
    
    @Override
    public String print(final Instant object, final Locale locale) {
        return object.toString();
    }
}
