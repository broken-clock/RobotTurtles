// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public interface ParserContext
{
    public static final ParserContext TEMPLATE_EXPRESSION = new ParserContext() {
        @Override
        public String getExpressionPrefix() {
            return "#{";
        }
        
        @Override
        public String getExpressionSuffix() {
            return "}";
        }
        
        @Override
        public boolean isTemplate() {
            return true;
        }
    };
    
    boolean isTemplate();
    
    String getExpressionPrefix();
    
    String getExpressionSuffix();
}
