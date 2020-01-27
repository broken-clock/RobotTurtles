// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import java.util.Iterator;
import org.xml.sax.Attributes;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.Location;
import org.xml.sax.Locator;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.stream.XMLEventFactory;

class StaxEventContentHandler extends AbstractStaxContentHandler
{
    private final XMLEventFactory eventFactory;
    private final XMLEventConsumer eventConsumer;
    
    StaxEventContentHandler(final XMLEventConsumer consumer) {
        this.eventFactory = XMLEventFactory.newInstance();
        this.eventConsumer = consumer;
    }
    
    StaxEventContentHandler(final XMLEventConsumer consumer, final XMLEventFactory factory) {
        this.eventFactory = factory;
        this.eventConsumer = consumer;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        if (locator != null) {
            this.eventFactory.setLocation(new LocatorLocationAdapter(locator));
        }
    }
    
    @Override
    protected void startDocumentInternal() throws XMLStreamException {
        this.consumeEvent(this.eventFactory.createStartDocument());
    }
    
    @Override
    protected void endDocumentInternal() throws XMLStreamException {
        this.consumeEvent(this.eventFactory.createEndDocument());
    }
    
    @Override
    protected void startElementInternal(final QName name, final Attributes atts, final SimpleNamespaceContext namespaceContext) throws XMLStreamException {
        final List<Attribute> attributes = this.getAttributes(atts);
        final List<Namespace> namespaces = this.createNamespaces(namespaceContext);
        this.consumeEvent(this.eventFactory.createStartElement(name, attributes.iterator(), (namespaces != null) ? namespaces.iterator() : null));
    }
    
    @Override
    protected void endElementInternal(final QName name, final SimpleNamespaceContext namespaceContext) throws XMLStreamException {
        final List<Namespace> namespaces = this.createNamespaces(namespaceContext);
        this.consumeEvent(this.eventFactory.createEndElement(name, (namespaces != null) ? namespaces.iterator() : null));
    }
    
    @Override
    protected void charactersInternal(final char[] ch, final int start, final int length) throws XMLStreamException {
        this.consumeEvent(this.eventFactory.createCharacters(new String(ch, start, length)));
    }
    
    @Override
    protected void ignorableWhitespaceInternal(final char[] ch, final int start, final int length) throws XMLStreamException {
        this.consumeEvent(this.eventFactory.createIgnorableSpace(new String(ch, start, length)));
    }
    
    @Override
    protected void processingInstructionInternal(final String target, final String data) throws XMLStreamException {
        this.consumeEvent(this.eventFactory.createProcessingInstruction(target, data));
    }
    
    private void consumeEvent(final XMLEvent event) throws XMLStreamException {
        this.eventConsumer.add(event);
    }
    
    private List<Namespace> createNamespaces(final SimpleNamespaceContext namespaceContext) {
        if (namespaceContext == null) {
            return null;
        }
        final List<Namespace> namespaces = new ArrayList<Namespace>();
        final String defaultNamespaceUri = namespaceContext.getNamespaceURI("");
        if (StringUtils.hasLength(defaultNamespaceUri)) {
            namespaces.add(this.eventFactory.createNamespace(defaultNamespaceUri));
        }
        final Iterator<String> iterator = namespaceContext.getBoundPrefixes();
        while (iterator.hasNext()) {
            final String prefix = iterator.next();
            final String namespaceUri = namespaceContext.getNamespaceURI(prefix);
            namespaces.add(this.eventFactory.createNamespace(prefix, namespaceUri));
        }
        return namespaces;
    }
    
    private List<Attribute> getAttributes(final Attributes attributes) {
        final List<Attribute> list = new ArrayList<Attribute>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final QName name = this.toQName(attributes.getURI(i), attributes.getQName(i));
            if (!"xmlns".equals(name.getLocalPart()) && !"xmlns".equals(name.getPrefix())) {
                list.add(this.eventFactory.createAttribute(name, attributes.getValue(i)));
            }
        }
        return list;
    }
    
    @Override
    protected void skippedEntityInternal(final String name) throws XMLStreamException {
    }
    
    private static final class LocatorLocationAdapter implements Location
    {
        private final Locator locator;
        
        public LocatorLocationAdapter(final Locator locator) {
            this.locator = locator;
        }
        
        @Override
        public int getLineNumber() {
            return this.locator.getLineNumber();
        }
        
        @Override
        public int getColumnNumber() {
            return this.locator.getColumnNumber();
        }
        
        @Override
        public int getCharacterOffset() {
            return -1;
        }
        
        @Override
        public String getPublicId() {
            return this.locator.getPublicId();
        }
        
        @Override
        public String getSystemId() {
            return this.locator.getSystemId();
        }
    }
}
