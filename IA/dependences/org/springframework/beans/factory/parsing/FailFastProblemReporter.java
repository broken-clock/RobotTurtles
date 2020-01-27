// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class FailFastProblemReporter implements ProblemReporter
{
    private Log logger;
    
    public FailFastProblemReporter() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public void setLogger(final Log logger) {
        this.logger = ((logger != null) ? logger : LogFactory.getLog(this.getClass()));
    }
    
    @Override
    public void fatal(final Problem problem) {
        throw new BeanDefinitionParsingException(problem);
    }
    
    @Override
    public void error(final Problem problem) {
        throw new BeanDefinitionParsingException(problem);
    }
    
    @Override
    public void warning(final Problem problem) {
        this.logger.warn(problem, problem.getRootCause());
    }
}
