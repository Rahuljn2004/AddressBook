package com.example.addressbook.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper bean.
 * This class is responsible for creating and configuring the ModelMapper bean
 * which is used for object mapping in the application.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and returns a ModelMapper bean.
     *
     * @return a new instance of ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }
}
