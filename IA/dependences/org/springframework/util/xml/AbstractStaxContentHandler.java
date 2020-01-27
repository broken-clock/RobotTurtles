// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

abstract class AbstractStaxContentHandler implements ContentHandler
{
    private SimpleNamespaceContext namespaceContext;
    private boolean namespaceContextChanged;
    
    AbstractStaxContentHandler() {
        this.namespaceContext = new SimpleNamespaceContext();
        this.namespaceContextChanged = false;
    }
    
    @Override
    public final void startDocument() throws SAXException {
        this.namespaceContext.clear();
        this.namespaceContextChanged = false;
        try {
            this.startDocumentInternal();
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startDocument: " + ex.getMessage(), ex);
        }
    }
    
    protected abstract void startDocumentInternal() throws XMLStreamException;
    
    @Override
    public final void endDocument() throws SAXException {
        this.namespaceContext.clear();
        this.namespaceContextChanged = false;
        try {
            this.endDocumentInternal();
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startDocument: " + ex.getMessage(), ex);
        }
    }
    
    protected abstract void endDocumentInternal() throws XMLStreamException;
    
    @Override
    public final void startPrefixMapping(final String prefix, final String uri) {
        this.namespaceContext.bindNamespaceUri(prefix, uri);
        this.namespaceContextChanged = true;
    }
    
    @Override
    public final void endPrefixMapping(final String prefix) {
        this.namespaceContext.removeBinding(prefix);
        this.namespaceContextChanged = true;
    }
    
    @Override
    public final void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            this.startElementInternal(this.toQName(uri, qName), atts, this.namespaceContextChanged ? this.namespaceContext : null);
            this.namespaceContextChanged = false;
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startElement: " + ex.getMessage(), ex);
        }
    }
    
    protected abstract void startElementInternal(final QName p0, final Attributes p1, final SimpleNamespaceContext p2) throws XMLStreamException;
    
    @Override
    public final void endElement(final String uri, final String localName, final String qName) throws SAXException {
        try {
            this.endElementInternal(this.toQName(uri, qName), this.namespaceContextChanged ? this.namespaceContext : null);
            this.namespaceContextChanged = false;
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle endElement: " + ex.getMessage(), ex);
        }
    }
    
    protected abstract void endElementInternal(final QName p0, final SimpleNamespaceContext p1) throws XMLStreamException;
    
    @Override
    public final void characters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.charactersInternal(ch, start, length);
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle characters: " + ex.getMessage(), ex);
        }
    }
    
    protected abstract void charactersInternal(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    @Override
    public final void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.ignorableWhitespaceInternal(ch, start, length);
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle ignorableWhitespace:" + ex.getMessage(), ex);
        }
    }
    
    protected abstract void ignorableWhitespaceInternal(final char[] p0, final int p1, final int p2) throws XMLStreamException;
    
    @Override
    public final void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this.processingInstructionInternal(target, data);
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle processingInstruction: " + ex.getMessage(), ex);
        }
    }
    
    protected abstract void processingInstructionInternal(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public final void skippedEntity(final String name) throws SAXException {
        try {
            this.skippedEntityInternal(name);
        }
        catch (XMLStreamException ex) {
            throw new SAXException("Could not handle skippedEntity: " + ex.getMessage(), ex);
        }
    }
    
    protected QName toQName(final String namespaceUri, final String qualifiedName) {
        final int idx = qualifiedName.indexOf(58);
        if (idx == -1) {
            return new QName(namespaceUri, qualifiedName);
        }
        final String prefix = qualifiedName.substring(0, idx);
        final String localPart = qualifiedName.substring(idx + 1);
        return new QName(namespaceUri, localPart, prefix);
    }
    
    protected abstract void skippedEntityInternal(final String p0) throws XMLStreamException;
}
