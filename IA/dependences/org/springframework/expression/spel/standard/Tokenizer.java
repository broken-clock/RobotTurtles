// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.standard;

import org.springframework.util.Assert;
import java.util.Arrays;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelMessage;
import java.util.ArrayList;
import java.util.List;

class Tokenizer
{
    private static final String[] ALTERNATIVE_OPERATOR_NAMES;
    private static final byte[] FLAGS;
    private static final byte IS_DIGIT = 1;
    private static final byte IS_HEXDIGIT = 2;
    private static final byte IS_ALPHA = 4;
    String expressionString;
    char[] toProcess;
    int pos;
    int max;
    List<Token> tokens;
    
    public Tokenizer(final String inputdata) {
        this.tokens = new ArrayList<Token>();
        this.expressionString = inputdata;
        this.toProcess = (inputdata + "\u0000").toCharArray();
        this.max = this.toProcess.length;
        this.pos = 0;
        this.process();
    }
    
    public void process() {
        while (this.pos < this.max) {
            final char ch = this.toProcess[this.pos];
            if (this.isAlphabetic(ch)) {
                this.lexIdentifier();
            }
            else {
                switch (ch) {
                    case '+': {
                        if (this.isTwoCharToken(TokenKind.INC)) {
                            this.pushPairToken(TokenKind.INC);
                            continue;
                        }
                        this.pushCharToken(TokenKind.PLUS);
                        continue;
                    }
                    case '_': {
                        this.lexIdentifier();
                        continue;
                    }
                    case '-': {
                        if (this.isTwoCharToken(TokenKind.DEC)) {
                            this.pushPairToken(TokenKind.DEC);
                            continue;
                        }
                        this.pushCharToken(TokenKind.MINUS);
                        continue;
                    }
                    case ':': {
                        this.pushCharToken(TokenKind.COLON);
                        continue;
                    }
                    case '.': {
                        this.pushCharToken(TokenKind.DOT);
                        continue;
                    }
                    case ',': {
                        this.pushCharToken(TokenKind.COMMA);
                        continue;
                    }
                    case '*': {
                        this.pushCharToken(TokenKind.STAR);
                        continue;
                    }
                    case '/': {
                        this.pushCharToken(TokenKind.DIV);
                        continue;
                    }
                    case '%': {
                        this.pushCharToken(TokenKind.MOD);
                        continue;
                    }
                    case '(': {
                        this.pushCharToken(TokenKind.LPAREN);
                        continue;
                    }
                    case ')': {
                        this.pushCharToken(TokenKind.RPAREN);
                        continue;
                    }
                    case '[': {
                        this.pushCharToken(TokenKind.LSQUARE);
                        continue;
                    }
                    case '#': {
                        this.pushCharToken(TokenKind.HASH);
                        continue;
                    }
                    case ']': {
                        this.pushCharToken(TokenKind.RSQUARE);
                        continue;
                    }
                    case '{': {
                        this.pushCharToken(TokenKind.LCURLY);
                        continue;
                    }
                    case '}': {
                        this.pushCharToken(TokenKind.RCURLY);
                        continue;
                    }
                    case '@': {
                        this.pushCharToken(TokenKind.BEAN_REF);
                        continue;
                    }
                    case '^': {
                        if (this.isTwoCharToken(TokenKind.SELECT_FIRST)) {
                            this.pushPairToken(TokenKind.SELECT_FIRST);
                            continue;
                        }
                        this.pushCharToken(TokenKind.POWER);
                        continue;
                    }
                    case '!': {
                        if (this.isTwoCharToken(TokenKind.NE)) {
                            this.pushPairToken(TokenKind.NE);
                            continue;
                        }
                        if (this.isTwoCharToken(TokenKind.PROJECT)) {
                            this.pushPairToken(TokenKind.PROJECT);
                            continue;
                        }
                        this.pushCharToken(TokenKind.NOT);
                        continue;
                    }
                    case '=': {
                        if (this.isTwoCharToken(TokenKind.EQ)) {
                            this.pushPairToken(TokenKind.EQ);
                            continue;
                        }
                        this.pushCharToken(TokenKind.ASSIGN);
                        continue;
                    }
                    case '&': {
                        if (!this.isTwoCharToken(TokenKind.SYMBOLIC_AND)) {
                            throw new InternalParseException(new SpelParseException(this.expressionString, this.pos, SpelMessage.MISSING_CHARACTER, new Object[] { "&" }));
                        }
                        this.pushPairToken(TokenKind.SYMBOLIC_AND);
                        continue;
                    }
                    case '|': {
                        if (!this.isTwoCharToken(TokenKind.SYMBOLIC_OR)) {
                            throw new InternalParseException(new SpelParseException(this.expressionString, this.pos, SpelMessage.MISSING_CHARACTER, new Object[] { "|" }));
                        }
                        this.pushPairToken(TokenKind.SYMBOLIC_OR);
                        continue;
                    }
                    case '?': {
                        if (this.isTwoCharToken(TokenKind.SELECT)) {
                            this.pushPairToken(TokenKind.SELECT);
                            continue;
                        }
                        if (this.isTwoCharToken(TokenKind.ELVIS)) {
                            this.pushPairToken(TokenKind.ELVIS);
                            continue;
                        }
                        if (this.isTwoCharToken(TokenKind.SAFE_NAVI)) {
                            this.pushPairToken(TokenKind.SAFE_NAVI);
                            continue;
                        }
                        this.pushCharToken(TokenKind.QMARK);
                        continue;
                    }
                    case '$': {
                        if (this.isTwoCharToken(TokenKind.SELECT_LAST)) {
                            this.pushPairToken(TokenKind.SELECT_LAST);
                            continue;
                        }
                        this.lexIdentifier();
                        continue;
                    }
                    case '>': {
                        if (this.isTwoCharToken(TokenKind.GE)) {
                            this.pushPairToken(TokenKind.GE);
                            continue;
                        }
                        this.pushCharToken(TokenKind.GT);
                        continue;
                    }
                    case '<': {
                        if (this.isTwoCharToken(TokenKind.LE)) {
                            this.pushPairToken(TokenKind.LE);
                            continue;
                        }
                        this.pushCharToken(TokenKind.LT);
                        continue;
                    }
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                        this.lexNumericLiteral(ch == '0');
                        continue;
                    }
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        ++this.pos;
                        continue;
                    }
                    case '\'': {
                        this.lexQuotedStringLiteral();
                        continue;
                    }
                    case '\"': {
                        this.lexDoubleQuotedStringLiteral();
                        continue;
                    }
                    case '\0': {
                        ++this.pos;
                        continue;
                    }
                    case '\\': {
                        throw new InternalParseException(new SpelParseException(this.expressionString, this.pos, SpelMessage.UNEXPECTED_ESCAPE_CHAR, new Object[0]));
                    }
                    default: {
                        throw new IllegalStateException("Cannot handle (" + (Object)(int)ch + ") '" + ch + "'");
                    }
                }
            }
        }
    }
    
    public List<Token> getTokens() {
        return this.tokens;
    }
    
    private void lexQuotedStringLiteral() {
        final int start = this.pos;
        boolean terminated = false;
        while (!terminated) {
            ++this.pos;
            final char ch = this.toProcess[this.pos];
            if (ch == '\'') {
                if (this.toProcess[this.pos + 1] == '\'') {
                    ++this.pos;
                }
                else {
                    terminated = true;
                }
            }
            if (ch == '\0') {
                throw new InternalParseException(new SpelParseException(this.expressionString, start, SpelMessage.NON_TERMINATING_QUOTED_STRING, new Object[0]));
            }
        }
        ++this.pos;
        this.tokens.add(new Token(TokenKind.LITERAL_STRING, this.subarray(start, this.pos), start, this.pos));
    }
    
    private void lexDoubleQuotedStringLiteral() {
        final int start = this.pos;
        boolean terminated = false;
        while (!terminated) {
            ++this.pos;
            final char ch = this.toProcess[this.pos];
            if (ch == '\"') {
                if (this.toProcess[this.pos + 1] == '\"') {
                    ++this.pos;
                }
                else {
                    terminated = true;
                }
            }
            if (ch == '\0') {
                throw new InternalParseException(new SpelParseException(this.expressionString, start, SpelMessage.NON_TERMINATING_DOUBLE_QUOTED_STRING, new Object[0]));
            }
        }
        ++this.pos;
        this.tokens.add(new Token(TokenKind.LITERAL_STRING, this.subarray(start, this.pos), start, this.pos));
    }
    
    private void lexNumericLiteral(final boolean firstCharIsZero) {
        boolean isReal = false;
        final int start = this.pos;
        char ch = this.toProcess[this.pos + 1];
        final boolean isHex = ch == 'x' || ch == 'X';
        if (firstCharIsZero && isHex) {
            ++this.pos;
            do {
                ++this.pos;
            } while (this.isHexadecimalDigit(this.toProcess[this.pos]));
            if (this.isChar('L', 'l')) {
                this.pushHexIntToken(this.subarray(start + 2, this.pos), true, start, this.pos);
                ++this.pos;
            }
            else {
                this.pushHexIntToken(this.subarray(start + 2, this.pos), false, start, this.pos);
            }
            return;
        }
        do {
            ++this.pos;
        } while (this.isDigit(this.toProcess[this.pos]));
        ch = this.toProcess[this.pos];
        if (ch == '.') {
            isReal = true;
            final int dotpos = this.pos;
            do {
                ++this.pos;
            } while (this.isDigit(this.toProcess[this.pos]));
            if (this.pos == dotpos + 1) {
                this.pos = dotpos;
                this.pushIntToken(this.subarray(start, this.pos), false, start, this.pos);
                return;
            }
        }
        int endOfNumber = this.pos;
        if (this.isChar('L', 'l')) {
            if (isReal) {
                throw new InternalParseException(new SpelParseException(this.expressionString, start, SpelMessage.REAL_CANNOT_BE_LONG, new Object[0]));
            }
            this.pushIntToken(this.subarray(start, endOfNumber), true, start, endOfNumber);
            ++this.pos;
        }
        else if (this.isExponentChar(this.toProcess[this.pos])) {
            isReal = true;
            ++this.pos;
            final char possibleSign = this.toProcess[this.pos];
            if (this.isSign(possibleSign)) {
                ++this.pos;
            }
            do {
                ++this.pos;
            } while (this.isDigit(this.toProcess[this.pos]));
            boolean isFloat = false;
            if (this.isFloatSuffix(this.toProcess[this.pos])) {
                isFloat = true;
                endOfNumber = ++this.pos;
            }
            else if (this.isDoubleSuffix(this.toProcess[this.pos])) {
                endOfNumber = ++this.pos;
            }
            this.pushRealToken(this.subarray(start, this.pos), isFloat, start, this.pos);
        }
        else {
            ch = this.toProcess[this.pos];
            boolean isFloat2 = false;
            if (this.isFloatSuffix(ch)) {
                isReal = true;
                isFloat2 = true;
                endOfNumber = ++this.pos;
            }
            else if (this.isDoubleSuffix(ch)) {
                isReal = true;
                endOfNumber = ++this.pos;
            }
            if (isReal) {
                this.pushRealToken(this.subarray(start, endOfNumber), isFloat2, start, endOfNumber);
            }
            else {
                this.pushIntToken(this.subarray(start, endOfNumber), false, start, endOfNumber);
            }
        }
    }
    
    private void lexIdentifier() {
        final int start = this.pos;
        do {
            ++this.pos;
        } while (this.isIdentifier(this.toProcess[this.pos]));
        final char[] subarray = this.subarray(start, this.pos);
        if (this.pos - start == 2 || this.pos - start == 3) {
            final String asString = new String(subarray).toUpperCase();
            final int idx = Arrays.binarySearch(Tokenizer.ALTERNATIVE_OPERATOR_NAMES, asString);
            if (idx >= 0) {
                this.pushOneCharOrTwoCharToken(TokenKind.valueOf(asString), start, subarray);
                return;
            }
        }
        this.tokens.add(new Token(TokenKind.IDENTIFIER, subarray, start, this.pos));
    }
    
    private void pushIntToken(final char[] data, final boolean isLong, final int start, final int end) {
        if (isLong) {
            this.tokens.add(new Token(TokenKind.LITERAL_LONG, data, start, end));
        }
        else {
            this.tokens.add(new Token(TokenKind.LITERAL_INT, data, start, end));
        }
    }
    
    private void pushHexIntToken(final char[] data, final boolean isLong, final int start, final int end) {
        if (data.length != 0) {
            if (isLong) {
                this.tokens.add(new Token(TokenKind.LITERAL_HEXLONG, data, start, end));
            }
            else {
                this.tokens.add(new Token(TokenKind.LITERAL_HEXINT, data, start, end));
            }
            return;
        }
        if (isLong) {
            throw new InternalParseException(new SpelParseException(this.expressionString, start, SpelMessage.NOT_A_LONG, new Object[] { this.expressionString.substring(start, end + 1) }));
        }
        throw new InternalParseException(new SpelParseException(this.expressionString, start, SpelMessage.NOT_AN_INTEGER, new Object[] { this.expressionString.substring(start, end) }));
    }
    
    private void pushRealToken(final char[] data, final boolean isFloat, final int start, final int end) {
        if (isFloat) {
            this.tokens.add(new Token(TokenKind.LITERAL_REAL_FLOAT, data, start, end));
        }
        else {
            this.tokens.add(new Token(TokenKind.LITERAL_REAL, data, start, end));
        }
    }
    
    private char[] subarray(final int start, final int end) {
        final char[] result = new char[end - start];
        System.arraycopy(this.toProcess, start, result, 0, end - start);
        return result;
    }
    
    private boolean isTwoCharToken(final TokenKind kind) {
        Assert.isTrue(kind.tokenChars.length == 2);
        Assert.isTrue(this.toProcess[this.pos] == kind.tokenChars[0]);
        return this.toProcess[this.pos + 1] == kind.tokenChars[1];
    }
    
    private void pushCharToken(final TokenKind kind) {
        this.tokens.add(new Token(kind, this.pos, this.pos + 1));
        ++this.pos;
    }
    
    private void pushPairToken(final TokenKind kind) {
        this.tokens.add(new Token(kind, this.pos, this.pos + 2));
        this.pos += 2;
    }
    
    private void pushOneCharOrTwoCharToken(final TokenKind kind, final int pos, final char[] data) {
        this.tokens.add(new Token(kind, data, pos, pos + kind.getLength()));
    }
    
    private boolean isIdentifier(final char ch) {
        return this.isAlphabetic(ch) || this.isDigit(ch) || ch == '_' || ch == '$';
    }
    
    private boolean isChar(final char a, final char b) {
        final char ch = this.toProcess[this.pos];
        return ch == a || ch == b;
    }
    
    private boolean isExponentChar(final char ch) {
        return ch == 'e' || ch == 'E';
    }
    
    private boolean isFloatSuffix(final char ch) {
        return ch == 'f' || ch == 'F';
    }
    
    private boolean isDoubleSuffix(final char ch) {
        return ch == 'd' || ch == 'D';
    }
    
    private boolean isSign(final char ch) {
        return ch == '+' || ch == '-';
    }
    
    private boolean isDigit(final char ch) {
        return ch <= '\u00ff' && (Tokenizer.FLAGS[ch] & 0x1) != 0x0;
    }
    
    private boolean isAlphabetic(final char ch) {
        return ch <= '\u00ff' && (Tokenizer.FLAGS[ch] & 0x4) != 0x0;
    }
    
    private boolean isHexadecimalDigit(final char ch) {
        return ch <= '\u00ff' && (Tokenizer.FLAGS[ch] & 0x2) != 0x0;
    }
    
    static {
        ALTERNATIVE_OPERATOR_NAMES = new String[] { "DIV", "EQ", "GE", "GT", "LE", "LT", "MOD", "NE", "NOT" };
        FLAGS = new byte[256];
        for (int ch = 48; ch <= 57; ++ch) {
            final byte[] flags = Tokenizer.FLAGS;
            final int n = ch;
            flags[n] |= 0x3;
        }
        for (int ch = 65; ch <= 70; ++ch) {
            final byte[] flags2 = Tokenizer.FLAGS;
            final int n2 = ch;
            flags2[n2] |= 0x2;
        }
        for (int ch = 97; ch <= 102; ++ch) {
            final byte[] flags3 = Tokenizer.FLAGS;
            final int n3 = ch;
            flags3[n3] |= 0x2;
        }
        for (int ch = 65; ch <= 90; ++ch) {
            final byte[] flags4 = Tokenizer.FLAGS;
            final int n4 = ch;
            flags4[n4] |= 0x4;
        }
        for (int ch = 97; ch <= 122; ++ch) {
            final byte[] flags5 = Tokenizer.FLAGS;
            final int n5 = ch;
            flags5[n5] |= 0x4;
        }
    }
}
