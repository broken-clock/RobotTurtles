// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import java.util.Iterator;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Locator;
import org.springframework.util.Assert;
import javax.xml.stream.XMLStreamWriter;

class StaxStreamContentHandler extends AbstractStaxContentHandler
{
    private final XMLStreamWriter streamWriter;
    
    StaxStreamContentHandler(final XMLStreamWriter streamWriter) {
        Assert.notNull(streamWriter, "'streamWriter' must not be null");
        this.streamWriter = streamWriter;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    protected void charactersInternal(final char[] ch, final int start, final int length) throws XMLStreamException {
        this.streamWriter.writeCharacters(ch, start, length);
    }
    
    @Override
    protected void endDocumentInternal() throws XMLStreamException {
        this.streamWriter.writeEndDocument();
    }
    
    @Override
    protected void endElementInternal(final QName name, final SimpleNamespaceContext namespaceContext) throws XMLStreamException {
        this.streamWriter.writeEndElement();
    }
    
    @Override
    protected void ignorableWhitespaceInternal(final char[] ch, final int start, final int length) throws XMLStreamException {
        this.streamWriter.writeCharacters(ch, start, length);
    }
    
    @Override
    protected void processingInstructionInternal(final String target, final String data) throws XMLStreamException {
        this.streamWriter.writeProcessingInstruction(target, data);
    }
    
    @Override
    protected void skippedEntityInternal(final String name) {
    }
    
    @Override
    protected void startDocumentInternal() throws XMLStreamException {
        this.streamWriter.writeStartDocument();
    }
    
    @Override
    protected void startElementInternal(final QName name, final Attributes attributes, final SimpleNamespaceContext namespaceContext) throws XMLStreamException {
        this.streamWriter.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
        if (namespaceContext != null) {
            final String defaultNamespaceUri = namespaceContext.getNamespaceURI("");
            if (StringUtils.hasLength(defaultNamespaceUri)) {
                this.streamWriter.writeNamespace("", defaultNamespaceUri);
                this.streamWriter.setDefaultNamespace(defaultNamespaceUri);
            }
            final Iterator<String> iterator = namespaceContext.getBoundPrefixes();
            while (iterator.hasNext()) {
                final String prefix = iterator.next();
                this.streamWriter.writeNamespace(prefix, namespaceContext.getNamespaceURI(prefix));
                this.streamWriter.setPrefix(prefix, namespaceContext.getNamespaceURI(prefix));
            }
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            final QName attrName = this.toQName(attributes.getURI(i), attributes.getQName(i));
            if (!"xmlns".equals(attrName.getLocalPart()) && !"xmlns".equals(attrName.getPrefix())) {
                this.streamWriter.writeAttribute(attrName.getPrefix(), attrName.getNamespaceURI(), attrName.getLocalPart(), attributes.getValue(i));
            }
        }
    }
}
