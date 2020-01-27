// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public interface DocumentLoader
{
    Document loadDocument(final InputSource p0, final EntityResolver p1, final ErrorHandler p2, final int p3, final boolean p4) throws Exception;
}
