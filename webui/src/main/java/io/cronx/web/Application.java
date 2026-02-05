package io.cronx.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.cronx.web.util.I18nUtil;
import io.cronx.web.util.UnifiedPostConstructUtils;
import io.cronx.web.webconfig.ConsoleConfig;
import io.cronx.web.webconfig.CronXFlywayInit;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;

@Slf4j
@EnableCaching
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, FlywayAutoConfiguration.class })
@ConditionalOnClass(SpringfoxWebMvcConfiguration.class)
@MapperScan("io.cronx.web.mapper")
@ComponentScan({ "io.cronx.web.*" })
public class Application implements WebMvcConfigurer {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        context.getBean(CronXFlywayInit.class).doUpgrade();

        log.info("Init default locale.");
        ConsoleConfig config = context.getBean(ConsoleConfig.class);
        if (config.getI18nDefaultLocale() != null) {
            I18nUtil.setDefaultLocale(config.getI18nDefaultLocale());
        }

        UnifiedPostConstructUtils.doPostConstruct(context);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
