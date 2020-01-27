// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.MergedStream;
import java.io.ByteArrayInputStream;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.format.InputAccessor;
import java.io.IOException;
import java.io.InputStream;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.util.Collection;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.databind.ObjectReader;

public class DataFormatReaders
{
    public static final int DEFAULT_MAX_INPUT_LOOKAHEAD = 64;
    protected final ObjectReader[] _readers;
    protected final MatchStrength _optimalMatch;
    protected final MatchStrength _minimalMatch;
    protected final int _maxInputLookahead;
    
    public DataFormatReaders(final ObjectReader... detectors) {
        this(detectors, MatchStrength.SOLID_MATCH, MatchStrength.WEAK_MATCH, 64);
    }
    
    public DataFormatReaders(final Collection<ObjectReader> detectors) {
        this((ObjectReader[])detectors.toArray(new ObjectReader[detectors.size()]));
    }
    
    private DataFormatReaders(final ObjectReader[] detectors, final MatchStrength optMatch, final MatchStrength minMatch, final int maxInputLookahead) {
        this._readers = detectors;
        this._optimalMatch = optMatch;
        this._minimalMatch = minMatch;
        this._maxInputLookahead = maxInputLookahead;
    }
    
    public DataFormatReaders withOptimalMatch(final MatchStrength optMatch) {
        if (optMatch == this._optimalMatch) {
            return this;
        }
        return new DataFormatReaders(this._readers, optMatch, this._minimalMatch, this._maxInputLookahead);
    }
    
    public DataFormatReaders withMinimalMatch(final MatchStrength minMatch) {
        if (minMatch == this._minimalMatch) {
            return this;
        }
        return new DataFormatReaders(this._readers, this._optimalMatch, minMatch, this._maxInputLookahead);
    }
    
    public DataFormatReaders with(final ObjectReader[] readers) {
        return new DataFormatReaders(readers, this._optimalMatch, this._minimalMatch, this._maxInputLookahead);
    }
    
    public DataFormatReaders withMaxInputLookahead(final int lookaheadBytes) {
        if (lookaheadBytes == this._maxInputLookahead) {
            return this;
        }
        return new DataFormatReaders(this._readers, this._optimalMatch, this._minimalMatch, lookaheadBytes);
    }
    
    public DataFormatReaders with(final DeserializationConfig config) {
        final int len = this._readers.length;
        final ObjectReader[] r = new ObjectReader[len];
        for (int i = 0; i < len; ++i) {
            r[i] = this._readers[i].with(config);
        }
        return new DataFormatReaders(r, this._optimalMatch, this._minimalMatch, this._maxInputLookahead);
    }
    
    public DataFormatReaders withType(final JavaType type) {
        final int len = this._readers.length;
        final ObjectReader[] r = new ObjectReader[len];
        for (int i = 0; i < len; ++i) {
            r[i] = this._readers[i].forType(type);
        }
        return new DataFormatReaders(r, this._optimalMatch, this._minimalMatch, this._maxInputLookahead);
    }
    
    public Match findFormat(final InputStream in) throws IOException {
        return this._findFormat(new AccessorForReader(in, new byte[this._maxInputLookahead]));
    }
    
    public Match findFormat(final byte[] fullInputData) throws IOException {
        return this._findFormat(new AccessorForReader(fullInputData));
    }
    
    public Match findFormat(final byte[] fullInputData, final int offset, final int len) throws IOException {
        return this._findFormat(new AccessorForReader(fullInputData, offset, len));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        final int len = this._readers.length;
        if (len > 0) {
            sb.append(this._readers[0].getFactory().getFormatName());
            for (int i = 1; i < len; ++i) {
                sb.append(", ");
                sb.append(this._readers[i].getFactory().getFormatName());
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    private Match _findFormat(final AccessorForReader acc) throws IOException {
        ObjectReader bestMatch = null;
        MatchStrength bestMatchStrength = null;
        for (final ObjectReader f : this._readers) {
            acc.reset();
            final MatchStrength strength = f.getFactory().hasFormat(acc);
            if (strength != null) {
                if (strength.ordinal() >= this._minimalMatch.ordinal()) {
                    if (bestMatch == null || bestMatchStrength.ordinal() < strength.ordinal()) {
                        bestMatch = f;
                        bestMatchStrength = strength;
                        if (strength.ordinal() >= this._optimalMatch.ordinal()) {
                            break;
                        }
                    }
                }
            }
        }
        return acc.createMatcher(bestMatch, bestMatchStrength);
    }
    
    protected class AccessorForReader extends InputAccessor.Std
    {
        public AccessorForReader(final InputStream in, final byte[] buffer) {
            super(in, buffer);
        }
        
        public AccessorForReader(final byte[] inputDocument) {
            super(inputDocument);
        }
        
        public AccessorForReader(final byte[] inputDocument, final int start, final int len) {
            super(inputDocument, start, len);
        }
        
        public Match createMatcher(final ObjectReader match, final MatchStrength matchStrength) {
            return new Match(this._in, this._buffer, this._bufferedStart, this._bufferedEnd - this._bufferedStart, match, matchStrength);
        }
    }
    
    public static class Match
    {
        protected final InputStream _originalStream;
        protected final byte[] _bufferedData;
        protected final int _bufferedStart;
        protected final int _bufferedLength;
        protected final ObjectReader _match;
        protected final MatchStrength _matchStrength;
        
        protected Match(final InputStream in, final byte[] buffered, final int bufferedStart, final int bufferedLength, final ObjectReader match, final MatchStrength strength) {
            this._originalStream = in;
            this._bufferedData = buffered;
            this._bufferedStart = bufferedStart;
            this._bufferedLength = bufferedLength;
            this._match = match;
            this._matchStrength = strength;
        }
        
        public boolean hasMatch() {
            return this._match != null;
        }
        
        public MatchStrength getMatchStrength() {
            return (this._matchStrength == null) ? MatchStrength.INCONCLUSIVE : this._matchStrength;
        }
        
        public ObjectReader getReader() {
            return this._match;
        }
        
        public String getMatchedFormatName() {
            return this._match.getFactory().getFormatName();
        }
        
        public JsonParser createParserWithMatch() throws IOException {
            if (this._match == null) {
                return null;
            }
            final JsonFactory jf = this._match.getFactory();
            if (this._originalStream == null) {
                return jf.createParser(this._bufferedData, this._bufferedStart, this._bufferedLength);
            }
            return jf.createParser(this.getDataStream());
        }
        
        public InputStream getDataStream() {
            if (this._originalStream == null) {
                return new ByteArrayInputStream(this._bufferedData, this._bufferedStart, this._bufferedLength);
            }
            return new MergedStream(null, this._originalStream, this._bufferedData, this._bufferedStart, this._bufferedLength);
        }
    }
}
