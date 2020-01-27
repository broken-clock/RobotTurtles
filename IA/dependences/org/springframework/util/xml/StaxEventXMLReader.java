// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Characters;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import org.xml.sax.Locator;
import javax.xml.stream.Location;
import org.xml.sax.ext.Locator2;
import org.springframework.util.StringUtils;
import javax.xml.stream.events.StartDocument;
import org.xml.sax.SAXException;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import org.springframework.util.Assert;
import javax.xml.stream.XMLEventReader;

class StaxEventXMLReader extends AbstractStaxXMLReader
{
    private static final String DEFAULT_XML_VERSION = "1.0";
    private final XMLEventReader reader;
    private String xmlVersion;
    private String encoding;
    
    StaxEventXMLReader(final XMLEventReader reader) {
        this.xmlVersion = "1.0";
        Assert.notNull(reader, "'reader' must not be null");
        try {
            final XMLEvent event = reader.peek();
            if (event != null && !event.isStartDocument() && !event.isStartElement()) {
                throw new IllegalStateException("XMLEventReader not at start of document or element");
            }
        }
        catch (XMLStreamException ex) {
            throw new IllegalStateException("Could not read first element: " + ex.getMessage());
        }
        this.reader = reader;
    }
    
    @Override
    protected void parseInternal() throws SAXException, XMLStreamException {
        boolean documentStarted = false;
        boolean documentEnded = false;
        int elementDepth = 0;
        while (this.reader.hasNext() && elementDepth >= 0) {
            final XMLEvent event = this.reader.nextEvent();
            if (!event.isStartDocument() && !event.isEndDocument() && !documentStarted) {
                this.handleStartDocument(event);
                documentStarted = true;
            }
            switch (event.getEventType()) {
                case 7: {
                    this.handleStartDocument(event);
                    documentStarted = true;
                    continue;
                }
                case 1: {
                    ++elementDepth;
                    this.handleStartElement(event.asStartElement());
                    continue;
                }
                case 2: {
                    if (--elementDepth >= 0) {
                        this.handleEndElement(event.asEndElement());
                        continue;
                    }
                    continue;
                }
                case 3: {
                    this.handleProcessingInstruction((ProcessingInstruction)event);
                    continue;
                }
                case 4:
                case 6:
                case 12: {
                    this.handleCharacters(event.asCharacters());
                    continue;
                }
                case 8: {
                    this.handleEndDocument();
                    documentEnded = true;
                    continue;
                }
                case 14: {
                    this.handleNotationDeclaration((NotationDeclaration)event);
                    continue;
                }
                case 15: {
                    this.handleEntityDeclaration((EntityDeclaration)event);
                    continue;
                }
                case 5: {
                    this.handleComment((Comment)event);
                    continue;
                }
                case 11: {
                    this.handleDtd((DTD)event);
                    continue;
                }
                case 9: {
                    this.handleEntityReference((EntityReference)event);
                    continue;
                }
            }
        }
        if (documentStarted && !documentEnded) {
            this.handleEndDocument();
        }
    }
    
    private void handleStartDocument(final XMLEvent event) throws SAXException {
        if (event.isStartDocument()) {
            final StartDocument startDocument = (StartDocument)event;
            final String xmlVersion = startDocument.getVersion();
            if (StringUtils.hasLength(xmlVersion)) {
                this.xmlVersion = xmlVersion;
            }
            if (startDocument.encodingSet()) {
                this.encoding = startDocument.getCharacterEncodingScheme();
            }
        }
        if (this.getContentHandler() != null) {
            final Location location = event.getLocation();
            this.getContentHandler().setDocumentLocator(new Locator2() {
                @Override
                public int getColumnNumber() {
                    return (location != null) ? location.getColumnNumber() : -1;
                }
                
                @Override
                public int getLineNumber() {
                    return (location != null) ? location.getLineNumber() : -1;
                }
                
                @Override
                public String getPublicId() {
                    return (location != null) ? location.getPublicId() : null;
                }
                
                @Override
                public String getSystemId() {
                    return (location != null) ? location.getSystemId() : null;
                }
                
                @Override
                public String getXMLVersion() {
                    return StaxEventXMLReader.this.xmlVersion;
                }
                
                @Override
                public String getEncoding() {
                    return StaxEventXMLReader.this.encoding;
                }
            });
            this.getContentHandler().startDocument();
        }
    }
    
