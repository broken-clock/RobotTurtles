// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.XMLReader;

abstract class AbstractXMLReader implements XMLReader
{
    private DTDHandler dtdHandler;
    private ContentHandler contentHandler;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private LexicalHandler lexicalHandler;
    
    @Override
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }
    
    @Override
    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }
    
    @Override
    public void setDTDHandler(final DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    @Override
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    protected LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(name);
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(name);
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            return this.lexicalHandler;
        }
        throw new SAXNotRecognizedException(name);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            this.lexicalHandler = (LexicalHandler)value;
            return;
        }
        throw new SAXNotRecognizedException(name);
    }
}
