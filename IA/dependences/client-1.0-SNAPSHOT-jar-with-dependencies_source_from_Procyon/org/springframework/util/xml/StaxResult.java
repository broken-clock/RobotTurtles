// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.util.XMLEventConsumer;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLEventWriter;
import javax.xml.transform.sax.SAXResult;

class StaxResult extends SAXResult
{
    private XMLEventWriter eventWriter;
    private XMLStreamWriter streamWriter;
    
    StaxResult(final XMLStreamWriter streamWriter) {
        super.setHandler(new StaxStreamContentHandler(streamWriter));
        this.streamWriter = streamWriter;
    }
    
    StaxResult(final XMLEventWriter eventWriter) {
        super.setHandler(new StaxEventContentHandler(eventWriter));
        this.eventWriter = eventWriter;
    }
    
    StaxResult(final XMLEventWriter eventWriter, final XMLEventFactory eventFactory) {
        super.setHandler(new StaxEventContentHandler(eventWriter, eventFactory));
        this.eventWriter = eventWriter;
    }
    
    XMLEventWriter getXMLEventWriter() {
        return this.eventWriter;
    }
    
    XMLStreamWriter getXMLStreamWriter() {
        return this.streamWriter;
    }
    
    @Override
    public void setHandler(final ContentHandler handler) {
        throw new UnsupportedOperationException("setHandler is not supported");
    }
}
