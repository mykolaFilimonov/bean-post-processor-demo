package com.example.beanpostprocessordemo;

import com.example.beanpostprocessordemo.annotation.EnableStringTrimming;
import com.example.beanpostprocessordemo.annotation.Trimmed;
import com.example.beanpostprocessordemo.configuration.StringTrimmingConfiguration;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * This is processor class implements {@link BeanPostProcessor}, looks for a beans where method parameters are marked with {@link Trimmed}
 * annotation, creates proxy of them, overrides methods and trims all {@link String} arguments marked with {@link Trimmed}. For example if
 * there is a string " Java   " as an input parameter it has to be automatically trimmed to "Java" if parameter is marked with
 * {@link Trimmed} annotation.
 * <p>
 * <p>
 * Note! This bean is not marked as a {@link Component} to avoid automatic scanning, instead it should be created in
 * {@link StringTrimmingConfiguration} class which can be imported to a {@link Configuration} class by annotation
 * {@link EnableStringTrimming}
 */
public class TrimmedAnnotationBeanPostProcessor extends AbstractParameterAnnotationBeanPostProcessor {

    @Override
    protected boolean isEligible(Parameter parameter) {
        return parameter.isAnnotationPresent(Trimmed.class) && parameter.getType().equals(String.class);
    }

    @Override
    protected void processArgs(Method method, Object[] arguments) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (isEligible(parameters[i])) {
                arguments[i] = ((String) arguments[i]).trim();
            }
        }
    }
}
