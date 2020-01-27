// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.xml.sax.Locator;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.springframework.util.Assert;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.List;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

class DomContentHandler implements ContentHandler
{
    private final Document document;
    private final List<Element> elements;
    private final Node node;
    
    DomContentHandler(final Node node) {
        this.elements = new ArrayList<Element>();
        Assert.notNull(node, "node must not be null");
        this.node = node;
        if (node instanceof Document) {
            this.document = (Document)node;
        }
        else {
            this.document = node.getOwnerDocument();
        }
        Assert.notNull(this.document, "document must not be null");
    }
    
    private Node getParent() {
        if (!this.elements.isEmpty()) {
            return this.elements.get(this.elements.size() - 1);
        }
        return this.node;
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        final Node parent = this.getParent();
        Element element = this.document.createElementNS(uri, qName);
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String attrUri = attributes.getURI(i);
            final String attrQname = attributes.getQName(i);
            final String value = attributes.getValue(i);
            if (!attrQname.startsWith("xmlns")) {
                element.setAttributeNS(attrUri, attrQname, value);
            }
        }
        element = (Element)parent.appendChild(element);
        this.elements.add(element);
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.elements.remove(this.elements.size() - 1);
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final String data = new String(ch, start, length);
        final Node parent = this.getParent();
        final Node lastChild = parent.getLastChild();
        if (lastChild != null && lastChild.getNodeType() == 3) {
            ((Text)lastChild).appendData(data);
        }
        else {
            final Text text = this.document.createTextNode(data);
            parent.appendChild(text);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        final Node parent = this.getParent();
        final ProcessingInstruction pi = this.document.createProcessingInstruction(target, data);
        parent.appendChild(pi);
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
}
