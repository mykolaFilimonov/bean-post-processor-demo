package com.example.beanpostprocessordemo.configuration;

import com.example.beanpostprocessordemo.TrimmedAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;

public class StringTrimmingConfiguration {

  @Bean
  public TrimmedAnnotationBeanPostProcessor getTrimmedAnnotationBeanPostProcessor() {
    return new TrimmedAnnotationBeanPostProcessor();
  }

}
