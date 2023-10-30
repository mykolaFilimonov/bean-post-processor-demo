package com.example.beanpostprocessordemo;

import com.example.beanpostprocessordemo.annotation.Multiply;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MultiplyAnnotationBeanPostProcessor extends AbstractParameterAnnotationBeanPostProcessor {

    @Override
    protected boolean isEligible(Parameter parameter) {
        return parameter.isAnnotationPresent(Multiply.class) && parameter.getType().equals(Integer.class);
    }

    @Override
    protected void processArgs(Method method, Object[] arguments) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (isEligible(parameters[i])) {
                arguments[i] = ((Integer) arguments[i]) * 5;
            }
        }
    }
}
