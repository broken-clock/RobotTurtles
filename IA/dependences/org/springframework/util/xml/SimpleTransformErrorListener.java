// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import javax.xml.transform.TransformerException;
import org.apache.commons.logging.Log;
import javax.xml.transform.ErrorListener;

public class SimpleTransformErrorListener implements ErrorListener
{
    private final Log logger;
    
    public SimpleTransformErrorListener(final Log logger) {
        this.logger = logger;
    }
    
    @Override
    public void warning(final TransformerException ex) throws TransformerException {
        this.logger.warn("XSLT transformation warning", ex);
    }
    
    @Override
    public void error(final TransformerException ex) throws TransformerException {
        this.logger.error("XSLT transformation error", ex);
    }
    
    @Override
    public void fatalError(final TransformerException ex) throws TransformerException {
        throw ex;
    }
}
