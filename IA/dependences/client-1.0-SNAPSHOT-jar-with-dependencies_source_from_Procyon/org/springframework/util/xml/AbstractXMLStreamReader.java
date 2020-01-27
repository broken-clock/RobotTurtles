// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import javax.xml.namespace.QName;
import org.springframework.util.Assert;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

abstract class AbstractXMLStreamReader implements XMLStreamReader
{
    @Override
    public String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text", this.getLocation());
        }
        int eventType = this.next();
        final StringBuilder builder = new StringBuilder();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                builder.append(this.getText());
            }
            else if (eventType != 3) {
                if (eventType != 5) {
                    if (eventType == 8) {
                        throw new XMLStreamException("unexpected end of document when reading element text content", this.getLocation());
                    }
                    if (eventType == 1) {
                        throw new XMLStreamException("element text content may not contain START_ELEMENT", this.getLocation());
                    }
                    throw new XMLStreamException("Unexpected event type " + eventType, this.getLocation());
                }
            }
            eventType = this.next();
        }
        return builder.toString();
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        return this.getAttributeName(index).getLocalPart();
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        return this.getAttributeName(index).getNamespaceURI();
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        return this.getAttributeName(index).getPrefix();
    }
    
    @Override
    public String getNamespaceURI() {
        final int eventType = this.getEventType();
        if (eventType == 1 || eventType == 2) {
            return this.getName().getNamespaceURI();
        }
        throw new IllegalStateException("parser must be on START_ELEMENT or END_ELEMENT state");
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        Assert.notNull(prefix, "No prefix given");
        return this.getNamespaceContext().getNamespaceURI(prefix);
    }
    
    @Override
    public boolean hasText() {
        final int eventType = this.getEventType();
        return eventType == 6 || eventType == 4 || eventType == 5 || eventType == 12 || eventType == 9;
    }
    
    @Override
    public String getPrefix() {
        final int eventType = this.getEventType();
        if (eventType == 1 || eventType == 2) {
            return this.getName().getPrefix();
        }
        throw new IllegalStateException("parser must be on START_ELEMENT or END_ELEMENT state");
    }
    
    @Override
    public boolean hasName() {
        final int eventType = this.getEventType();
        return eventType == 1 || eventType == 2;
    }
    
    @Override
    public boolean isWhiteSpace() {
        return this.getEventType() == 6;
    }
    
    @Override
    public boolean isStartElement() {
        return this.getEventType() == 1;
    }
    
    @Override
    public boolean isEndElement() {
        return this.getEventType() == 2;
    }
    
    @Override
    public boolean isCharacters() {
        return this.getEventType() == 4;
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        int eventType;
        for (eventType = this.next(); (eventType == 4 && this.isWhiteSpace()) || (eventType == 12 && this.isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {}
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException("expected start or end tag", this.getLocation());
        }
        return eventType;
    }
    
    @Override
    public void require(final int expectedType, final String namespaceURI, final String localName) throws XMLStreamException {
        final int eventType = this.getEventType();
        if (eventType != expectedType) {
            throw new XMLStreamException("Expected [" + expectedType + "] but read [" + eventType + "]");
        }
    }
    
    @Override
    public String getAttributeValue(final String namespaceURI, final String localName) {
        for (int i = 0; i < this.getAttributeCount(); ++i) {
            final QName name = this.getAttributeName(i);
            if (name.getLocalPart().equals(localName) && (namespaceURI == null || name.getNamespaceURI().equals(namespaceURI))) {
                return this.getAttributeValue(i);
            }
        }
        return null;
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        return this.getEventType() != 8;
    }
    
    @Override
    public String getLocalName() {
        return this.getName().getLocalPart();
    }
    
    @Override
    public char[] getTextCharacters() {
        return this.getText().toCharArray();
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, int length) throws XMLStreamException {
        final char[] source = this.getTextCharacters();
        length = Math.min(length, source.length);
        System.arraycopy(source, sourceStart, target, targetStart, length);
        return length;
    }
    
    @Override
    public int getTextLength() {
        return this.getText().length();
    }
}
