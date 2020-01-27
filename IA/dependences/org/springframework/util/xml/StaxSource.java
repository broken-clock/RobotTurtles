// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.transform.sax.SAXSource;

class StaxSource extends SAXSource
{
    private XMLEventReader eventReader;
    private XMLStreamReader streamReader;
    
    StaxSource(final XMLStreamReader streamReader) {
        super(new StaxStreamXMLReader(streamReader), new InputSource());
        this.streamReader = streamReader;
    }
    
    StaxSource(final XMLEventReader eventReader) {
        super(new StaxEventXMLReader(eventReader), new InputSource());
        this.eventReader = eventReader;
    }
    
    XMLEventReader getXMLEventReader() {
        return this.eventReader;
    }
    
    XMLStreamReader getXMLStreamReader() {
        return this.streamReader;
    }
    
    @Override
    public void setInputSource(final InputSource inputSource) {
        throw new UnsupportedOperationException("setInputSource is not supported");
    }
    
    @Override
    public void setXMLReader(final XMLReader reader) {
        throw new UnsupportedOperationException("setXMLReader is not supported");
    }
}
