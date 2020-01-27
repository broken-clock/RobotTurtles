// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.PointcutParser;
import java.util.HashSet;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import java.util.List;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import org.springframework.core.ParameterNameDiscoverer;

public class AspectJAdviceParameterNameDiscoverer implements ParameterNameDiscoverer
{
    private static final String THIS_JOIN_POINT = "thisJoinPoint";
    private static final String THIS_JOIN_POINT_STATIC_PART = "thisJoinPointStaticPart";
    private static final int STEP_JOIN_POINT_BINDING = 1;
    private static final int STEP_THROWING_BINDING = 2;
    private static final int STEP_ANNOTATION_BINDING = 3;
    private static final int STEP_RETURNING_BINDING = 4;
    private static final int STEP_PRIMITIVE_ARGS_BINDING = 5;
    private static final int STEP_THIS_TARGET_ARGS_BINDING = 6;
    private static final int STEP_REFERENCE_PCUT_BINDING = 7;
    private static final int STEP_FINISHED = 8;
    private static final Set<String> singleValuedAnnotationPcds;
    private static final Set<String> nonReferencePointcutTokens;
    private boolean raiseExceptions;
    private String returningName;
    private String throwingName;
    private String pointcutExpression;
    private Class<?>[] argumentTypes;
    private String[] parameterNameBindings;
    private int numberOfRemainingUnboundArguments;
    
    public AspectJAdviceParameterNameDiscoverer(final String pointcutExpression) {
        this.pointcutExpression = pointcutExpression;
    }
    
    public void setRaiseExceptions(final boolean raiseExceptions) {
        this.raiseExceptions = raiseExceptions;
    }
    
    public void setReturningName(final String returningName) {
        this.returningName = returningName;
    }
    
    public void setThrowingName(final String throwingName) {
        this.throwingName = throwingName;
    }
    
    @Override
    public String[] getParameterNames(final Method method) {
        this.argumentTypes = method.getParameterTypes();
        this.numberOfRemainingUnboundArguments = this.argumentTypes.length;
        this.parameterNameBindings = new String[this.numberOfRemainingUnboundArguments];
        int minimumNumberUnboundArgs = 0;
        if (this.returningName != null) {
            ++minimumNumberUnboundArgs;
        }
        if (this.throwingName != null) {
            ++minimumNumberUnboundArgs;
        }
        if (this.numberOfRemainingUnboundArguments < minimumNumberUnboundArgs) {
            throw new IllegalStateException("Not enough arguments in method to satisfy binding of returning and throwing variables");
        }
        try {
            int algorithmicStep = 1;
            while (this.numberOfRemainingUnboundArguments > 0 && algorithmicStep < 8) {
                switch (algorithmicStep++) {
                    case 1: {
                        if (!this.maybeBindThisJoinPoint()) {
                            this.maybeBindThisJoinPointStaticPart();
                            continue;
                        }
                        continue;
                    }
                    case 2: {
                        this.maybeBindThrowingVariable();
                        continue;
                    }
                    case 3: {
                        this.maybeBindAnnotationsFromPointcutExpression();
                        continue;
                    }
                    case 4: {
                        this.maybeBindReturningVariable();
                        continue;
                    }
                    case 5: {
                        this.maybeBindPrimitiveArgsFromPointcutExpression();
                        continue;
                    }
                    case 6: {
                        this.maybeBindThisOrTargetOrArgsFromPointcutExpression();
                        continue;
                    }
                    case 7: {
                        this.maybeBindReferencePointcutParameter();
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("Unknown algorithmic step: " + (algorithmicStep - 1));
                    }
                }
            }
        }
        catch (AmbiguousBindingException ambigEx) {
            if (this.raiseExceptions) {
                throw ambigEx;
            }
            return null;
        }
        catch (IllegalArgumentException ex) {
            if (this.raiseExceptions) {
                throw ex;
            }
            return null;
        }
        if (this.numberOfRemainingUnboundArguments == 0) {
            return this.parameterNameBindings;
        }
        if (this.raiseExceptions) {
            throw new IllegalStateException("Failed to bind all argument names: " + this.numberOfRemainingUnboundArguments + " argument(s) could not be bound");
        }
        return null;
    }
    
    @Override
    public String[] getParameterNames(final Constructor<?> ctor) {
        if (this.raiseExceptions) {
            throw new UnsupportedOperationException("An advice method can never be a constructor");
        }
        return null;
    }
    
    private void bindParameterName(final int index, final String name) {
        this.parameterNameBindings[index] = name;
        --this.numberOfRemainingUnboundArguments;
    }
    
