// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.springframework.core.Constants;
import org.springframework.util.StringUtils;
import org.springframework.util.ClassUtils;
import java.util.regex.Matcher;
import org.springframework.util.StopWatch;
import org.apache.commons.logging.Log;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;
import java.util.Set;
import java.util.regex.Pattern;

public class CustomizableTraceInterceptor extends AbstractTraceInterceptor
{
    public static final String PLACEHOLDER_METHOD_NAME = "$[methodName]";
    public static final String PLACEHOLDER_TARGET_CLASS_NAME = "$[targetClassName]";
    public static final String PLACEHOLDER_TARGET_CLASS_SHORT_NAME = "$[targetClassShortName]";
    public static final String PLACEHOLDER_RETURN_VALUE = "$[returnValue]";
    public static final String PLACEHOLDER_ARGUMENT_TYPES = "$[argumentTypes]";
    public static final String PLACEHOLDER_ARGUMENTS = "$[arguments]";
    public static final String PLACEHOLDER_EXCEPTION = "$[exception]";
    public static final String PLACEHOLDER_INVOCATION_TIME = "$[invocationTime]";
    private static final String DEFAULT_ENTER_MESSAGE = "Entering method '$[methodName]' of class [$[targetClassName]]";
    private static final String DEFAULT_EXIT_MESSAGE = "Exiting method '$[methodName]' of class [$[targetClassName]]";
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Exception thrown in method '$[methodName]' of class [$[targetClassName]]";
    private static final Pattern PATTERN;
    private static final Set<Object> ALLOWED_PLACEHOLDERS;
    private String enterMessage;
    private String exitMessage;
    private String exceptionMessage;
    
    public CustomizableTraceInterceptor() {
        this.enterMessage = "Entering method '$[methodName]' of class [$[targetClassName]]";
        this.exitMessage = "Exiting method '$[methodName]' of class [$[targetClassName]]";
        this.exceptionMessage = "Exception thrown in method '$[methodName]' of class [$[targetClassName]]";
    }
    
    public void setEnterMessage(final String enterMessage) throws IllegalArgumentException {
        Assert.hasText(enterMessage, "'enterMessage' must not be empty");
        this.checkForInvalidPlaceholders(enterMessage);
        Assert.doesNotContain(enterMessage, "$[returnValue]", "enterMessage cannot contain placeholder [$[returnValue]]");
        Assert.doesNotContain(enterMessage, "$[exception]", "enterMessage cannot contain placeholder [$[exception]]");
        Assert.doesNotContain(enterMessage, "$[invocationTime]", "enterMessage cannot contain placeholder [$[invocationTime]]");
        this.enterMessage = enterMessage;
    }
    
    public void setExitMessage(final String exitMessage) {
        Assert.hasText(exitMessage, "'exitMessage' must not be empty");
        this.checkForInvalidPlaceholders(exitMessage);
        Assert.doesNotContain(exitMessage, "$[exception]", "exitMessage cannot contain placeholder [$[exception]]");
        this.exitMessage = exitMessage;
    }
    
    public void setExceptionMessage(final String exceptionMessage) {
        Assert.hasText(exceptionMessage, "'exceptionMessage' must not be empty");
        this.checkForInvalidPlaceholders(exceptionMessage);
        Assert.doesNotContain(exceptionMessage, "$[returnValue]", "exceptionMessage cannot contain placeholder [$[returnValue]]");
        Assert.doesNotContain(exceptionMessage, "$[invocationTime]", "exceptionMessage cannot contain placeholder [$[invocationTime]]");
        this.exceptionMessage = exceptionMessage;
    }
    
