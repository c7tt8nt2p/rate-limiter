package com.mycomp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfiguration implements WebMvcConfigurer {

    private final RootServiceInterceptor rootServiceInterceptor;

    @Autowired
    public AppConfiguration(RootServiceInterceptor rootServiceInterceptor) {
        this.rootServiceInterceptor = rootServiceInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rootServiceInterceptor);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
