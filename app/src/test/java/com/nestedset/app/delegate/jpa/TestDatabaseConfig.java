package com.nestedset.app.delegate.jpa;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = "com.nestedset.app.delegate.jpa")
public class TestDatabaseConfig {
}
