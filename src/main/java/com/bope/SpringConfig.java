package com.bope;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.bope")
@PropertySource(value = "token.properties")
public class SpringConfig {
}
