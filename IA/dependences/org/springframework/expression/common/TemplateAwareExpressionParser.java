// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.common;

import java.util.Stack;
import java.util.List;
import java.util.LinkedList;
import org.springframework.expression.ParseException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.ExpressionParser;

public abstract class TemplateAwareExpressionParser implements ExpressionParser
{
    private static final ParserContext NON_TEMPLATE_PARSER_CONTEXT;
    
    @Override
    public Expression parseExpression(final String expressionString) throws ParseException {
        return this.parseExpression(expressionString, TemplateAwareExpressionParser.NON_TEMPLATE_PARSER_CONTEXT);
    }
    
    @Override
    public Expression parseExpression(final String expressionString, ParserContext context) throws ParseException {
        if (context == null) {
            context = TemplateAwareExpressionParser.NON_TEMPLATE_PARSER_CONTEXT;
        }
        if (context.isTemplate()) {
            return this.parseTemplate(expressionString, context);
        }
        return this.doParseExpression(expressionString, context);
    }
    
    private Expression parseTemplate(final String expressionString, final ParserContext context) throws ParseException {
        if (expressionString.length() == 0) {
            return new LiteralExpression("");
        }
        final Expression[] expressions = this.parseExpressions(expressionString, context);
        if (expressions.length == 1) {
            return expressions[0];
        }
        return new CompositeStringExpression(expressionString, expressions);
    }
    
    private Expression[] parseExpressions(final String expressionString, final ParserContext context) throws ParseException {
        final List<Expression> expressions = new LinkedList<Expression>();
        final String prefix = context.getExpressionPrefix();
        final String suffix = context.getExpressionSuffix();
        int startIdx = 0;
        while (startIdx < expressionString.length()) {
            final int prefixIndex = expressionString.indexOf(prefix, startIdx);
            if (prefixIndex >= startIdx) {
                if (prefixIndex > startIdx) {
                    expressions.add(this.createLiteralExpression(context, expressionString.substring(startIdx, prefixIndex)));
                }
                final int afterPrefixIndex = prefixIndex + prefix.length();
                final int suffixIndex = this.skipToCorrectEndSuffix(prefix, suffix, expressionString, afterPrefixIndex);
                if (suffixIndex == -1) {
                    throw new ParseException(expressionString, prefixIndex, "No ending suffix '" + suffix + "' for expression starting at character " + prefixIndex + ": " + expressionString.substring(prefixIndex));
                }
                if (suffixIndex == afterPrefixIndex) {
                    throw new ParseException(expressionString, prefixIndex, "No expression defined within delimiter '" + prefix + suffix + "' at character " + prefixIndex);
                }
                String expr = expressionString.substring(prefixIndex + prefix.length(), suffixIndex);
                expr = expr.trim();
                if (expr.length() == 0) {
                    throw new ParseException(expressionString, prefixIndex, "No expression defined within delimiter '" + prefix + suffix + "' at character " + prefixIndex);
                }
                expressions.add(this.doParseExpression(expr, context));
                startIdx = suffixIndex + suffix.length();
            }
            else {
                expressions.add(this.createLiteralExpression(context, expressionString.substring(startIdx)));
                startIdx = expressionString.length();
            }
        }
        return expressions.toArray(new Expression[expressions.size()]);
    }
    
    private Expression createLiteralExpression(final ParserContext context, final String text) {
        return new LiteralExpression(text);
    }
    
    private boolean isSuffixHere(final String expressionString, int pos, final String suffix) {
        int suffixPosition = 0;
        for (int i = 0; i < suffix.length() && pos < expressionString.length(); ++i) {
            if (expressionString.charAt(pos++) != suffix.charAt(suffixPosition++)) {
                return false;
            }
        }
        return suffixPosition == suffix.length();
    }
    
    private int skipToCorrectEndSuffix(final String prefix, final String suffix, final String expressionString, final int afterPrefixIndex) throws ParseException {
        int pos = afterPrefixIndex;
        final int maxlen = expressionString.length();
        final int nextSuffix = expressionString.indexOf(suffix, afterPrefixIndex);
        if (nextSuffix == -1) {
            return -1;
        }
        Stack<Bracket> stack;
        for (stack = new Stack<Bracket>(); pos < maxlen && (!this.isSuffixHere(expressionString, pos, suffix) || !stack.isEmpty()); ++pos) {
            final char ch = expressionString.charAt(pos);
            switch (ch) {
                case '(':
                case '[':
                case '{': {
                    stack.push(new Bracket(ch, pos));
                    break;
                }
                case ')':
                case ']':
                case '}': {
                    if (stack.isEmpty()) {
                        throw new ParseException(expressionString, pos, "Found closing '" + ch + "' at position " + pos + " without an opening '" + Bracket.theOpenBracketFor(ch) + "'");
                    }
                    final Bracket p = stack.pop();
                    if (!p.compatibleWithCloseBracket(ch)) {
                        throw new ParseException(expressionString, pos, "Found closing '" + ch + "' at position " + pos + " but most recent opening is '" + p.bracket + "' at position " + p.pos);
                    }
                    break;
                }
                case '\"':
                case '\'': {
                    final int endLiteral = expressionString.indexOf(ch, pos + 1);
                    if (endLiteral == -1) {
                        throw new ParseException(expressionString, pos, "Found non terminating string literal starting at position " + pos);
                    }
                    pos = endLiteral;
                    break;
                }
            }
        }
        if (!stack.isEmpty()) {
            final Bracket p2 = stack.pop();
            throw new ParseException(expressionString, p2.pos, "Missing closing '" + Bracket.theCloseBracketFor(p2.bracket) + "' for '" + p2.bracket + "' at position " + p2.pos);
        }
        if (!this.isSuffixHere(expressionString, pos, suffix)) {
            return -1;
        }
        return pos;
    }
    
    protected abstract Expression doParseExpression(final String p0, final ParserContext p1) throws ParseException;
    
    static {
        NON_TEMPLATE_PARSER_CONTEXT = new ParserContext() {
            @Override
            public String getExpressionPrefix() {
                return null;
            }
            
            @Override
            public String getExpressionSuffix() {
                return null;
            }
            
            @Override
            public boolean isTemplate() {
                return false;
            }
        };
    }
    
    private static class Bracket
    {
        char bracket;
        int pos;
        
        Bracket(final char bracket, final int pos) {
            this.bracket = bracket;
            this.pos = pos;
        }
        
        boolean compatibleWithCloseBracket(final char closeBracket) {
            if (this.bracket == '{') {
                return closeBracket == '}';
            }
            if (this.bracket == '[') {
                return closeBracket == ']';
            }
            return closeBracket == ')';
        }
        
        static char theOpenBracketFor(final char closeBracket) {
            if (closeBracket == '}') {
                return '{';
            }
            if (closeBracket == ']') {
                return '[';
            }
            return '(';
        }
        
        static char theCloseBracketFor(final char openBracket) {
            if (openBracket == '{') {
                return '}';
            }
            if (openBracket == '[') {
                return ']';
            }
            return ')';
        }
    }
}