    private void handleStartElement(final StartElement startElement) throws SAXException {
        if (this.getContentHandler() != null) {
            final QName qName = startElement.getName();
            if (this.hasNamespacesFeature()) {
                Iterator i = startElement.getNamespaces();
                while (i.hasNext()) {
                    final Namespace namespace = i.next();
                    this.startPrefixMapping(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                i = startElement.getAttributes();
                while (i.hasNext()) {
                    final Attribute attribute = i.next();
                    final QName attributeName = attribute.getName();
                    this.startPrefixMapping(attributeName.getPrefix(), attributeName.getNamespaceURI());
                }
                this.getContentHandler().startElement(qName.getNamespaceURI(), qName.getLocalPart(), this.toQualifiedName(qName), this.getAttributes(startElement));
            }
            else {
                this.getContentHandler().startElement("", "", this.toQualifiedName(qName), this.getAttributes(startElement));
            }
        }
    }
    
    private void handleCharacters(final Characters characters) throws SAXException {
        final char[] data = characters.getData().toCharArray();
        if (this.getContentHandler() != null && characters.isIgnorableWhiteSpace()) {
            this.getContentHandler().ignorableWhitespace(data, 0, data.length);
            return;
        }
        if (characters.isCData() && this.getLexicalHandler() != null) {
            this.getLexicalHandler().startCDATA();
        }
        if (this.getContentHandler() != null) {
            this.getContentHandler().characters(data, 0, data.length);
        }
        if (characters.isCData() && this.getLexicalHandler() != null) {
            this.getLexicalHandler().endCDATA();
        }
    }
    
    private void handleEndElement(final EndElement endElement) throws SAXException {
        if (this.getContentHandler() != null) {
            final QName qName = endElement.getName();
            if (this.hasNamespacesFeature()) {
                this.getContentHandler().endElement(qName.getNamespaceURI(), qName.getLocalPart(), this.toQualifiedName(qName));
                final Iterator i = endElement.getNamespaces();
                while (i.hasNext()) {
                    final Namespace namespace = i.next();
                    this.endPrefixMapping(namespace.getPrefix());
                }
            }
            else {
                this.getContentHandler().endElement("", "", this.toQualifiedName(qName));
            }
        }
    }
    
    private void handleEndDocument() throws SAXException {
        if (this.getContentHandler() != null) {
            this.getContentHandler().endDocument();
        }
    }
    
    private void handleNotationDeclaration(final NotationDeclaration declaration) throws SAXException {
        if (this.getDTDHandler() != null) {
            this.getDTDHandler().notationDecl(declaration.getName(), declaration.getPublicId(), declaration.getSystemId());
        }
    }
    
    private void handleEntityDeclaration(final EntityDeclaration entityDeclaration) throws SAXException {
        if (this.getDTDHandler() != null) {
            this.getDTDHandler().unparsedEntityDecl(entityDeclaration.getName(), entityDeclaration.getPublicId(), entityDeclaration.getSystemId(), entityDeclaration.getNotationName());
        }
    }
    
    private void handleProcessingInstruction(final ProcessingInstruction pi) throws SAXException {
        if (this.getContentHandler() != null) {
            this.getContentHandler().processingInstruction(pi.getTarget(), pi.getData());
        }
    }
    
    private void handleComment(final Comment comment) throws SAXException {
        if (this.getLexicalHandler() != null) {
            final char[] ch = comment.getText().toCharArray();
            this.getLexicalHandler().comment(ch, 0, ch.length);
        }
    }
    
    private void handleDtd(final DTD dtd) throws SAXException {
        if (this.getLexicalHandler() != null) {
            final Location location = dtd.getLocation();
            this.getLexicalHandler().startDTD(null, location.getPublicId(), location.getSystemId());
        }
        if (this.getLexicalHandler() != null) {
            this.getLexicalHandler().endDTD();
        }
    }
    
    private void handleEntityReference(final EntityReference reference) throws SAXException {
        if (this.getLexicalHandler() != null) {
            this.getLexicalHandler().startEntity(reference.getName());
        }
        if (this.getLexicalHandler() != null) {
            this.getLexicalHandler().endEntity(reference.getName());
        }
    }
    
    private Attributes getAttributes(final StartElement event) {
        final AttributesImpl attributes = new AttributesImpl();
        Iterator i = event.getAttributes();
        while (i.hasNext()) {
            final Attribute attribute = i.next();
            final QName qName = attribute.getName();
            String namespace = qName.getNamespaceURI();
            if (namespace == null || !this.hasNamespacesFeature()) {
                namespace = "";
            }
            String type = attribute.getDTDType();
            if (type == null) {
                type = "CDATA";
            }
            attributes.addAttribute(namespace, qName.getLocalPart(), this.toQualifiedName(qName), type, attribute.getValue());
        }
        if (this.hasNamespacePrefixesFeature()) {
            i = event.getNamespaces();
            while (i.hasNext()) {
                final Namespace namespace2 = i.next();
                final String prefix = namespace2.getPrefix();
                final String namespaceUri = namespace2.getNamespaceURI();
                String qName2;
                if (StringUtils.hasLength(prefix)) {
                    qName2 = "xmlns:" + prefix;
                }
                else {
                    qName2 = "xmlns";
                }
                attributes.addAttribute("", "", qName2, "CDATA", namespaceUri);
            }
        }
        return attributes;
    }
}
