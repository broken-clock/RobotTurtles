// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format;

import java.util.Locale;

public interface Printer<T>
{
    String print(final T p0, final Locale p1);
}
