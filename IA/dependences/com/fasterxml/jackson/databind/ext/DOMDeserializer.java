// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import org.w3c.dom.Node;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.w3c.dom.Document;
import com.fasterxml.jackson.databind.DeserializationContext;
import javax.xml.parsers.DocumentBuilderFactory;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

public abstract class DOMDeserializer<T> extends FromStringDeserializer<T>
{
    private static final long serialVersionUID = 1L;
    private static final DocumentBuilderFactory DEFAULT_PARSER_FACTORY;
    
    protected DOMDeserializer(final Class<T> cls) {
        super(cls);
    }
    
    public abstract T _deserialize(final String p0, final DeserializationContext p1);
    
    protected final Document parse(final String value) throws IllegalArgumentException {
        try {
            return this.documentBuilder().parse(new InputSource(new StringReader(value)));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse JSON String as XML: " + e.getMessage(), e);
        }
    }
    
    protected DocumentBuilder documentBuilder() throws ParserConfigurationException {
        return DOMDeserializer.DEFAULT_PARSER_FACTORY.newDocumentBuilder();
    }
    
    static {
        final DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        parserFactory.setExpandEntityReferences(false);
        try {
            parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (ParserConfigurationException ex) {}
        catch (Error error) {}
        DEFAULT_PARSER_FACTORY = parserFactory;
    }
    
    public static class NodeDeserializer extends DOMDeserializer<Node>
    {
        private static final long serialVersionUID = 1L;
        
        public NodeDeserializer() {
            super(Node.class);
        }
        
        @Override
        public Node _deserialize(final String value, final DeserializationContext ctxt) throws IllegalArgumentException {
            return this.parse(value);
        }
    }
    
    public static class DocumentDeserializer extends DOMDeserializer<Document>
    {
        private static final long serialVersionUID = 1L;
        
        public DocumentDeserializer() {
            super(Document.class);
        }
        
        @Override
        public Document _deserialize(final String value, final DeserializationContext ctxt) throws IllegalArgumentException {
            return this.parse(value);
        }
    }
}
