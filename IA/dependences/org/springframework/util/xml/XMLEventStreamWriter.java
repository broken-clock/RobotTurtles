// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.NamespaceContext;
import org.springframework.util.Assert;
import java.util.ArrayList;
import javax.xml.stream.events.EndElement;
import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;

class XMLEventStreamWriter implements XMLStreamWriter
{
    private static final String DEFAULT_ENCODING = "UTF-8";
    private final XMLEventWriter eventWriter;
    private final XMLEventFactory eventFactory;
    private final List<EndElement> endElements;
    private boolean emptyElement;
    
    public XMLEventStreamWriter(final XMLEventWriter eventWriter, final XMLEventFactory eventFactory) {
        this.endElements = new ArrayList<EndElement>();
        this.emptyElement = false;
        Assert.notNull(eventWriter, "'eventWriter' must not be null");
        Assert.notNull(eventFactory, "'eventFactory' must not be null");
        this.eventWriter = eventWriter;
        this.eventFactory = eventFactory;
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        this.eventWriter.setNamespaceContext(context);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.eventWriter.getNamespaceContext();
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.eventWriter.setPrefix(prefix, uri);
    }
    
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.eventWriter.getPrefix(uri);
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.eventWriter.setDefaultNamespace(uri);
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createStartDocument());
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createStartDocument("UTF-8", version));
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createStartDocument(encoding, version));
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.doWriteStartElement(this.eventFactory.createStartElement(new QName(localName), null, null));
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.doWriteStartElement(this.eventFactory.createStartElement(new QName(namespaceURI, localName), null, null));
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.doWriteStartElement(this.eventFactory.createStartElement(new QName(namespaceURI, localName, prefix), null, null));
    }
    
    private void doWriteStartElement(final StartElement startElement) throws XMLStreamException {
        this.eventWriter.add(startElement);
        this.endElements.add(this.eventFactory.createEndElement(startElement.getName(), startElement.getNamespaces()));
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.writeStartElement(localName);
        this.emptyElement = true;
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.writeStartElement(namespaceURI, localName);
        this.emptyElement = true;
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.writeStartElement(prefix, localName, namespaceURI);
        this.emptyElement = true;
    }
    
    private void closeEmptyElementIfNecessary() throws XMLStreamException {
        if (this.emptyElement) {
            this.emptyElement = false;
            this.writeEndElement();
        }
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        final int last = this.endElements.size() - 1;
        final EndElement lastEndElement = this.endElements.get(last);
        this.eventWriter.add(lastEndElement);
        this.endElements.remove(last);
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createAttribute(localName, value));
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createAttribute(new QName(namespaceURI, localName), value));
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createAttribute(prefix, namespaceURI, localName, value));
    }
    
    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        this.doWriteNamespace(this.eventFactory.createNamespace(prefix, namespaceURI));
    }
    
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        this.doWriteNamespace(this.eventFactory.createNamespace(namespaceURI));
    }
    
    private void doWriteNamespace(final Namespace namespace) throws XMLStreamException {
        final int last = this.endElements.size() - 1;
        final EndElement oldEndElement = this.endElements.get(last);
        final Iterator oldNamespaces = oldEndElement.getNamespaces();
        final List<Namespace> newNamespaces = new ArrayList<Namespace>();
        while (oldNamespaces.hasNext()) {
            final Namespace oldNamespace = oldNamespaces.next();
            newNamespaces.add(oldNamespace);
        }
        newNamespaces.add(namespace);
        final EndElement newEndElement = this.eventFactory.createEndElement(oldEndElement.getName(), newNamespaces.iterator());
        this.eventWriter.add(namespace);
        this.endElements.set(last, newEndElement);
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createCharacters(text));
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createCharacters(new String(text, start, len)));
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createCData(data));
    }
    
    @Override
    public void writeComment(final String data) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createComment(data));
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createProcessingInstruction(target, ""));
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createProcessingInstruction(target, data));
    }
    
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createDTD(dtd));
    }
    
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createEntityReference(name, null));
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.add(this.eventFactory.createEndDocument());
    }
    
    @Override
    public void flush() throws XMLStreamException {
        this.eventWriter.flush();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.closeEmptyElementIfNecessary();
        this.eventWriter.close();
    }
}
