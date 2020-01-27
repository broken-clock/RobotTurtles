// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.commons.logging.Log;
import org.xml.sax.ErrorHandler;

public class SimpleSaxErrorHandler implements ErrorHandler
{
    private final Log logger;
    
    public SimpleSaxErrorHandler(final Log logger) {
        this.logger = logger;
    }
    
    @Override
    public void warning(final SAXParseException ex) throws SAXException {
        this.logger.warn("Ignored XML validation warning", ex);
    }
    
    @Override
    public void error(final SAXParseException ex) throws SAXException {
        throw ex;
    }
    
    @Override
    public void fatalError(final SAXParseException ex) throws SAXException {
        throw ex;
    }
}
