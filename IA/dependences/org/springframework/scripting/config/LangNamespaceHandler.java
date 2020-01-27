// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scripting.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class LangNamespaceHandler extends NamespaceHandlerSupport
{
    @Override
    public void init() {
        this.registerScriptBeanDefinitionParser("groovy", "org.springframework.scripting.groovy.GroovyScriptFactory");
        this.registerScriptBeanDefinitionParser("jruby", "org.springframework.scripting.jruby.JRubyScriptFactory");
        this.registerScriptBeanDefinitionParser("bsh", "org.springframework.scripting.bsh.BshScriptFactory");
        this.registerBeanDefinitionParser("defaults", new ScriptingDefaultsParser());
    }
    
    private void registerScriptBeanDefinitionParser(final String key, final String scriptFactoryClassName) {
        this.registerBeanDefinitionParser(key, new ScriptBeanDefinitionParser(scriptFactoryClassName));
    }
}
