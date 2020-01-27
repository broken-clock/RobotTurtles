// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJAfterThrowingAdvice;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.aop.aspectj.AspectJPointcutAdvisor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.aop.aspectj.DeclareParentsAdvisor;
import org.w3c.dom.NodeList;
import org.springframework.beans.factory.config.BeanReference;
import java.util.ArrayList;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.util.StringUtils;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.springframework.util.xml.DomUtils;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

class ConfigBeanDefinitionParser implements BeanDefinitionParser
{
    private static final String ASPECT = "aspect";
    private static final String EXPRESSION = "expression";
    private static final String ID = "id";
    private static final String POINTCUT = "pointcut";
    private static final String ADVICE_BEAN_NAME = "adviceBeanName";
    private static final String ADVISOR = "advisor";
    private static final String ADVICE_REF = "advice-ref";
    private static final String POINTCUT_REF = "pointcut-ref";
    private static final String REF = "ref";
    private static final String BEFORE = "before";
    private static final String DECLARE_PARENTS = "declare-parents";
    private static final String TYPE_PATTERN = "types-matching";
    private static final String DEFAULT_IMPL = "default-impl";
    private static final String DELEGATE_REF = "delegate-ref";
    private static final String IMPLEMENT_INTERFACE = "implement-interface";
    private static final String AFTER = "after";
    private static final String AFTER_RETURNING_ELEMENT = "after-returning";
    private static final String AFTER_THROWING_ELEMENT = "after-throwing";
    private static final String AROUND = "around";
    private static final String RETURNING = "returning";
    private static final String RETURNING_PROPERTY = "returningName";
    private static final String THROWING = "throwing";
    private static final String THROWING_PROPERTY = "throwingName";
    private static final String ARG_NAMES = "arg-names";
    private static final String ARG_NAMES_PROPERTY = "argumentNames";
    private static final String ASPECT_NAME_PROPERTY = "aspectName";
    private static final String DECLARATION_ORDER_PROPERTY = "declarationOrder";
    private static final String ORDER_PROPERTY = "order";
    private static final int METHOD_INDEX = 0;
    private static final int POINTCUT_INDEX = 1;
    private static final int ASPECT_INSTANCE_FACTORY_INDEX = 2;
    private ParseState parseState;
    
