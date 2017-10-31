package com.showcase.search.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .and()
                .anonymous()
                .and()
                .servletApi()
                .and()
                .headers()
                .cacheControl()
                .and()
                .and()
                .authorizeRequests()
                .antMatchers(ServiceUrls.HEALTH,
                        ServiceUrls.STATUS,
                        ServiceUrls.INFO,
                        ServiceUrls.SWAGGER_UI,
                        ServiceUrls.SWAGGER_WEB_JARS,
                        ServiceUrls.SWAGGER_API_DOCS,
                        ServiceUrls.SWAGGER_RESOURCES,
                        ServiceUrls.SWAGGER_CONFIGURATION,
                        ServiceUrls.PRODUCT_RESOURCES,
                        ServiceUrls.PRODUCT_INDEX,
                        ServiceUrls.PRODUCT_INDEX_MAPPING,
                        ServiceUrls.PRODUCT_INDEX_SETTINGS,
                        ServiceUrls.PRODUCT_INDEX_ANALYZE,
                        ServiceUrls.PRODUCT_BOOTSTRAP,
                        ServiceUrls.PRODUCT_RESOURCE)
                .permitAll()
                .antMatchers("/**")
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .csrf()
                .disable();
    }

}
