// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import javax.xml.stream.Location;
import org.xml.sax.Locator;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.springframework.util.StringUtils;
import javax.xml.namespace.QName;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class AbstractStaxXMLReader extends AbstractXMLReader
{
    private static final String NAMESPACES_FEATURE_NAME = "http://xml.org/sax/features/namespaces";
    private static final String NAMESPACE_PREFIXES_FEATURE_NAME = "http://xml.org/sax/features/namespace-prefixes";
    private static final String IS_STANDALONE_FEATURE_NAME = "http://xml.org/sax/features/is-standalone";
    private boolean namespacesFeature;
    private boolean namespacePrefixesFeature;
    private Boolean isStandalone;
    private final Map<String, String> namespaces;
    
    AbstractStaxXMLReader() {
        this.namespacesFeature = true;
        this.namespacePrefixesFeature = false;
        this.namespaces = new LinkedHashMap<String, String>();
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return this.namespacesFeature;
        }
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return this.namespacePrefixesFeature;
        }
        if (!"http://xml.org/sax/features/is-standalone".equals(name)) {
            return super.getFeature(name);
        }
        if (this.isStandalone != null) {
            return this.isStandalone;
        }
        throw new SAXNotSupportedException("startDocument() callback not completed yet");
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            this.namespacesFeature = value;
        }
        else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            this.namespacePrefixesFeature = value;
        }
        else {
            super.setFeature(name, value);
        }
    }
    
    protected void setStandalone(final boolean standalone) {
        this.isStandalone = standalone;
    }
    
    protected boolean hasNamespacesFeature() {
        return this.namespacesFeature;
    }
    
    protected boolean hasNamespacePrefixesFeature() {
        return this.namespacePrefixesFeature;
    }
    
    protected String toQualifiedName(final QName qName) {
        final String prefix = qName.getPrefix();
        if (!StringUtils.hasLength(prefix)) {
            return qName.getLocalPart();
        }
        return prefix + ":" + qName.getLocalPart();
    }
    
    @Override
    public final void parse(final InputSource ignored) throws SAXException {
        this.parse();
    }
    
    @Override
    public final void parse(final String ignored) throws SAXException {
        this.parse();
    }
    
    private void parse() throws SAXException {
        try {
            this.parseInternal();
        }
        catch (XMLStreamException ex) {
            Locator locator = null;
            if (ex.getLocation() != null) {
                locator = new StaxLocator(ex.getLocation());
            }
            final SAXParseException saxException = new SAXParseException(ex.getMessage(), locator, ex);
            if (this.getErrorHandler() == null) {
                throw saxException;
            }
            this.getErrorHandler().fatalError(saxException);
        }
    }
    
    protected abstract void parseInternal() throws SAXException, XMLStreamException;
    
    protected void startPrefixMapping(String prefix, final String namespace) throws SAXException {
        if (this.getContentHandler() != null) {
            if (prefix == null) {
                prefix = "";
            }
            if (!StringUtils.hasLength(namespace)) {
                return;
            }
            if (!namespace.equals(this.namespaces.get(prefix))) {
                this.getContentHandler().startPrefixMapping(prefix, namespace);
                this.namespaces.put(prefix, namespace);
            }
        }
    }
    
    protected void endPrefixMapping(final String prefix) throws SAXException {
        if (this.getContentHandler() != null && this.namespaces.containsKey(prefix)) {
            this.getContentHandler().endPrefixMapping(prefix);
            this.namespaces.remove(prefix);
        }
    }
    
    private static class StaxLocator implements Locator
    {
        private Location location;
        
        protected StaxLocator(final Location location) {
            this.location = location;
        }
        
        @Override
        public String getPublicId() {
            return this.location.getPublicId();
        }
        
        @Override
        public String getSystemId() {
            return this.location.getSystemId();
        }
        
        @Override
        public int getLineNumber() {
            return this.location.getLineNumber();
        }
        
        @Override
        public int getColumnNumber() {
            return this.location.getColumnNumber();
        }
    }
}
