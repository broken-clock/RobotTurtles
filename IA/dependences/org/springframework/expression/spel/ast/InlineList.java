// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelNode;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.springframework.expression.TypedValue;

public class InlineList extends SpelNodeImpl
{
    TypedValue constant;
    
    public InlineList(final int pos, final SpelNodeImpl... args) {
        super(pos, args);
        this.constant = null;
        this.checkIfConstant();
    }
    
    private void checkIfConstant() {
        boolean isConstant = true;
        for (int c = 0, max = this.getChildCount(); c < max; ++c) {
            final SpelNode child = this.getChild(c);
            if (!(child instanceof Literal)) {
                if (child instanceof InlineList) {
                    final InlineList inlineList = (InlineList)child;
                    if (!inlineList.isConstant()) {
                        isConstant = false;
                    }
                }
                else {
                    isConstant = false;
                }
            }
        }
        if (isConstant) {
            final List<Object> constantList = new ArrayList<Object>();
            for (int childcount = this.getChildCount(), c2 = 0; c2 < childcount; ++c2) {
                final SpelNode child2 = this.getChild(c2);
                if (child2 instanceof Literal) {
                    constantList.add(((Literal)child2).getLiteralValue().getValue());
                }
                else if (child2 instanceof InlineList) {
                    constantList.add(((InlineList)child2).getConstantValue());
                }
            }
            this.constant = new TypedValue(Collections.unmodifiableList((List<?>)constantList));
        }
    }
    
    @Override
    public TypedValue getValueInternal(final ExpressionState expressionState) throws EvaluationException {
        if (this.constant != null) {
            return this.constant;
        }
        final List<Object> returnValue = new ArrayList<Object>();
        for (int childcount = this.getChildCount(), c = 0; c < childcount; ++c) {
            returnValue.add(this.getChild(c).getValue(expressionState));
        }
        return new TypedValue(returnValue);
    }
    
    @Override
    public String toStringAST() {
        final StringBuilder s = new StringBuilder();
        s.append('{');
        for (int count = this.getChildCount(), c = 0; c < count; ++c) {
            if (c > 0) {
                s.append(',');
            }
            s.append(this.getChild(c).toStringAST());
        }
        s.append('}');
        return s.toString();
    }
    
    public boolean isConstant() {
        return this.constant != null;
    }
    
    private List<Object> getConstantValue() {
        return (List<Object>)this.constant.getValue();
    }
}