    @Override
    protected Object invokeUnderTrace(final MethodInvocation invocation, final Log logger) throws Throwable {
        final String name = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
        final StopWatch stopWatch = new StopWatch(name);
        Object returnValue = null;
        boolean exitThroughException = false;
        try {
            stopWatch.start(name);
            this.writeToLog(logger, this.replacePlaceholders(this.enterMessage, invocation, null, null, -1L));
            returnValue = invocation.proceed();
            return returnValue;
        }
        catch (Throwable ex) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            exitThroughException = true;
            this.writeToLog(logger, this.replacePlaceholders(this.exceptionMessage, invocation, null, ex, stopWatch.getTotalTimeMillis()), ex);
            throw ex;
        }
        finally {
            if (!exitThroughException) {
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                this.writeToLog(logger, this.replacePlaceholders(this.exitMessage, invocation, returnValue, null, stopWatch.getTotalTimeMillis()));
            }
        }
    }
    
    protected void writeToLog(final Log logger, final String message) {
        this.writeToLog(logger, message, null);
    }
    
    protected void writeToLog(final Log logger, final String message, final Throwable ex) {
        if (ex != null) {
            logger.trace(message, ex);
        }
        else {
            logger.trace(message);
        }
    }
    
    protected String replacePlaceholders(final String message, final MethodInvocation methodInvocation, final Object returnValue, final Throwable throwable, final long invocationTime) {
        final Matcher matcher = CustomizableTraceInterceptor.PATTERN.matcher(message);
        final StringBuffer output = new StringBuffer();
        while (matcher.find()) {
            final String match = matcher.group();
            if ("$[methodName]".equals(match)) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(methodInvocation.getMethod().getName()));
            }
            else if ("$[targetClassName]".equals(match)) {
                final String className = this.getClassForLogging(methodInvocation.getThis()).getName();
                matcher.appendReplacement(output, Matcher.quoteReplacement(className));
            }
            else if ("$[targetClassShortName]".equals(match)) {
                final String shortName = ClassUtils.getShortName(this.getClassForLogging(methodInvocation.getThis()));
                matcher.appendReplacement(output, Matcher.quoteReplacement(shortName));
            }
            else if ("$[arguments]".equals(match)) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(StringUtils.arrayToCommaDelimitedString(methodInvocation.getArguments())));
            }
            else if ("$[argumentTypes]".equals(match)) {
                this.appendArgumentTypes(methodInvocation, matcher, output);
            }
            else if ("$[returnValue]".equals(match)) {
                this.appendReturnValue(methodInvocation, matcher, output, returnValue);
            }
            else if (throwable != null && "$[exception]".equals(match)) {
                matcher.appendReplacement(output, Matcher.quoteReplacement(throwable.toString()));
            }
            else {
                if (!"$[invocationTime]".equals(match)) {
                    throw new IllegalArgumentException("Unknown placeholder [" + match + "]");
                }
                matcher.appendReplacement(output, Long.toString(invocationTime));
            }
        }
        matcher.appendTail(output);
        return output.toString();
    }
    
    private void appendReturnValue(final MethodInvocation methodInvocation, final Matcher matcher, final StringBuffer output, final Object returnValue) {
        if (methodInvocation.getMethod().getReturnType() == Void.TYPE) {
            matcher.appendReplacement(output, "void");
        }
        else if (returnValue == null) {
            matcher.appendReplacement(output, "null");
        }
        else {
            matcher.appendReplacement(output, Matcher.quoteReplacement(returnValue.toString()));
        }
    }
    
    private void appendArgumentTypes(final MethodInvocation methodInvocation, final Matcher matcher, final StringBuffer output) {
        final Class<?>[] argumentTypes = methodInvocation.getMethod().getParameterTypes();
        final String[] argumentTypeShortNames = new String[argumentTypes.length];
        for (int i = 0; i < argumentTypeShortNames.length; ++i) {
            argumentTypeShortNames[i] = ClassUtils.getShortName(argumentTypes[i]);
        }
        matcher.appendReplacement(output, Matcher.quoteReplacement(StringUtils.arrayToCommaDelimitedString(argumentTypeShortNames)));
    }
    
    private void checkForInvalidPlaceholders(final String message) throws IllegalArgumentException {
        final Matcher matcher = CustomizableTraceInterceptor.PATTERN.matcher(message);
        while (matcher.find()) {
            final String match = matcher.group();
            if (!CustomizableTraceInterceptor.ALLOWED_PLACEHOLDERS.contains(match)) {
                throw new IllegalArgumentException("Placeholder [" + match + "] is not valid");
            }
        }
    }
    
    static {
        PATTERN = Pattern.compile("\\$\\[\\p{Alpha}+\\]");
        ALLOWED_PLACEHOLDERS = new Constants(CustomizableTraceInterceptor.class).getValues("PLACEHOLDER_");
    }
}
