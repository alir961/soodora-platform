package com.grooveconductor.internal;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.grooveconductor.internal.repository")
@EntityScan(basePackages = "com.grooveconductor.internal.entity")
public class InfrastructureTestConfiguration {

}
