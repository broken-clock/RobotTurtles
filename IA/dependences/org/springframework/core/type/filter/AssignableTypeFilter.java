// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.filter;

public class AssignableTypeFilter extends AbstractTypeHierarchyTraversingFilter
{
    private final Class<?> targetType;
    
    public AssignableTypeFilter(final Class<?> targetType) {
        super(true, true);
        this.targetType = targetType;
    }
    
    @Override
    protected boolean matchClassName(final String className) {
        return this.targetType.getName().equals(className);
    }
    
    @Override
    protected Boolean matchSuperClass(final String superClassName) {
        return this.matchTargetType(superClassName);
    }
    
    @Override
    protected Boolean matchInterface(final String interfaceName) {
        return this.matchTargetType(interfaceName);
    }
    
    protected Boolean matchTargetType(final String typeName) {
        if (this.targetType.getName().equals(typeName)) {
            return true;
        }
        if (Object.class.getName().equals(typeName)) {
            return Boolean.FALSE;
        }
        if (typeName.startsWith("java.")) {
            try {
                final Class<?> clazz = this.getClass().getClassLoader().loadClass(typeName);
                return this.targetType.isAssignableFrom(clazz);
            }
            catch (ClassNotFoundException ex) {}
        }
        return null;
    }
}
