package com.showcase.search.config;

import com.showcase.search.web.CatalogIndexController;
import com.showcase.search.web.CatalogProductsController;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {CatalogProductsController.class,CatalogIndexController.class})
public class SwaggerConfig {
    // Regex patterns
    private static final String PRODUCTS = "/products";
    private static final String PRODUCTS_ALL = "/products.*";

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket eventLogApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(paths())
                .build()
                .pathMapping("/")
                .directModelSubstitute(DateTime.class, String.class)
                .directModelSubstitute(LocalDateTime.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, newArrayList(new ResponseMessageBuilder()
                        .code(500)
                        .message("An internal server error occurred")
                        .responseModel(new ModelRef("HttpErrorResponse"))
                        .build()));
    }

    /**
     * Return the ApiInfo object that defines the API
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("APN Products Search API Documentation")
                .description("Documentation pertaining to the APIs exposed by the Catalog Products Micro service")
                .version("1.0")
                .license("APN License")
                .build();
    }
    /**
     * Predicates for the paths
     * @return
     */
    private Predicate<String> paths() {
        return or(
                regex(PRODUCTS),
                regex(PRODUCTS_ALL));
    }

    /**
     * Define the authorization mechanism
     * @return
     */
    private ApiKey apiKey() {
        return new ApiKey("myKey", "api_key", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(regex("/anyPath.*"))
                .build();
    }

    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration(
                null,
                null,
                null,
                null,
                null,
                ApiKeyVehicle.HEADER,
                "x-Auth-Token",
                "," /*scope separator*/);
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(
                new SecurityReference("myKey", authorizationScopes));
    }
}
