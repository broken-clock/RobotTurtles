// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format;

import java.text.ParseException;
import java.util.Locale;

public interface Parser<T>
{
    T parse(final String p0, final Locale p1) throws ParseException;
}
