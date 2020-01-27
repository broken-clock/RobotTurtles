// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import javax.xml.stream.XMLEventFactory;
import org.xml.sax.XMLReader;
import javax.xml.stream.util.XMLEventConsumer;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLEventWriter;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.Result;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.Source;
import javax.xml.stream.XMLStreamReader;

public abstract class StaxUtils
{
    public static Source createStaxSource(final XMLStreamReader streamReader) {
        return new StAXSource(streamReader);
    }
    
    public static Source createStaxSource(final XMLEventReader eventReader) throws XMLStreamException {
        return new StAXSource(eventReader);
    }
    
    public static Source createCustomStaxSource(final XMLStreamReader streamReader) {
        return new StaxSource(streamReader);
    }
    
    public static Source createCustomStaxSource(final XMLEventReader eventReader) {
        return new StaxSource(eventReader);
    }
    
    public static boolean isStaxSource(final Source source) {
        return source instanceof StAXSource || source instanceof StaxSource;
    }
    
    public static XMLStreamReader getXMLStreamReader(final Source source) {
        if (source instanceof StAXSource) {
            return ((StAXSource)source).getXMLStreamReader();
        }
        if (source instanceof StaxSource) {
            return ((StaxSource)source).getXMLStreamReader();
        }
        throw new IllegalArgumentException("Source '" + source + "' is neither StaxSource nor StAXSource");
    }
    
    public static XMLEventReader getXMLEventReader(final Source source) {
        if (source instanceof StAXSource) {
            return ((StAXSource)source).getXMLEventReader();
        }
        if (source instanceof StaxSource) {
            return ((StaxSource)source).getXMLEventReader();
        }
        throw new IllegalArgumentException("Source '" + source + "' is neither StaxSource nor StAXSource");
    }
    
    public static Result createStaxResult(final XMLStreamWriter streamWriter) {
        return new StAXResult(streamWriter);
    }
    
    public static Result createStaxResult(final XMLEventWriter eventWriter) {
        return new StAXResult(eventWriter);
    }
    
    public static Result createCustomStaxResult(final XMLStreamWriter streamWriter) {
        return new StaxResult(streamWriter);
    }
    
    public static Result createCustomStaxResult(final XMLEventWriter eventWriter) {
        return new StaxResult(eventWriter);
    }
    
    public static boolean isStaxResult(final Result result) {
        return result instanceof StAXResult || result instanceof StaxResult;
    }
    
    public static XMLStreamWriter getXMLStreamWriter(final Result result) {
        if (result instanceof StAXResult) {
            return ((StAXResult)result).getXMLStreamWriter();
        }
        if (result instanceof StaxResult) {
            return ((StaxResult)result).getXMLStreamWriter();
        }
        throw new IllegalArgumentException("Result '" + result + "' is neither StaxResult nor StAXResult");
    }
    
    public static XMLEventWriter getXMLEventWriter(final Result result) {
        if (result instanceof StAXResult) {
            return ((StAXResult)result).getXMLEventWriter();
        }
        if (result instanceof StaxResult) {
            return ((StaxResult)result).getXMLEventWriter();
        }
        throw new IllegalArgumentException("Result '" + result + "' is neither StaxResult nor StAXResult");
    }
    
    public static ContentHandler createContentHandler(final XMLStreamWriter streamWriter) {
        return new StaxStreamContentHandler(streamWriter);
    }
    
    public static ContentHandler createContentHandler(final XMLEventWriter eventWriter) {
        return new StaxEventContentHandler(eventWriter);
    }
    
    public static XMLReader createXMLReader(final XMLStreamReader streamReader) {
        return new StaxStreamXMLReader(streamReader);
    }
    
    public static XMLReader createXMLReader(final XMLEventReader eventReader) {
        return new StaxEventXMLReader(eventReader);
    }
    
    public static XMLStreamReader createEventStreamReader(final XMLEventReader eventReader) throws XMLStreamException {
        return new XMLEventStreamReader(eventReader);
    }
    
    public static XMLStreamWriter createEventStreamWriter(final XMLEventWriter eventWriter) {
        return new XMLEventStreamWriter(eventWriter, XMLEventFactory.newFactory());
    }
    
    public static XMLStreamWriter createEventStreamWriter(final XMLEventWriter eventWriter, final XMLEventFactory eventFactory) {
        return new XMLEventStreamWriter(eventWriter, eventFactory);
    }
}