    ConfigBeanDefinitionParser() {
        this.parseState = new ParseState();
    }
    
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
        parserContext.pushContainingComponent(compositeDef);
        this.configureAutoProxyCreator(parserContext, element);
        final List<Element> childElts = DomUtils.getChildElements(element);
        for (final Element elt : childElts) {
            final String localName = parserContext.getDelegate().getLocalName(elt);
            if ("pointcut".equals(localName)) {
                this.parsePointcut(elt, parserContext);
            }
            else if ("advisor".equals(localName)) {
                this.parseAdvisor(elt, parserContext);
            }
            else {
                if (!"aspect".equals(localName)) {
                    continue;
                }
                this.parseAspect(elt, parserContext);
            }
        }
        parserContext.popAndRegisterContainingComponent();
        return null;
    }
    
    private void configureAutoProxyCreator(final ParserContext parserContext, final Element element) {
        AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(parserContext, element);
    }
    
    private void parseAdvisor(final Element advisorElement, final ParserContext parserContext) {
        final AbstractBeanDefinition advisorDef = this.createAdvisorBeanDefinition(advisorElement, parserContext);
        final String id = advisorElement.getAttribute("id");
        try {
            this.parseState.push(new AdvisorEntry(id));
            String advisorBeanName = id;
            if (StringUtils.hasText(advisorBeanName)) {
                parserContext.getRegistry().registerBeanDefinition(advisorBeanName, advisorDef);
            }
            else {
                advisorBeanName = parserContext.getReaderContext().registerWithGeneratedName(advisorDef);
            }
            final Object pointcut = this.parsePointcutProperty(advisorElement, parserContext);
            if (pointcut instanceof BeanDefinition) {
                advisorDef.getPropertyValues().add("pointcut", pointcut);
                parserContext.registerComponent(new AdvisorComponentDefinition(advisorBeanName, advisorDef, (BeanDefinition)pointcut));
            }
            else if (pointcut instanceof String) {
                advisorDef.getPropertyValues().add("pointcut", new RuntimeBeanReference((String)pointcut));
                parserContext.registerComponent(new AdvisorComponentDefinition(advisorBeanName, advisorDef));
            }
        }
        finally {
            this.parseState.pop();
        }
    }
    
    private AbstractBeanDefinition createAdvisorBeanDefinition(final Element advisorElement, final ParserContext parserContext) {
        final RootBeanDefinition advisorDefinition = new RootBeanDefinition(DefaultBeanFactoryPointcutAdvisor.class);
        advisorDefinition.setSource(parserContext.extractSource(advisorElement));
        final String adviceRef = advisorElement.getAttribute("advice-ref");
        if (!StringUtils.hasText(adviceRef)) {
            parserContext.getReaderContext().error("'advice-ref' attribute contains empty value.", advisorElement, this.parseState.snapshot());
        }
        else {
            advisorDefinition.getPropertyValues().add("adviceBeanName", new RuntimeBeanNameReference(adviceRef));
        }
        if (advisorElement.hasAttribute("order")) {
            advisorDefinition.getPropertyValues().add("order", advisorElement.getAttribute("order"));
        }
        return advisorDefinition;
    }
    
    private void parseAspect(final Element aspectElement, final ParserContext parserContext) {
        final String aspectId = aspectElement.getAttribute("id");
        final String aspectName = aspectElement.getAttribute("ref");
        try {
            this.parseState.push(new AspectEntry(aspectId, aspectName));
            final List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
            final List<BeanReference> beanReferences = new ArrayList<BeanReference>();
            final List<Element> declareParents = DomUtils.getChildElementsByTagName(aspectElement, "declare-parents");
            for (int i = 0; i < declareParents.size(); ++i) {
                final Element declareParentsElement = declareParents.get(i);
                beanDefinitions.add(this.parseDeclareParents(declareParentsElement, parserContext));
            }
            final NodeList nodeList = aspectElement.getChildNodes();
            boolean adviceFoundAlready = false;
            for (int j = 0; j < nodeList.getLength(); ++j) {
                final Node node = nodeList.item(j);
                if (this.isAdviceNode(node, parserContext)) {
                    if (!adviceFoundAlready) {
                        adviceFoundAlready = true;
                        if (!StringUtils.hasText(aspectName)) {
                            parserContext.getReaderContext().error("<aspect> tag needs aspect bean reference via 'ref' attribute when declaring advices.", aspectElement, this.parseState.snapshot());
                            return;
                        }
                        beanReferences.add(new RuntimeBeanReference(aspectName));
                    }
                    final AbstractBeanDefinition advisorDefinition = this.parseAdvice(aspectName, j, aspectElement, (Element)node, parserContext, beanDefinitions, beanReferences);
                    beanDefinitions.add(advisorDefinition);
                }
            }
            final AspectComponentDefinition aspectComponentDefinition = this.createAspectComponentDefinition(aspectElement, aspectId, beanDefinitions, beanReferences, parserContext);
            parserContext.pushContainingComponent(aspectComponentDefinition);
            final List<Element> pointcuts = DomUtils.getChildElementsByTagName(aspectElement, "pointcut");
            for (final Element pointcutElement : pointcuts) {
                this.parsePointcut(pointcutElement, parserContext);
            }
            parserContext.popAndRegisterContainingComponent();
        }
        finally {
            this.parseState.pop();
        }
    }
    
    private AspectComponentDefinition createAspectComponentDefinition(final Element aspectElement, final String aspectId, final List<BeanDefinition> beanDefs, final List<BeanReference> beanRefs, final ParserContext parserContext) {
        final BeanDefinition[] beanDefArray = beanDefs.toArray(new BeanDefinition[beanDefs.size()]);
        final BeanReference[] beanRefArray = beanRefs.toArray(new BeanReference[beanRefs.size()]);
        final Object source = parserContext.extractSource(aspectElement);
        return new AspectComponentDefinition(aspectId, beanDefArray, beanRefArray, source);
    }
    
    private boolean isAdviceNode(final Node aNode, final ParserContext parserContext) {
        if (!(aNode instanceof Element)) {
            return false;
        }
        final String name = parserContext.getDelegate().getLocalName(aNode);
        return "before".equals(name) || "after".equals(name) || "after-returning".equals(name) || "after-throwing".equals(name) || "around".equals(name);
    }
    
    private AbstractBeanDefinition parseDeclareParents(final Element declareParentsElement, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DeclareParentsAdvisor.class);
        builder.addConstructorArgValue(declareParentsElement.getAttribute("implement-interface"));
        builder.addConstructorArgValue(declareParentsElement.getAttribute("types-matching"));
        final String defaultImpl = declareParentsElement.getAttribute("default-impl");
        final String delegateRef = declareParentsElement.getAttribute("delegate-ref");
        if (StringUtils.hasText(defaultImpl) && !StringUtils.hasText(delegateRef)) {
            builder.addConstructorArgValue(defaultImpl);
        }
        else if (StringUtils.hasText(delegateRef) && !StringUtils.hasText(defaultImpl)) {
            builder.addConstructorArgReference(delegateRef);
        }
        else {
            parserContext.getReaderContext().error("Exactly one of the default-impl or delegate-ref attributes must be specified", declareParentsElement, this.parseState.snapshot());
        }
        final AbstractBeanDefinition definition = builder.getBeanDefinition();
        definition.setSource(parserContext.extractSource(declareParentsElement));
        parserContext.getReaderContext().registerWithGeneratedName(definition);
        return definition;
    }
    
    private AbstractBeanDefinition parseAdvice(final String aspectName, final int order, final Element aspectElement, final Element adviceElement, final ParserContext parserContext, final List<BeanDefinition> beanDefinitions, final List<BeanReference> beanReferences) {
        try {
            this.parseState.push(new AdviceEntry(parserContext.getDelegate().getLocalName(adviceElement)));
            final RootBeanDefinition methodDefinition = new RootBeanDefinition(MethodLocatingFactoryBean.class);
            methodDefinition.getPropertyValues().add("targetBeanName", aspectName);
            methodDefinition.getPropertyValues().add("methodName", adviceElement.getAttribute("method"));
            methodDefinition.setSynthetic(true);
            final RootBeanDefinition aspectFactoryDef = new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
            aspectFactoryDef.getPropertyValues().add("aspectBeanName", aspectName);
            aspectFactoryDef.setSynthetic(true);
            final AbstractBeanDefinition adviceDef = this.createAdviceDefinition(adviceElement, parserContext, aspectName, order, methodDefinition, aspectFactoryDef, beanDefinitions, beanReferences);
            final RootBeanDefinition advisorDefinition = new RootBeanDefinition(AspectJPointcutAdvisor.class);
            advisorDefinition.setSource(parserContext.extractSource(adviceElement));
            advisorDefinition.getConstructorArgumentValues().addGenericArgumentValue(adviceDef);
            if (aspectElement.hasAttribute("order")) {
                advisorDefinition.getPropertyValues().add("order", aspectElement.getAttribute("order"));
            }
            parserContext.getReaderContext().registerWithGeneratedName(advisorDefinition);
            return advisorDefinition;
        }
        finally {
            this.parseState.pop();
        }
    }
    
    private AbstractBeanDefinition createAdviceDefinition(final Element adviceElement, final ParserContext parserContext, final String aspectName, final int order, final RootBeanDefinition methodDef, final RootBeanDefinition aspectFactoryDef, final List<BeanDefinition> beanDefinitions, final List<BeanReference> beanReferences) {
        final RootBeanDefinition adviceDefinition = new RootBeanDefinition(this.getAdviceClass(adviceElement, parserContext));
        adviceDefinition.setSource(parserContext.extractSource(adviceElement));
        adviceDefinition.getPropertyValues().add("aspectName", aspectName);
        adviceDefinition.getPropertyValues().add("declarationOrder", order);
        if (adviceElement.hasAttribute("returning")) {
            adviceDefinition.getPropertyValues().add("returningName", adviceElement.getAttribute("returning"));
        }
        if (adviceElement.hasAttribute("throwing")) {
            adviceDefinition.getPropertyValues().add("throwingName", adviceElement.getAttribute("throwing"));
        }
        if (adviceElement.hasAttribute("arg-names")) {
            adviceDefinition.getPropertyValues().add("argumentNames", adviceElement.getAttribute("arg-names"));
        }
        final ConstructorArgumentValues cav = adviceDefinition.getConstructorArgumentValues();
        cav.addIndexedArgumentValue(0, methodDef);
        final Object pointcut = this.parsePointcutProperty(adviceElement, parserContext);
        if (pointcut instanceof BeanDefinition) {
            cav.addIndexedArgumentValue(1, pointcut);
            beanDefinitions.add((BeanDefinition)pointcut);
        }
        else if (pointcut instanceof String) {
            final RuntimeBeanReference pointcutRef = new RuntimeBeanReference((String)pointcut);
            cav.addIndexedArgumentValue(1, pointcutRef);
            beanReferences.add(pointcutRef);
        }
        cav.addIndexedArgumentValue(2, aspectFactoryDef);
        return adviceDefinition;
    }
    
    private Class<?> getAdviceClass(final Element adviceElement, final ParserContext parserContext) {
        final String elementName = parserContext.getDelegate().getLocalName(adviceElement);
        if ("before".equals(elementName)) {
            return AspectJMethodBeforeAdvice.class;
        }
        if ("after".equals(elementName)) {
            return AspectJAfterAdvice.class;
        }
        if ("after-returning".equals(elementName)) {
            return AspectJAfterReturningAdvice.class;
        }
        if ("after-throwing".equals(elementName)) {
            return AspectJAfterThrowingAdvice.class;
        }
        if ("around".equals(elementName)) {
            return AspectJAroundAdvice.class;
        }
        throw new IllegalArgumentException("Unknown advice kind [" + elementName + "].");
    }
    
    private AbstractBeanDefinition parsePointcut(final Element pointcutElement, final ParserContext parserContext) {
        final String id = pointcutElement.getAttribute("id");
        final String expression = pointcutElement.getAttribute("expression");
        AbstractBeanDefinition pointcutDefinition = null;
        try {
            this.parseState.push(new PointcutEntry(id));
            pointcutDefinition = this.createPointcutDefinition(expression);
            pointcutDefinition.setSource(parserContext.extractSource(pointcutElement));
            String pointcutBeanName = id;
            if (StringUtils.hasText(pointcutBeanName)) {
                parserContext.getRegistry().registerBeanDefinition(pointcutBeanName, pointcutDefinition);
            }
            else {
                pointcutBeanName = parserContext.getReaderContext().registerWithGeneratedName(pointcutDefinition);
            }
            parserContext.registerComponent(new PointcutComponentDefinition(pointcutBeanName, pointcutDefinition, expression));
        }
        finally {
            this.parseState.pop();
        }
        return pointcutDefinition;
    }
    
    private Object parsePointcutProperty(final Element element, final ParserContext parserContext) {
        if (element.hasAttribute("pointcut") && element.hasAttribute("pointcut-ref")) {
            parserContext.getReaderContext().error("Cannot define both 'pointcut' and 'pointcut-ref' on <advisor> tag.", element, this.parseState.snapshot());
            return null;
        }
        if (element.hasAttribute("pointcut")) {
            final String expression = element.getAttribute("pointcut");
            final AbstractBeanDefinition pointcutDefinition = this.createPointcutDefinition(expression);
            pointcutDefinition.setSource(parserContext.extractSource(element));
            return pointcutDefinition;
        }
        if (!element.hasAttribute("pointcut-ref")) {
            parserContext.getReaderContext().error("Must define one of 'pointcut' or 'pointcut-ref' on <advisor> tag.", element, this.parseState.snapshot());
            return null;
        }
        final String pointcutRef = element.getAttribute("pointcut-ref");
        if (!StringUtils.hasText(pointcutRef)) {
            parserContext.getReaderContext().error("'pointcut-ref' attribute contains empty value.", element, this.parseState.snapshot());
            return null;
        }
        return pointcutRef;
    }
    
    protected AbstractBeanDefinition createPointcutDefinition(final String expression) {
        final RootBeanDefinition beanDefinition = new RootBeanDefinition(AspectJExpressionPointcut.class);
        beanDefinition.setScope("prototype");
        beanDefinition.setSynthetic(true);
        beanDefinition.getPropertyValues().add("expression", expression);
        return beanDefinition;
    }
}
