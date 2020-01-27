// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.standard;

class Token
{
    TokenKind kind;
    String data;
    int startpos;
    int endpos;
    
    Token(final TokenKind tokenKind, final int startpos, final int endpos) {
        this.kind = tokenKind;
        this.startpos = startpos;
        this.endpos = endpos;
    }
    
    Token(final TokenKind tokenKind, final char[] tokenData, final int pos, final int endpos) {
        this(tokenKind, pos, endpos);
        this.data = new String(tokenData);
    }
    
    public TokenKind getKind() {
        return this.kind;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append("[").append(this.kind.toString());
        if (this.kind.hasPayload()) {
            s.append(":").append(this.data);
        }
        s.append("]");
        s.append("(").append(this.startpos).append(",").append(this.endpos).append(")");
        return s.toString();
    }
    
    public boolean isIdentifier() {
        return this.kind == TokenKind.IDENTIFIER;
    }
    
    public boolean isNumericRelationalOperator() {
        return this.kind == TokenKind.GT || this.kind == TokenKind.GE || this.kind == TokenKind.LT || this.kind == TokenKind.LE || this.kind == TokenKind.EQ || this.kind == TokenKind.NE;
    }
    
    public String stringValue() {
        return this.data;
    }
    
    public Token asInstanceOfToken() {
        return new Token(TokenKind.INSTANCEOF, this.startpos, this.endpos);
    }
    
    public Token asMatchesToken() {
        return new Token(TokenKind.MATCHES, this.startpos, this.endpos);
    }
    
    public Token asBetweenToken() {
        return new Token(TokenKind.BETWEEN, this.startpos, this.endpos);
    }
}
