// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import org.springframework.util.Assert;
import java.util.LinkedList;
import java.util.List;

public class CompositeComponentDefinition extends AbstractComponentDefinition
{
    private final String name;
    private final Object source;
    private final List<ComponentDefinition> nestedComponents;
    
    public CompositeComponentDefinition(final String name, final Object source) {
        this.nestedComponents = new LinkedList<ComponentDefinition>();
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.source = source;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Object getSource() {
        return this.source;
    }
    
    public void addNestedComponent(final ComponentDefinition component) {
        Assert.notNull(component, "ComponentDefinition must not be null");
        this.nestedComponents.add(component);
    }
    
    public ComponentDefinition[] getNestedComponents() {
        return this.nestedComponents.toArray(new ComponentDefinition[this.nestedComponents.size()]);
    }
}
