package io.cronx.web.webconfig;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties;
import springfox.boot.starter.autoconfigure.SwaggerUiWebFluxConfiguration;
import springfox.boot.starter.autoconfigure.SwaggerUiWebMvcConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.configuration.OpenApiDocumentationConfiguration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

@Configuration
@EnableSwagger2
@EnableKnife4j
@EnableConfigurationProperties(SpringfoxConfigurationProperties.class)
@ConditionalOnProperty(value = "springfox.documentation.enabled", havingValue = "true", matchIfMissing = true)
@Import({ OpenApiDocumentationConfiguration.class, SpringDataRestConfiguration.class, BeanValidatorPluginsConfiguration.class, Swagger2DocumentationConfiguration.class,
          SwaggerUiWebFluxConfiguration.class, SwaggerUiWebMvcConfiguration.class })
@AutoConfigureAfter({ WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class })
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        final String scanPackage = "io.cronx.web.controller";

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage(scanPackage))
            .paths(PathSelectors.any())
            .build()
            .groupName("CronX Platform API Doc");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("CronX Platform API Manager")
            .contact(new Contact("cronx", "cronx.io", "contact@cronx.io"))
            .description("Welcome to CronX Platform")
            .termsOfServiceUrl("http://127.0.0.1:8555/")
            .version("1.0")
            .build();
    }

}
