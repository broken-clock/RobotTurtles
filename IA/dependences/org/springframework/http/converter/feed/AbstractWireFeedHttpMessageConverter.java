// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.feed;

import java.io.Writer;
import org.springframework.http.converter.HttpMessageNotWritableException;
import java.io.OutputStreamWriter;
import com.sun.syndication.io.WireFeedOutput;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import java.io.Reader;
import com.sun.syndication.io.FeedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.io.InputStreamReader;
import com.sun.syndication.io.WireFeedInput;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import java.nio.charset.Charset;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import com.sun.syndication.feed.WireFeed;

public abstract class AbstractWireFeedHttpMessageConverter<T extends WireFeed> extends AbstractHttpMessageConverter<T>
{
    public static final Charset DEFAULT_CHARSET;
    
    protected AbstractWireFeedHttpMessageConverter(final MediaType supportedMediaType) {
        super(supportedMediaType);
    }
    
    @Override
    protected T readInternal(final Class<? extends T> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final WireFeedInput feedInput = new WireFeedInput();
        final MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset;
        if (contentType != null && contentType.getCharSet() != null) {
            charset = contentType.getCharSet();
        }
        else {
            charset = AbstractWireFeedHttpMessageConverter.DEFAULT_CHARSET;
        }
        try {
            final Reader reader = new InputStreamReader(inputMessage.getBody(), charset);
            return (T)feedInput.build(reader);
        }
        catch (FeedException ex) {
            throw new HttpMessageNotReadableException("Could not read WireFeed: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    @Override
    protected void writeInternal(final T wireFeed, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        String wireFeedEncoding = wireFeed.getEncoding();
        if (!StringUtils.hasLength(wireFeedEncoding)) {
            wireFeedEncoding = AbstractWireFeedHttpMessageConverter.DEFAULT_CHARSET.name();
        }
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType != null) {
            final Charset wireFeedCharset = Charset.forName(wireFeedEncoding);
            contentType = new MediaType(contentType.getType(), contentType.getSubtype(), wireFeedCharset);
            outputMessage.getHeaders().setContentType(contentType);
        }
        final WireFeedOutput feedOutput = new WireFeedOutput();
        try {
            final Writer writer = new OutputStreamWriter(outputMessage.getBody(), wireFeedEncoding);
            feedOutput.output((WireFeed)wireFeed, writer);
        }
        catch (FeedException ex) {
            throw new HttpMessageNotWritableException("Could not write WiredFeed: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    static {
        DEFAULT_CHARSET = Charset.forName("UTF-8");
    }
}