    private boolean maybeBindThisJoinPoint() {
        if (this.argumentTypes[0] == JoinPoint.class || this.argumentTypes[0] == ProceedingJoinPoint.class) {
            this.bindParameterName(0, "thisJoinPoint");
            return true;
        }
        return false;
    }
    
    private void maybeBindThisJoinPointStaticPart() {
        if (this.argumentTypes[0] == JoinPoint.StaticPart.class) {
            this.bindParameterName(0, "thisJoinPointStaticPart");
        }
    }
    
    private void maybeBindThrowingVariable() {
        if (this.throwingName == null) {
            return;
        }
        int throwableIndex = -1;
        for (int i = 0; i < this.argumentTypes.length; ++i) {
            if (this.isUnbound(i) && this.isSubtypeOf(Throwable.class, i)) {
                if (throwableIndex != -1) {
                    throw new AmbiguousBindingException("Binding of throwing parameter '" + this.throwingName + "' is ambiguous: could be bound to argument " + throwableIndex + " or argument " + i);
                }
                throwableIndex = i;
            }
        }
        if (throwableIndex == -1) {
            throw new IllegalStateException("Binding of throwing parameter '" + this.throwingName + "' could not be completed as no available arguments are a subtype of Throwable");
        }
        this.bindParameterName(throwableIndex, this.throwingName);
    }
    
    private void maybeBindReturningVariable() {
        if (this.numberOfRemainingUnboundArguments == 0) {
            throw new IllegalStateException("Algorithm assumes that there must be at least one unbound parameter on entry to this method");
        }
        if (this.returningName != null) {
            if (this.numberOfRemainingUnboundArguments > 1) {
                throw new AmbiguousBindingException("Binding of returning parameter '" + this.returningName + "' is ambiguous, there are " + this.numberOfRemainingUnboundArguments + " candidates.");
            }
            for (int i = 0; i < this.parameterNameBindings.length; ++i) {
                if (this.parameterNameBindings[i] == null) {
                    this.bindParameterName(i, this.returningName);
                    break;
                }
            }
        }
    }
    
    private void maybeBindAnnotationsFromPointcutExpression() {
        final List<String> varNames = new ArrayList<String>();
        final String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        for (int i = 0; i < tokens.length; ++i) {
            String toMatch = tokens[i];
            final int firstParenIndex = toMatch.indexOf("(");
            if (firstParenIndex != -1) {
                toMatch = toMatch.substring(0, firstParenIndex);
            }
            if (AspectJAdviceParameterNameDiscoverer.singleValuedAnnotationPcds.contains(toMatch)) {
                final PointcutBody body = this.getPointcutBody(tokens, i);
                i += body.numTokensConsumed;
                final String varName = this.maybeExtractVariableName(body.text);
                if (varName != null) {
                    varNames.add(varName);
                }
            }
            else if (tokens[i].startsWith("@args(") || tokens[i].equals("@args")) {
                final PointcutBody body = this.getPointcutBody(tokens, i);
                i += body.numTokensConsumed;
                this.maybeExtractVariableNamesFromArgs(body.text, varNames);
            }
        }
        this.bindAnnotationsFromVarNames(varNames);
    }
    
    private void bindAnnotationsFromVarNames(final List<String> varNames) {
        if (!varNames.isEmpty()) {
            final int numAnnotationSlots = this.countNumberOfUnboundAnnotationArguments();
            if (numAnnotationSlots > 1) {
                throw new AmbiguousBindingException("Found " + varNames.size() + " potential annotation variable(s), and " + numAnnotationSlots + " potential argument slots");
            }
            if (numAnnotationSlots == 1) {
                if (varNames.size() != 1) {
                    throw new IllegalArgumentException("Found " + varNames.size() + " candidate annotation binding variables" + " but only one potential argument binding slot");
                }
                this.findAndBind(Annotation.class, varNames.get(0));
            }
        }
    }
    
    private String maybeExtractVariableName(final String candidateToken) {
        if (candidateToken == null || candidateToken.equals("")) {
            return null;
        }
        if (Character.isJavaIdentifierStart(candidateToken.charAt(0)) && Character.isLowerCase(candidateToken.charAt(0))) {
            final char[] charArray;
            final char[] tokenChars = charArray = candidateToken.toCharArray();
            for (final char tokenChar : charArray) {
                if (!Character.isJavaIdentifierPart(tokenChar)) {
                    return null;
                }
            }
            return candidateToken;
        }
        return null;
    }
    
