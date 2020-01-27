// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.standard;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.expression.spel.ast.ConstructorReference;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.util.StringUtils;
import org.springframework.expression.spel.ast.QualifiedIdentifier;
import org.springframework.expression.spel.ast.Identifier;
import java.util.LinkedList;
import org.springframework.expression.spel.ast.Selection;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.expression.spel.ast.InlineList;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.FunctionReference;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.ast.CompoundExpression;
import java.util.ArrayList;
import org.springframework.expression.spel.ast.OperatorNot;
import org.springframework.expression.spel.ast.OpDec;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.expression.spel.ast.OperatorPower;
import org.springframework.expression.spel.ast.OpModulus;
import org.springframework.expression.spel.ast.OpDivide;
import org.springframework.expression.spel.ast.OpMultiply;
import org.springframework.expression.spel.ast.OpMinus;
import org.springframework.expression.spel.ast.OpPlus;
import org.springframework.expression.spel.ast.OperatorBetween;
import org.springframework.expression.spel.ast.OperatorMatches;
import org.springframework.expression.spel.ast.OperatorInstanceof;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.expression.spel.ast.OpEQ;
import org.springframework.expression.spel.ast.OpGE;
import org.springframework.expression.spel.ast.OpLE;
import org.springframework.expression.spel.ast.OpLT;
import org.springframework.expression.spel.ast.OpGT;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.expression.spel.ast.Ternary;
import org.springframework.expression.spel.ast.Elvis;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.util.Assert;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import java.util.Stack;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.expression.common.TemplateAwareExpressionParser;

class InternalSpelExpressionParser extends TemplateAwareExpressionParser
{
    private static final Pattern VALID_QUALIFIED_ID_PATTERN;
    private String expressionString;
    private List<Token> tokenStream;
    private int tokenStreamLength;
    private int tokenStreamPointer;
    private final Stack<SpelNodeImpl> constructedNodes;
    private final SpelParserConfiguration configuration;
    
    public InternalSpelExpressionParser(final SpelParserConfiguration configuration) {
        this.constructedNodes = new Stack<SpelNodeImpl>();
        this.configuration = configuration;
    }
    
    @Override
    protected SpelExpression doParseExpression(final String expressionString, final ParserContext context) throws ParseException {
        try {
            this.expressionString = expressionString;
            final Tokenizer tokenizer = new Tokenizer(expressionString);
            tokenizer.process();
            this.tokenStream = tokenizer.getTokens();
            this.tokenStreamLength = this.tokenStream.size();
            this.tokenStreamPointer = 0;
            this.constructedNodes.clear();
            final SpelNodeImpl ast = this.eatExpression();
            if (this.moreTokens()) {
                throw new SpelParseException(this.peekToken().startpos, SpelMessage.MORE_INPUT, new Object[] { this.toString(this.nextToken()) });
            }
            Assert.isTrue(this.constructedNodes.isEmpty());
            return new SpelExpression(expressionString, ast, this.configuration);
        }
        catch (InternalParseException ipe) {
            throw ipe.getCause();
        }
    }
    
    private SpelNodeImpl eatExpression() {
        SpelNodeImpl expr = this.eatLogicalOrExpression();
        if (this.moreTokens()) {
            final Token t = this.peekToken();
            if (t.kind == TokenKind.ASSIGN) {
                if (expr == null) {
                    expr = new NullLiteral(this.toPos(t.startpos - 1, t.endpos - 1));
                }
                this.nextToken();
                final SpelNodeImpl assignedValue = this.eatLogicalOrExpression();
                return new Assign(this.toPos(t), new SpelNodeImpl[] { expr, assignedValue });
            }
            if (t.kind == TokenKind.ELVIS) {
                if (expr == null) {
                    expr = new NullLiteral(this.toPos(t.startpos - 1, t.endpos - 2));
                }
                this.nextToken();
                SpelNodeImpl valueIfNull = this.eatExpression();
                if (valueIfNull == null) {
                    valueIfNull = new NullLiteral(this.toPos(t.startpos + 1, t.endpos + 1));
                }
                return new Elvis(this.toPos(t), new SpelNodeImpl[] { expr, valueIfNull });
            }
            if (t.kind == TokenKind.QMARK) {
                if (expr == null) {
                    expr = new NullLiteral(this.toPos(t.startpos - 1, t.endpos - 1));
                }
                this.nextToken();
                final SpelNodeImpl ifTrueExprValue = this.eatExpression();
                this.eatToken(TokenKind.COLON);
                final SpelNodeImpl ifFalseExprValue = this.eatExpression();
                return new Ternary(this.toPos(t), new SpelNodeImpl[] { expr, ifTrueExprValue, ifFalseExprValue });
            }
        }
        return expr;
    }
    
