package com.example.beanpostprocessordemo.postprocessor.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "com.example.beanpostprocessordemo.postprocessor.service")
public class TrimmingNotEnabledConfig {

}
