// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import java.io.IOException;
import java.io.CharConversionException;
import org.springframework.util.StringUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

public class XmlValidationModeDetector
{
    public static final int VALIDATION_NONE = 0;
    public static final int VALIDATION_AUTO = 1;
    public static final int VALIDATION_DTD = 2;
    public static final int VALIDATION_XSD = 3;
    private static final String DOCTYPE = "DOCTYPE";
    private static final String START_COMMENT = "<!--";
    private static final String END_COMMENT = "-->";
    private boolean inComment;
    
    public int detectValidationMode(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            boolean isDtdValidated = false;
            String content;
            while ((content = reader.readLine()) != null) {
                content = this.consumeCommentTokens(content);
                if (!this.inComment) {
                    if (!StringUtils.hasText(content)) {
                        continue;
                    }
                    if (this.hasDoctype(content)) {
                        isDtdValidated = true;
                        break;
                    }
                    if (this.hasOpeningTag(content)) {
                        break;
                    }
                    continue;
                }
            }
            return isDtdValidated ? 2 : 3;
        }
        catch (CharConversionException ex) {
            return 1;
        }
        finally {
            reader.close();
        }
    }
    
    private boolean hasDoctype(final String content) {
        return content.indexOf("DOCTYPE") > -1;
    }
    
    private boolean hasOpeningTag(final String content) {
        if (this.inComment) {
            return false;
        }
        final int openTagIndex = content.indexOf(60);
        return openTagIndex > -1 && content.length() > openTagIndex && Character.isLetter(content.charAt(openTagIndex + 1));
    }
    
    private String consumeCommentTokens(String line) {
        if (line.indexOf("<!--") == -1 && line.indexOf("-->") == -1) {
            return line;
        }
        while ((line = this.consume(line)) != null) {
            if (!this.inComment && !line.trim().startsWith("<!--")) {
                return line;
            }
        }
        return line;
    }
    
    private String consume(final String line) {
        final int index = this.inComment ? this.endComment(line) : this.startComment(line);
        return (index == -1) ? null : line.substring(index);
    }
    
    private int startComment(final String line) {
        return this.commentToken(line, "<!--", true);
    }
    
    private int endComment(final String line) {
        return this.commentToken(line, "-->", false);
    }
    
    private int commentToken(final String line, final String token, final boolean inCommentIfPresent) {
        final int index = line.indexOf(token);
        if (index > -1) {
            this.inComment = inCommentIfPresent;
        }
        return (index == -1) ? index : (index + token.length());
    }
}
