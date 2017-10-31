package com.showcase.search.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceUrls {

    /** Resource for Simple Search */
    public static final String SERVICE_CONTEXT = "/products" ;
    public static final String PRODUCT_RESOURCES = "/products" ;
    public static final String PRODUCT_RESOURCE_ID = "/{id}" ;
    public static final String PRODUCT_RESOURCE = PRODUCT_RESOURCES +  PRODUCT_RESOURCE_ID;
    public static final String PRODUCT_INDEX = PRODUCT_RESOURCES + "/index" ;
    public static final String PRODUCT_INDEX_MAPPING = PRODUCT_RESOURCES + "/index/_mapping" ;
    public static final String PRODUCT_INDEX_SETTINGS = PRODUCT_RESOURCES + "/index/_settings" ;
    public static final String PRODUCT_INDEX_ANALYZE = PRODUCT_RESOURCES + "/index/_analyze" ;
    public static final String PRODUCT_BOOTSTRAP = PRODUCT_RESOURCES + "/init_data" ;

    /** unprotected endpoints */
    public static final String STATUS = "/status";
    public static final String HEALTH = "/health";
    public static final String INFO = "/info";

    /** Documentation URLs */
    public static final String SWAGGER_UI = "/swagger-ui.html";
    public static final String SWAGGER_WEB_JARS = "/webjars/**";
    public static final String SWAGGER_API_DOCS = "/v2/api-docs";
    public static final String SWAGGER_RESOURCES = "/swagger-resources/**";
    public static final String SWAGGER_CONFIGURATION = "/configuration/**";

}
