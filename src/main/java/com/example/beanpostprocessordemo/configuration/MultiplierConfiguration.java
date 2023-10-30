package com.example.beanpostprocessordemo.configuration;

import com.example.beanpostprocessordemo.MultiplyAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;

public class MultiplierConfiguration {

    @Bean
    public MultiplyAnnotationBeanPostProcessor getMultiplyAnnotationBeanPostProcessor() {
        return new MultiplyAnnotationBeanPostProcessor();
    }
}
