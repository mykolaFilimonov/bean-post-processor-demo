package com.example.beanpostprocessordemo.postprocessor.configuration;

import com.example.beanpostprocessordemo.annotation.EnableMultiplier;
import com.example.beanpostprocessordemo.annotation.EnableStringTrimming;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableStringTrimming
@EnableMultiplier
@ComponentScan(basePackages = "com.example.beanpostprocessordemo.postprocessor.service")
public class TrimmingEnabledConfig {

}
