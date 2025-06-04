package com.grooveconductor.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.grooveconductor.infrastructure")
@EntityScan(basePackages = "com.grooveconductor.infrastructure")
public class InfrastructureTestConfiguration {

}
