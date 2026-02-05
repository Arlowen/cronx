package io.cronx.web.webconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.cronx.web.interceptor.SessionManager;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public SessionManager sessionManager() {
        return new SessionManager();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionManager());
    }
}
