package com.soodora.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.soodora.infrastructure")
@EntityScan(basePackages = "com.soodora.infrastructure")
public class InfrastructureTestConfiguration {

}
