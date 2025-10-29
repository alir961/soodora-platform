package com.soodora.api;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@ComponentScan(basePackages = {"com.soodora"})
@EntityScan(basePackages = {"com.soodora.infrastructure"})
@EnableJpaRepositories(basePackages = {"com.soodora.infrastructure"})
@PropertySource("classpath:application-persistence.properties")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.timeZone(TimeZone.getTimeZone("Europe/Paris"));
    }

}
