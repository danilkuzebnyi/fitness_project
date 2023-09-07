package org.danylo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class ResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**", "/trainers/images/**")
                .addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("static/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("static/css/**")
                .addResourceLocations("classpath:/static/css/");
    }
}

