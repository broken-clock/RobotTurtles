// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.AccessException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;

public class BeanReference extends SpelNodeImpl
{
    private final String beanname;
    
    public BeanReference(final int pos, final String beanname) {
        super(pos, new SpelNodeImpl[0]);
        this.beanname = beanname;
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState state) throws EvaluationException {
        final BeanResolver beanResolver = state.getEvaluationContext().getBeanResolver();
        if (beanResolver == null) {
            throw new SpelEvaluationException(this.getStartPosition(), SpelMessage.NO_BEAN_RESOLVER_REGISTERED, new Object[] { this.beanname });
        }
        try {
            final TypedValue bean = new TypedValue(beanResolver.resolve(state.getEvaluationContext(), this.beanname));
            return bean;
        }
        catch (AccessException ae) {
            throw new SpelEvaluationException(this.getStartPosition(), ae, SpelMessage.EXCEPTION_DURING_BEAN_RESOLUTION, new Object[] { this.beanname, ae.getMessage() });
        }
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder sb = new StringBuilder();
        sb.append("@");
        if (this.beanname.indexOf(46) == -1) {
            sb.append(this.beanname);
        }
        else {
            sb.append("'").append(this.beanname).append("'");
        }
        return sb.toString();
    }
}
