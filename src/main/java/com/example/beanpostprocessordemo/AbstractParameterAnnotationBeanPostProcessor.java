package com.example.beanpostprocessordemo;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public abstract class AbstractParameterAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (isAnnotationAppear(bean)) {
            if (bean instanceof Advised advised) {
                advised.addAdvice((MethodInterceptor) (methodInvocation) -> {
                    processArgs(methodInvocation.getMethod(), methodInvocation.getArguments());
                    return methodInvocation.proceed();
                });
            } else {
                ProxyFactory proxyFactory = new ProxyFactory(bean);
                proxyFactory.addAdvice((MethodInterceptor) (methodInvocation) -> {
                    processArgs(methodInvocation.getMethod(), methodInvocation.getArguments());
                    return methodInvocation.proceed();
                });
                ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
                return proxyFactory.getProxy(classLoader);
            }
        }
        return bean;
    }

    private boolean isAnnotationAppear(Object bean) {
        return Arrays.stream(getTagetClass(bean).getDeclaredMethods())
                .flatMap(method -> Arrays.stream(method.getParameters()))
                .anyMatch(this::isEligible);
    }

    private Class<?> getTagetClass(Object bean) {
        if (bean instanceof Advised advised) {
            return advised.getTargetClass();
        }
        return bean.getClass();
    }

    protected abstract boolean isEligible(Parameter parameter);

    protected abstract void processArgs(Method method, Object[] arguments);
}