    private SpelNodeImpl eatLogicalOrExpression() {
        SpelNodeImpl expr = this.eatLogicalAndExpression();
        while (this.peekIdentifierToken("or") || this.peekToken(TokenKind.SYMBOLIC_OR)) {
            final Token t = this.nextToken();
            final SpelNodeImpl rhExpr = this.eatLogicalAndExpression();
            this.checkOperands(t, expr, rhExpr);
            expr = new OpOr(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
        }
        return expr;
    }
    
    private SpelNodeImpl eatLogicalAndExpression() {
        SpelNodeImpl expr = this.eatRelationalExpression();
        while (this.peekIdentifierToken("and") || this.peekToken(TokenKind.SYMBOLIC_AND)) {
            final Token t = this.nextToken();
            final SpelNodeImpl rhExpr = this.eatRelationalExpression();
            this.checkOperands(t, expr, rhExpr);
            expr = new OpAnd(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
        }
        return expr;
    }
    
    private SpelNodeImpl eatRelationalExpression() {
        final SpelNodeImpl expr = this.eatSumExpression();
        final Token relationalOperatorToken = this.maybeEatRelationalOperator();
        if (relationalOperatorToken == null) {
            return expr;
        }
        final Token t = this.nextToken();
        final SpelNodeImpl rhExpr = this.eatSumExpression();
        this.checkOperands(t, expr, rhExpr);
        final TokenKind tk = relationalOperatorToken.kind;
        if (relationalOperatorToken.isNumericRelationalOperator()) {
            final int pos = this.toPos(t);
            if (tk == TokenKind.GT) {
                return new OpGT(pos, new SpelNodeImpl[] { expr, rhExpr });
            }
            if (tk == TokenKind.LT) {
                return new OpLT(pos, new SpelNodeImpl[] { expr, rhExpr });
            }
            if (tk == TokenKind.LE) {
                return new OpLE(pos, new SpelNodeImpl[] { expr, rhExpr });
            }
            if (tk == TokenKind.GE) {
                return new OpGE(pos, new SpelNodeImpl[] { expr, rhExpr });
            }
            if (tk == TokenKind.EQ) {
                return new OpEQ(pos, new SpelNodeImpl[] { expr, rhExpr });
            }
            Assert.isTrue(tk == TokenKind.NE);
            return new OpNE(pos, new SpelNodeImpl[] { expr, rhExpr });
        }
        else {
            if (tk == TokenKind.INSTANCEOF) {
                return new OperatorInstanceof(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
            }
            if (tk == TokenKind.MATCHES) {
                return new OperatorMatches(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
            }
            Assert.isTrue(tk == TokenKind.BETWEEN);
            return new OperatorBetween(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
        }
    }
    
    private SpelNodeImpl eatSumExpression() {
        SpelNodeImpl expr = this.eatProductExpression();
        while (this.peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.INC)) {
            final Token t = this.nextToken();
            final SpelNodeImpl rhExpr = this.eatProductExpression();
            this.checkRightOperand(t, rhExpr);
            if (t.kind == TokenKind.PLUS) {
                expr = new OpPlus(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
            }
            else {
                if (t.kind != TokenKind.MINUS) {
                    continue;
                }
                expr = new OpMinus(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
            }
        }
        return expr;
    }
    
    private SpelNodeImpl eatProductExpression() {
        SpelNodeImpl expr = this.eatPowerIncDecExpression();
        while (this.peekToken(TokenKind.STAR, TokenKind.DIV, TokenKind.MOD)) {
            final Token t = this.nextToken();
            final SpelNodeImpl rhExpr = this.eatPowerIncDecExpression();
            this.checkOperands(t, expr, rhExpr);
            if (t.kind == TokenKind.STAR) {
                expr = new OpMultiply(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
            }
            else if (t.kind == TokenKind.DIV) {
                expr = new OpDivide(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
            }
            else {
                Assert.isTrue(t.kind == TokenKind.MOD);
                expr = new OpModulus(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
            }
        }
        return expr;
    }
    
    private SpelNodeImpl eatPowerIncDecExpression() {
        final SpelNodeImpl expr = this.eatUnaryExpression();
        if (this.peekToken(TokenKind.POWER)) {
            final Token t = this.nextToken();
            final SpelNodeImpl rhExpr = this.eatUnaryExpression();
            this.checkRightOperand(t, rhExpr);
            return new OperatorPower(this.toPos(t), new SpelNodeImpl[] { expr, rhExpr });
        }
        if (expr == null || !this.peekToken(TokenKind.INC, TokenKind.DEC)) {
            return expr;
        }
        final Token t = this.nextToken();
        if (t.getKind() == TokenKind.INC) {
            return new OpInc(this.toPos(t), true, new SpelNodeImpl[] { expr });
        }
        return new OpDec(this.toPos(t), true, new SpelNodeImpl[] { expr });
    }
    
    private SpelNodeImpl eatUnaryExpression() {
        if (this.peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.NOT)) {
            final Token t = this.nextToken();
            final SpelNodeImpl expr = this.eatUnaryExpression();
            if (t.kind == TokenKind.NOT) {
                return new OperatorNot(this.toPos(t), expr);
            }
            if (t.kind == TokenKind.PLUS) {
                return new OpPlus(this.toPos(t), new SpelNodeImpl[] { expr });
            }
            Assert.isTrue(t.kind == TokenKind.MINUS);
            return new OpMinus(this.toPos(t), new SpelNodeImpl[] { expr });
        }
        else {
            if (!this.peekToken(TokenKind.INC, TokenKind.DEC)) {
                return this.eatPrimaryExpression();
            }
            final Token t = this.nextToken();
            final SpelNodeImpl expr = this.eatUnaryExpression();
            if (t.getKind() == TokenKind.INC) {
                return new OpInc(this.toPos(t), false, new SpelNodeImpl[] { expr });
            }
            return new OpDec(this.toPos(t), false, new SpelNodeImpl[] { expr });
        }
    }
    
    private SpelNodeImpl eatPrimaryExpression() {
        final List<SpelNodeImpl> nodes = new ArrayList<SpelNodeImpl>();
        final SpelNodeImpl start = this.eatStartNode();
        nodes.add(start);
        while (this.maybeEatNode()) {
            nodes.add(this.pop());
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        return new CompoundExpression(this.toPos(start.getStartPosition(), nodes.get(nodes.size() - 1).getEndPosition()), (SpelNodeImpl[])nodes.toArray(new SpelNodeImpl[nodes.size()]));
    }
    
    private boolean maybeEatNode() {
        SpelNodeImpl expr = null;
        if (this.peekToken(TokenKind.DOT, TokenKind.SAFE_NAVI)) {
            expr = this.eatDottedNode();
        }
        else {
            expr = this.maybeEatNonDottedNode();
        }
        if (expr == null) {
            return false;
        }
        this.push(expr);
        return true;
    }
    
    private SpelNodeImpl maybeEatNonDottedNode() {
        if (this.peekToken(TokenKind.LSQUARE) && this.maybeEatIndexer()) {
            return this.pop();
        }
        return null;
    }
    
    private SpelNodeImpl eatDottedNode() {
        final Token t = this.nextToken();
        final boolean nullSafeNavigation = t.kind == TokenKind.SAFE_NAVI;
        if (this.maybeEatMethodOrProperty(nullSafeNavigation) || this.maybeEatFunctionOrVar() || this.maybeEatProjection(nullSafeNavigation) || this.maybeEatSelection(nullSafeNavigation)) {
            return this.pop();
        }
        if (this.peekToken() == null) {
            this.raiseInternalException(t.startpos, SpelMessage.OOD, new Object[0]);
        }
        else {
            this.raiseInternalException(t.startpos, SpelMessage.UNEXPECTED_DATA_AFTER_DOT, this.toString(this.peekToken()));
        }
        return null;
    }
    
    private boolean maybeEatFunctionOrVar() {
        if (!this.peekToken(TokenKind.HASH)) {
            return false;
        }
        final Token t = this.nextToken();
        final Token functionOrVariableName = this.eatToken(TokenKind.IDENTIFIER);
        final SpelNodeImpl[] args = this.maybeEatMethodArgs();
        if (args == null) {
            this.push(new VariableReference(functionOrVariableName.data, this.toPos(t.startpos, functionOrVariableName.endpos)));
            return true;
        }
        this.push(new FunctionReference(functionOrVariableName.data, this.toPos(t.startpos, functionOrVariableName.endpos), args));
        return true;
    }
    
    private SpelNodeImpl[] maybeEatMethodArgs() {
        if (!this.peekToken(TokenKind.LPAREN)) {
            return null;
        }
        final List<SpelNodeImpl> args = new ArrayList<SpelNodeImpl>();
        this.consumeArguments(args);
        this.eatToken(TokenKind.RPAREN);
        return args.toArray(new SpelNodeImpl[args.size()]);
    }
    
    private void eatConstructorArgs(final List<SpelNodeImpl> accumulatedArguments) {
        if (!this.peekToken(TokenKind.LPAREN)) {
            throw new InternalParseException(new SpelParseException(this.expressionString, this.positionOf(this.peekToken()), SpelMessage.MISSING_CONSTRUCTOR_ARGS, new Object[0]));
        }
        this.consumeArguments(accumulatedArguments);
        this.eatToken(TokenKind.RPAREN);
    }
    
    private void consumeArguments(final List<SpelNodeImpl> accumulatedArguments) {
        final int pos = this.peekToken().startpos;
        Token next = null;
        do {
            this.nextToken();
            final Token t = this.peekToken();
            if (t == null) {
                this.raiseInternalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
            }
            if (t.kind != TokenKind.RPAREN) {
                accumulatedArguments.add(this.eatExpression());
            }
            next = this.peekToken();
        } while (next != null && next.kind == TokenKind.COMMA);
        if (next == null) {
            this.raiseInternalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
        }
    }
    
    private int positionOf(final Token t) {
        if (t == null) {
            return this.expressionString.length();
        }
        return t.startpos;
    }
    
    private SpelNodeImpl eatStartNode() {
        if (this.maybeEatLiteral()) {
            return this.pop();
        }
        if (this.maybeEatParenExpression()) {
            return this.pop();
        }
        if (this.maybeEatTypeReference() || this.maybeEatNullReference() || this.maybeEatConstructorReference() || this.maybeEatMethodOrProperty(false) || this.maybeEatFunctionOrVar()) {
            return this.pop();
        }
        if (this.maybeEatBeanReference()) {
            return this.pop();
        }
        if (this.maybeEatProjection(false) || this.maybeEatSelection(false) || this.maybeEatIndexer()) {
            return this.pop();
        }
        if (this.maybeEatInlineList()) {
            return this.pop();
        }
        return null;
    }
    
    private boolean maybeEatBeanReference() {
        if (this.peekToken(TokenKind.BEAN_REF)) {
            final Token beanRefToken = this.nextToken();
            Token beanNameToken = null;
            String beanname = null;
            if (this.peekToken(TokenKind.IDENTIFIER)) {
                beanNameToken = this.eatToken(TokenKind.IDENTIFIER);
                beanname = beanNameToken.data;
            }
            else if (this.peekToken(TokenKind.LITERAL_STRING)) {
                beanNameToken = this.eatToken(TokenKind.LITERAL_STRING);
                beanname = beanNameToken.stringValue();
                beanname = beanname.substring(1, beanname.length() - 1);
            }
            else {
                this.raiseInternalException(beanRefToken.startpos, SpelMessage.INVALID_BEAN_REFERENCE, new Object[0]);
            }
            final BeanReference beanReference = new BeanReference(this.toPos(beanNameToken), beanname);
            this.constructedNodes.push(beanReference);
            return true;
        }
        return false;
    }
    
    private boolean maybeEatTypeReference() {
        if (!this.peekToken(TokenKind.IDENTIFIER)) {
            return false;
        }
        final Token typeName = this.peekToken();
        if (!typeName.stringValue().equals("T")) {
            return false;
        }
        this.nextToken();
        this.eatToken(TokenKind.LPAREN);
        final SpelNodeImpl node = this.eatPossiblyQualifiedId();
        int dims = 0;
        while (this.peekToken(TokenKind.LSQUARE, true)) {
            this.eatToken(TokenKind.RSQUARE);
            ++dims;
        }
        this.eatToken(TokenKind.RPAREN);
        this.constructedNodes.push(new TypeReference(this.toPos(typeName), node, dims));
        return true;
    }
    
    private boolean maybeEatNullReference() {
        if (!this.peekToken(TokenKind.IDENTIFIER)) {
            return false;
        }
        final Token nullToken = this.peekToken();
        if (!nullToken.stringValue().equalsIgnoreCase("null")) {
            return false;
        }
        this.nextToken();
        this.constructedNodes.push(new NullLiteral(this.toPos(nullToken)));
        return true;
    }
    
    private boolean maybeEatProjection(final boolean nullSafeNavigation) {
        final Token t = this.peekToken();
        if (!this.peekToken(TokenKind.PROJECT, true)) {
            return false;
        }
        final SpelNodeImpl expr = this.eatExpression();
        this.eatToken(TokenKind.RSQUARE);
        this.constructedNodes.push(new Projection(nullSafeNavigation, this.toPos(t), expr));
        return true;
    }
    
    private boolean maybeEatInlineList() {
        final Token t = this.peekToken();
        if (!this.peekToken(TokenKind.LCURLY, true)) {
            return false;
        }
        SpelNodeImpl expr = null;
        Token closingCurly = this.peekToken();
        if (this.peekToken(TokenKind.RCURLY, true)) {
            expr = new InlineList(this.toPos(t.startpos, closingCurly.endpos), new SpelNodeImpl[0]);
        }
        else {
            final List<SpelNodeImpl> listElements = new ArrayList<SpelNodeImpl>();
            do {
                listElements.add(this.eatExpression());
            } while (this.peekToken(TokenKind.COMMA, true));
            closingCurly = this.eatToken(TokenKind.RCURLY);
            expr = new InlineList(this.toPos(t.startpos, closingCurly.endpos), (SpelNodeImpl[])listElements.toArray(new SpelNodeImpl[listElements.size()]));
        }
        this.constructedNodes.push(expr);
        return true;
    }
    
    private boolean maybeEatIndexer() {
        final Token t = this.peekToken();
        if (!this.peekToken(TokenKind.LSQUARE, true)) {
            return false;
        }
        final SpelNodeImpl expr = this.eatExpression();
        this.eatToken(TokenKind.RSQUARE);
        this.constructedNodes.push(new Indexer(this.toPos(t), expr));
        return true;
    }
    
    private boolean maybeEatSelection(final boolean nullSafeNavigation) {
        final Token t = this.peekToken();
        if (!this.peekSelectToken()) {
            return false;
        }
        this.nextToken();
        final SpelNodeImpl expr = this.eatExpression();
        if (expr == null) {
            this.raiseInternalException(this.toPos(t), SpelMessage.MISSING_SELECTION_EXPRESSION, new Object[0]);
        }
        this.eatToken(TokenKind.RSQUARE);
        if (t.kind == TokenKind.SELECT_FIRST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 1, this.toPos(t), expr));
        }
        else if (t.kind == TokenKind.SELECT_LAST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 2, this.toPos(t), expr));
        }
        else {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 0, this.toPos(t), expr));
        }
        return true;
    }
    
    private SpelNodeImpl eatPossiblyQualifiedId() {
        final LinkedList<SpelNodeImpl> qualifiedIdPieces = new LinkedList<SpelNodeImpl>();
        Token node;
        for (node = this.peekToken(); this.isValidQualifiedId(node); node = this.peekToken()) {
            this.nextToken();
            if (node.kind != TokenKind.DOT) {
                qualifiedIdPieces.add(new Identifier(node.stringValue(), this.toPos(node)));
            }
        }
        if (qualifiedIdPieces.isEmpty()) {
            if (node == null) {
                this.raiseInternalException(this.expressionString.length(), SpelMessage.OOD, new Object[0]);
            }
            this.raiseInternalException(node.startpos, SpelMessage.NOT_EXPECTED_TOKEN, "qualified ID", node.getKind().toString().toLowerCase());
        }
        final int pos = this.toPos(qualifiedIdPieces.getFirst().getStartPosition(), qualifiedIdPieces.getLast().getEndPosition());
        return new QualifiedIdentifier(pos, (SpelNodeImpl[])qualifiedIdPieces.toArray(new SpelNodeImpl[qualifiedIdPieces.size()]));
    }
    
    private boolean isValidQualifiedId(final Token node) {
        if (node == null || node.kind == TokenKind.LITERAL_STRING) {
            return false;
        }
        if (node.kind == TokenKind.DOT || node.kind == TokenKind.IDENTIFIER) {
            return true;
        }
        final String value = node.stringValue();
        return StringUtils.hasLength(value) && InternalSpelExpressionParser.VALID_QUALIFIED_ID_PATTERN.matcher(value).matches();
    }
    
    private boolean maybeEatMethodOrProperty(final boolean nullSafeNavigation) {
        if (!this.peekToken(TokenKind.IDENTIFIER)) {
            return false;
        }
        final Token methodOrPropertyName = this.nextToken();
        final SpelNodeImpl[] args = this.maybeEatMethodArgs();
        if (args == null) {
            this.push(new PropertyOrFieldReference(nullSafeNavigation, methodOrPropertyName.data, this.toPos(methodOrPropertyName)));
            return true;
        }
        this.push(new MethodReference(nullSafeNavigation, methodOrPropertyName.data, this.toPos(methodOrPropertyName), args));
        return true;
    }
    
    private boolean maybeEatConstructorReference() {
        if (this.peekIdentifierToken("new")) {
            final Token newToken = this.nextToken();
            final SpelNodeImpl possiblyQualifiedConstructorName = this.eatPossiblyQualifiedId();
            final List<SpelNodeImpl> nodes = new ArrayList<SpelNodeImpl>();
            nodes.add(possiblyQualifiedConstructorName);
            if (this.peekToken(TokenKind.LSQUARE)) {
                final List<SpelNodeImpl> dimensions = new ArrayList<SpelNodeImpl>();
                while (this.peekToken(TokenKind.LSQUARE, true)) {
                    if (!this.peekToken(TokenKind.RSQUARE)) {
                        dimensions.add(this.eatExpression());
                    }
                    else {
                        dimensions.add(null);
                    }
                    this.eatToken(TokenKind.RSQUARE);
                }
                if (this.maybeEatInlineList()) {
                    nodes.add(this.pop());
                }
                this.push(new ConstructorReference(this.toPos(newToken), dimensions.toArray(new SpelNodeImpl[dimensions.size()]), (SpelNodeImpl[])nodes.toArray(new SpelNodeImpl[nodes.size()])));
            }
            else {
                this.eatConstructorArgs(nodes);
                this.push(new ConstructorReference(this.toPos(newToken), (SpelNodeImpl[])nodes.toArray(new SpelNodeImpl[nodes.size()])));
            }
            return true;
        }
        return false;
    }
    
    private void push(final SpelNodeImpl newNode) {
        this.constructedNodes.push(newNode);
    }
    
    private SpelNodeImpl pop() {
        return this.constructedNodes.pop();
    }
    
    private boolean maybeEatLiteral() {
        final Token t = this.peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == TokenKind.LITERAL_INT) {
            this.push(Literal.getIntLiteral(t.data, this.toPos(t), 10));
        }
        else if (t.kind == TokenKind.LITERAL_LONG) {
            this.push(Literal.getLongLiteral(t.data, this.toPos(t), 10));
        }
        else if (t.kind == TokenKind.LITERAL_HEXINT) {
            this.push(Literal.getIntLiteral(t.data, this.toPos(t), 16));
        }
        else if (t.kind == TokenKind.LITERAL_HEXLONG) {
            this.push(Literal.getLongLiteral(t.data, this.toPos(t), 16));
        }
        else if (t.kind == TokenKind.LITERAL_REAL) {
            this.push(Literal.getRealLiteral(t.data, this.toPos(t), false));
        }
        else if (t.kind == TokenKind.LITERAL_REAL_FLOAT) {
            this.push(Literal.getRealLiteral(t.data, this.toPos(t), true));
        }
        else if (this.peekIdentifierToken("true")) {
            this.push(new BooleanLiteral(t.data, this.toPos(t), true));
        }
        else if (this.peekIdentifierToken("false")) {
            this.push(new BooleanLiteral(t.data, this.toPos(t), false));
        }
        else {
            if (t.kind != TokenKind.LITERAL_STRING) {
                return false;
            }
            this.push(new StringLiteral(t.data, this.toPos(t), t.data));
        }
        this.nextToken();
        return true;
    }
    
    private boolean maybeEatParenExpression() {
        if (this.peekToken(TokenKind.LPAREN)) {
            this.nextToken();
            final SpelNodeImpl expr = this.eatExpression();
            this.eatToken(TokenKind.RPAREN);
            this.push(expr);
            return true;
        }
        return false;
    }
    
    private Token maybeEatRelationalOperator() {
        final Token t = this.peekToken();
        if (t == null) {
            return null;
        }
        if (t.isNumericRelationalOperator()) {
            return t;
        }
        if (t.isIdentifier()) {
            final String idString = t.stringValue();
            if (idString.equalsIgnoreCase("instanceof")) {
                return t.asInstanceOfToken();
            }
            if (idString.equalsIgnoreCase("matches")) {
                return t.asMatchesToken();
            }
            if (idString.equalsIgnoreCase("between")) {
                return t.asBetweenToken();
            }
        }
        return null;
    }
    
    private Token eatToken(final TokenKind expectedKind) {
        final Token t = this.nextToken();
        if (t == null) {
            this.raiseInternalException(this.expressionString.length(), SpelMessage.OOD, new Object[0]);
        }
        if (t.kind != expectedKind) {
            this.raiseInternalException(t.startpos, SpelMessage.NOT_EXPECTED_TOKEN, expectedKind.toString().toLowerCase(), t.getKind().toString().toLowerCase());
        }
        return t;
    }
    
    private boolean peekToken(final TokenKind desiredTokenKind) {
        return this.peekToken(desiredTokenKind, false);
    }
    
    private boolean peekToken(final TokenKind desiredTokenKind, final boolean consumeIfMatched) {
        if (!this.moreTokens()) {
            return false;
        }
        final Token t = this.peekToken();
        if (t.kind == desiredTokenKind) {
            if (consumeIfMatched) {
                ++this.tokenStreamPointer;
            }
            return true;
        }
        return desiredTokenKind == TokenKind.IDENTIFIER && t.kind.ordinal() >= TokenKind.DIV.ordinal() && t.kind.ordinal() <= TokenKind.NOT.ordinal() && t.data != null;
    }
    
    private boolean peekToken(final TokenKind possible1, final TokenKind possible2) {
        if (!this.moreTokens()) {
            return false;
        }
        final Token t = this.peekToken();
        return t.kind == possible1 || t.kind == possible2;
    }
    
    private boolean peekToken(final TokenKind possible1, final TokenKind possible2, final TokenKind possible3) {
        if (!this.moreTokens()) {
            return false;
        }
        final Token t = this.peekToken();
        return t.kind == possible1 || t.kind == possible2 || t.kind == possible3;
    }
    
    private boolean peekIdentifierToken(final String identifierString) {
        if (!this.moreTokens()) {
            return false;
        }
        final Token t = this.peekToken();
        return t.kind == TokenKind.IDENTIFIER && t.stringValue().equalsIgnoreCase(identifierString);
    }
    
    private boolean peekSelectToken() {
        if (!this.moreTokens()) {
            return false;
        }
        final Token t = this.peekToken();
        return t.kind == TokenKind.SELECT || t.kind == TokenKind.SELECT_FIRST || t.kind == TokenKind.SELECT_LAST;
    }
    
    private boolean moreTokens() {
        return this.tokenStreamPointer < this.tokenStream.size();
    }
    
    private Token nextToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.tokenStream.get(this.tokenStreamPointer++);
    }
    
    private Token peekToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.tokenStream.get(this.tokenStreamPointer);
    }
    
    private void raiseInternalException(final int pos, final SpelMessage message, final Object... inserts) {
        throw new InternalParseException(new SpelParseException(this.expressionString, pos, message, inserts));
    }
    
    public String toString(final Token t) {
        if (t.getKind().hasPayload()) {
            return t.stringValue();
        }
        return t.kind.toString().toLowerCase();
    }
    
    private void checkOperands(final Token token, final SpelNodeImpl left, final SpelNodeImpl right) {
        this.checkLeftOperand(token, left);
        this.checkRightOperand(token, right);
    }
    
    private void checkLeftOperand(final Token token, final SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            this.raiseInternalException(token.startpos, SpelMessage.LEFT_OPERAND_PROBLEM, new Object[0]);
        }
    }
    
    private void checkRightOperand(final Token token, final SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            this.raiseInternalException(token.startpos, SpelMessage.RIGHT_OPERAND_PROBLEM, new Object[0]);
        }
    }
    
    private int toPos(final Token t) {
        return (t.startpos << 16) + t.endpos;
    }
    
    private int toPos(final int start, final int end) {
        return (start << 16) + end;
    }
    
    static {
        VALID_QUALIFIED_ID_PATTERN = Pattern.compile("[\\p{L}\\p{N}_$]+");
    }
}