    private void maybeExtractVariableNamesFromArgs(final String argsSpec, final List<String> varNames) {
        if (argsSpec == null) {
            return;
        }
        final String[] tokens = StringUtils.tokenizeToStringArray(argsSpec, ",");
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = StringUtils.trimWhitespace(tokens[i]);
            final String varName = this.maybeExtractVariableName(tokens[i]);
            if (varName != null) {
                varNames.add(varName);
            }
        }
    }
    
    private void maybeBindThisOrTargetOrArgsFromPointcutExpression() {
        if (this.numberOfRemainingUnboundArguments > 1) {
            throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments + " unbound args at this(),target(),args() binding stage, with no way to determine between them");
        }
        final List<String> varNames = new ArrayList<String>();
        final String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        for (int i = 0; i < tokens.length; ++i) {
            if (tokens[i].equals("this") || tokens[i].startsWith("this(") || tokens[i].equals("target") || tokens[i].startsWith("target(")) {
                final PointcutBody body = this.getPointcutBody(tokens, i);
                i += body.numTokensConsumed;
                final String varName = this.maybeExtractVariableName(body.text);
                if (varName != null) {
                    varNames.add(varName);
                }
            }
            else if (tokens[i].equals("args") || tokens[i].startsWith("args(")) {
                final PointcutBody body = this.getPointcutBody(tokens, i);
                i += body.numTokensConsumed;
                final List<String> candidateVarNames = new ArrayList<String>();
                this.maybeExtractVariableNamesFromArgs(body.text, candidateVarNames);
                for (final String varName2 : candidateVarNames) {
                    if (!this.alreadyBound(varName2)) {
                        varNames.add(varName2);
                    }
                }
            }
        }
        if (varNames.size() > 1) {
            throw new AmbiguousBindingException("Found " + varNames.size() + " candidate this(), target() or args() variables but only one unbound argument slot");
        }
        if (varNames.size() == 1) {
            for (int j = 0; j < this.parameterNameBindings.length; ++j) {
                if (this.isUnbound(j)) {
                    this.bindParameterName(j, varNames.get(0));
                    break;
                }
            }
        }
    }
    
    private void maybeBindReferencePointcutParameter() {
        if (this.numberOfRemainingUnboundArguments > 1) {
            throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments + " unbound args at reference pointcut binding stage, with no way to determine between them");
        }
        final List<String> varNames = new ArrayList<String>();
        final String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        for (int i = 0; i < tokens.length; ++i) {
            String toMatch = tokens[i];
            if (toMatch.startsWith("!")) {
                toMatch = toMatch.substring(1);
            }
            final int firstParenIndex = toMatch.indexOf("(");
            if (firstParenIndex != -1) {
                toMatch = toMatch.substring(0, firstParenIndex);
            }
            else {
                if (tokens.length < i + 2) {
                    continue;
                }
                final String nextToken = tokens[i + 1];
                if (nextToken.charAt(0) != '(') {
                    continue;
                }
            }
            final PointcutBody body = this.getPointcutBody(tokens, i);
            i += body.numTokensConsumed;
            if (!AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.contains(toMatch)) {
                final String varName = this.maybeExtractVariableName(body.text);
                if (varName != null) {
                    varNames.add(varName);
                }
            }
        }
        if (varNames.size() > 1) {
            throw new AmbiguousBindingException("Found " + varNames.size() + " candidate reference pointcut variables but only one unbound argument slot");
        }
        if (varNames.size() == 1) {
            for (int j = 0; j < this.parameterNameBindings.length; ++j) {
                if (this.isUnbound(j)) {
                    this.bindParameterName(j, varNames.get(0));
                    break;
                }
            }
        }
    }
    
    private PointcutBody getPointcutBody(final String[] tokens, final int startIndex) {
        int numTokensConsumed = 0;
        final String currentToken = tokens[startIndex];
        final int bodyStart = currentToken.indexOf(40);
        if (currentToken.charAt(currentToken.length() - 1) == ')') {
            return new PointcutBody(0, currentToken.substring(bodyStart + 1, currentToken.length() - 1));
        }
        final StringBuilder sb = new StringBuilder();
        if (bodyStart >= 0 && bodyStart != currentToken.length() - 1) {
            sb.append(currentToken.substring(bodyStart + 1));
            sb.append(" ");
        }
        ++numTokensConsumed;
        int currentIndex = startIndex + numTokensConsumed;
        while (currentIndex < tokens.length) {
            if (tokens[currentIndex].equals("(")) {
                ++currentIndex;
            }
            else {
                if (tokens[currentIndex].endsWith(")")) {
                    sb.append(tokens[currentIndex].substring(0, tokens[currentIndex].length() - 1));
                    return new PointcutBody(numTokensConsumed, sb.toString().trim());
                }
                String toAppend = tokens[currentIndex];
                if (toAppend.startsWith("(")) {
                    toAppend = toAppend.substring(1);
                }
                sb.append(toAppend);
                sb.append(" ");
                ++currentIndex;
                ++numTokensConsumed;
            }
        }
        return new PointcutBody(numTokensConsumed, null);
    }
    
    private void maybeBindPrimitiveArgsFromPointcutExpression() {
        final int numUnboundPrimitives = this.countNumberOfUnboundPrimitiveArguments();
        if (numUnboundPrimitives > 1) {
            throw new AmbiguousBindingException("Found '" + numUnboundPrimitives + "' unbound primitive arguments with no way to distinguish between them.");
        }
        if (numUnboundPrimitives == 1) {
            final List<String> varNames = new ArrayList<String>();
            final String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
            for (int i = 0; i < tokens.length; ++i) {
                if (tokens[i].equals("args") || tokens[i].startsWith("args(")) {
                    final PointcutBody body = this.getPointcutBody(tokens, i);
                    i += body.numTokensConsumed;
                    this.maybeExtractVariableNamesFromArgs(body.text, varNames);
                }
            }
            if (varNames.size() > 1) {
                throw new AmbiguousBindingException("Found " + varNames.size() + " candidate variable names but only one candidate binding slot when matching primitive args");
            }
            if (varNames.size() == 1) {
                for (int i = 0; i < this.argumentTypes.length; ++i) {
                    if (this.isUnbound(i) && this.argumentTypes[i].isPrimitive()) {
                        this.bindParameterName(i, varNames.get(0));
                        break;
                    }
                }
            }
        }
    }
    
    private boolean isUnbound(final int i) {
        return this.parameterNameBindings[i] == null;
    }
    
    private boolean alreadyBound(final String varName) {
        for (int i = 0; i < this.parameterNameBindings.length; ++i) {
            if (!this.isUnbound(i) && varName.equals(this.parameterNameBindings[i])) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSubtypeOf(final Class<?> supertype, final int argumentNumber) {
        return supertype.isAssignableFrom(this.argumentTypes[argumentNumber]);
    }
    
    private int countNumberOfUnboundAnnotationArguments() {
        int count = 0;
        for (int i = 0; i < this.argumentTypes.length; ++i) {
            if (this.isUnbound(i) && this.isSubtypeOf(Annotation.class, i)) {
                ++count;
            }
        }
        return count;
    }
    
    private int countNumberOfUnboundPrimitiveArguments() {
        int count = 0;
        for (int i = 0; i < this.argumentTypes.length; ++i) {
            if (this.isUnbound(i) && this.argumentTypes[i].isPrimitive()) {
                ++count;
            }
        }
        return count;
    }
    
    private void findAndBind(final Class<?> argumentType, final String varName) {
        for (int i = 0; i < this.argumentTypes.length; ++i) {
            if (this.isUnbound(i) && this.isSubtypeOf(argumentType, i)) {
                this.bindParameterName(i, varName);
                return;
            }
        }
        throw new IllegalStateException("Expected to find an unbound argument of type '" + argumentType.getName() + "'");
    }
    
    static {
        singleValuedAnnotationPcds = new HashSet<String>();
        nonReferencePointcutTokens = new HashSet<String>();
        AspectJAdviceParameterNameDiscoverer.singleValuedAnnotationPcds.add("@this");
        AspectJAdviceParameterNameDiscoverer.singleValuedAnnotationPcds.add("@target");
        AspectJAdviceParameterNameDiscoverer.singleValuedAnnotationPcds.add("@within");
        AspectJAdviceParameterNameDiscoverer.singleValuedAnnotationPcds.add("@withincode");
        AspectJAdviceParameterNameDiscoverer.singleValuedAnnotationPcds.add("@annotation");
        final Set<PointcutPrimitive> pointcutPrimitives = (Set<PointcutPrimitive>)PointcutParser.getAllSupportedPointcutPrimitives();
        for (final PointcutPrimitive primitive : pointcutPrimitives) {
            AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.add(primitive.getName());
        }
        AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.add("&&");
        AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.add("!");
        AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.add("||");
        AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.add("and");
        AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.add("or");
        AspectJAdviceParameterNameDiscoverer.nonReferencePointcutTokens.add("not");
    }
    
    private static class PointcutBody
    {
        private int numTokensConsumed;
        private String text;
        
        public PointcutBody(final int tokens, final String text) {
            this.numTokensConsumed = tokens;
            this.text = text;
        }
    }
    
    public static class AmbiguousBindingException extends RuntimeException
    {
        public AmbiguousBindingException(final String msg) {
            super(msg);
        }
    }
}
