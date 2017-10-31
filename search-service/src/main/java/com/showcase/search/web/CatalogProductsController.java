package com.showcase.search.web;

import com.showcase.search.exceptions.HttpServiceException;
import com.showcase.search.exceptions.HttpServiceExceptions;
import com.showcase.search.model.Product;
import com.showcase.search.model.ProductsRequest;
import com.showcase.search.model.SearchResponse;
import com.showcase.search.service.IndexService;
import com.showcase.search.service.ProductService;
import com.showcase.search.service.SimpleSearchService;
import com.google.common.base.Strings;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.showcase.search.config.ServiceUrls.PRODUCT_RESOURCE;
import static com.showcase.search.config.ServiceUrls.PRODUCT_RESOURCES;

@RestController
@Api(description = "Service to interact with Catalog ES repository", tags = "Product Catalog Micro Service")
public class CatalogProductsController {
    @Autowired
    SimpleSearchService simpleSearchService;

    @Autowired
    IndexService indexService;

    @Autowired
    ProductService productService;

    @RequestMapping(method = RequestMethod.GET, value = PRODUCT_RESOURCES, params = "searchType=term")
    @ApiOperation(value = "ProductSearchService", notes = "Auto Completion and Regular Term Searches for products. Suggestion searches are not paginated since they return all possible options up to a max of 25. Term based searches are paginated",
            produces = "application/json", consumes = "application/json", httpMethod = "GET", nickname = "searchProducts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchType", value = "Indicates the type of search to perform, suggest indicates Auto Completion suggestion search whereas term means a basic term based search and ft means full text", dataType = "string", paramType = "query", allowableValues = "suggest,term,ft", required = true),
            @ApiImplicitParam(name = "input", value = "Input string for suggestion or full text search. Only supported and required when searchType is suggest or ft", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "applyFuzziness", value = "Input string that determines if fuzziness is to be applied. Default false. Only supported when searchType is term or ft", dataType = "boolean", paramType = "query", allowableValues = "true,false"),
            @ApiImplicitParam(name = "category", value = "Category of the product. To be used to filter searches for suggest searches by category", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerId", value = "Seller Id to limit the search for a seller. To be used to filter searches for suggest searches by seller", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "mpn", value = "Manufacturer Part Number. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "title", value = "Title for a product. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "brand", value = "Product Brand. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellers.sku", value = "Seller Sku. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellers.id", value = "Seller Id. Only supported for searchType=term or searchType=ft. Ensures that search is performed for the given seller", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "categories.id", value = "Category Id. Only supported for searchType=term or searchType=ft. Ensures that search is performed for the given category", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "offset", value = "0 based page number. Only supported for searchType=term", dataType = "integer", paramType = "query", required = false, example = "1"),
            @ApiImplicitParam(name = "limit", value = "Number of records per page with a max of 100. Only supported for searchType=term", dataType = "integer", paramType = "query", required = false, example = "25")
    })
    public SearchResponse searchProducts(@RequestParam @ApiParam(value = "params", access = "internal", required = false, hidden = true)
                                             MultiValueMap<String, String> params) throws HttpServiceException {
        /** Validate the service */
        simpleSearchService.validateRequest(params) ;

        /** Execute the search and return results */
        return simpleSearchService.executeSearchRequest(params);
    }

    @RequestMapping(method = RequestMethod.GET, value = PRODUCT_RESOURCES, params = "searchType=ft")
    @ApiOperation(value = "ProductSearchService", notes = "Auto Completion and Regular Term Searches for products. Suggestion searches are not paginated since they return all possible options up to a max of 25. Term based searches are paginated",
            produces = "application/json", consumes = "application/json", httpMethod = "GET", nickname = "searchProducts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchType", value = "Indicates the type of search to perform, suggest indicates Auto Completion suggestion search whereas term means a basic term based search and ft means full text", dataType = "string", paramType = "query", allowableValues = "suggest,term,ft", required = true),
            @ApiImplicitParam(name = "input", value = "Input string for suggestion or full text search. Only supported and required when searchType is suggest or ft", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "applyFuzziness", value = "Input string that determines if fuzziness is to be applied. Default false. Only supported when searchType is term or ft", dataType = "boolean", paramType = "query", allowableValues = "true,false"),
            @ApiImplicitParam(name = "category", value = "Category of the product. To be used to filter searches for suggest searches by category", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerId", value = "Seller Id to limit the search for a seller. To be used to filter searches for suggest searches by seller", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "mpn", value = "Manufacturer Part Number. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "title", value = "Title for a product. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "brand", value = "Product Brand. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellers.sku", value = "Seller Sku. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellers.id", value = "Seller Id. Only supported for searchType=term or searchType=ft. Ensures that search is performed for the given seller", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "categories.id", value = "Category Id. Only supported for searchType=term or searchType=ft. Ensures that search is performed for the given category", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "offset", value = "0 based page number. Only supported for searchType=term", dataType = "integer", paramType = "query", required = false, example = "1"),
            @ApiImplicitParam(name = "limit", value = "Number of records per page with a max of 100. Only supported for searchType=term", dataType = "integer", paramType = "query", required = false, example = "25")
    })
    public SearchResponse performFullTextSearch(@RequestParam @ApiParam(value = "params", access = "internal", required = false, hidden = true)
                                         MultiValueMap<String, String> params) throws HttpServiceException {
        /** Validate the service */
        simpleSearchService.validateRequest(params) ;

        /** Execute the search and return results */
        return simpleSearchService.executeFullTextSearchRequest(params);
    }

    @RequestMapping(method = RequestMethod.GET, value = PRODUCT_RESOURCES, params = "searchType=suggest")
    @ApiOperation(value = "ProductSearchService", notes = "Auto Completion and Regular Term Searches for products. Suggestion searches are not paginated since they return all possible options up to a max of 25. Term based searches are paginated",
            produces = "application/json", consumes = "application/json", httpMethod = "GET", nickname = "searchProducts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchType", value = "Indicates the type of search to perform, suggest indicates Auto Completion suggestion search whereas term means a basic term based search and ft means full text", dataType = "string", paramType = "query", allowableValues = "suggest,term,ft", required = true),
            @ApiImplicitParam(name = "input", value = "Input string for suggestion or full text search. Only supported and required when searchType is suggest or ft", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "applyFuzziness", value = "Input string that determines if fuzziness is to be applied. Default false. Only supported when searchType is term or ft", dataType = "boolean", paramType = "query", allowableValues = "true,false"),
            @ApiImplicitParam(name = "category", value = "Category of the product. To be used to filter searches for suggest searches by category", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sellerId", value = "Seller Id to limit the search for a seller. To be used to filter searches for suggest searches by seller", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "mpn", value = "Manufacturer Part Number. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "title", value = "Title for a product. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "brand", value = "Product Brand. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellers.sku", value = "Seller Sku. Only supported for searchType=term", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "sellers.id", value = "Seller Id. Only supported for searchType=term or searchType=ft. Ensures that search is performed for the given seller", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "categories.id", value = "Category Id. Only supported for searchType=term or searchType=ft. Ensures that search is performed for the given category", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "offset", value = "0 based page number. Only supported for searchType=term", dataType = "integer", paramType = "query", required = false, example = "1"),
            @ApiImplicitParam(name = "limit", value = "Number of records per page with a max of 100. Only supported for searchType=term", dataType = "integer", paramType = "query", required = false, example = "25")
    })
    public SearchResponse suggestSearch(@RequestParam @ApiParam(value = "params", access="internal", required=false, hidden = true)
                                  MultiValueMap<String, String> params) throws HttpServiceException {
       return simpleSearchService.executeSuggestRequest(params);
    }

    @RequestMapping(method = RequestMethod.POST, value = PRODUCT_RESOURCES)
    @ApiOperation(value = "ProductUpsertService", notes = "Upsert i.e. add or update one or more products depending on whether that product exists in Elastic Search",
            produces = "application/json", consumes = "application/json", httpMethod = "POST", nickname = "AddProducts")
    public void addProducts(@RequestBody ProductsRequest productsRequest) throws HttpServiceException {
        List<Product> productList = productsRequest.getProducts();
        if (!productList.isEmpty()) {
            productService.bulkUpsertProducts(productList);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE , value = PRODUCT_RESOURCE )
    @ApiOperation(value = "ProductDeleteService", notes = "Deletes a product if it exists",
            produces = "application/json", consumes = "application/json", httpMethod = "DELETE", nickname = "DeleteProduct")
    public void deleteProduct(@PathVariable("apin") String apin) throws HttpServiceException {
        if (!Strings.isNullOrEmpty(apin)) {
            productService.deleteProduct(apin);
        } else {
            throw HttpServiceExceptions.badDataException("APIN is mandatory for deleting a product");
        }
    }

    @RequestMapping(method = RequestMethod.GET , value = PRODUCT_RESOURCE )
    @ApiOperation(value = "ProductDetailsService", notes = "Gets details of a product if it exists",
            produces = "application/json", consumes = "application/json", httpMethod = "GET", nickname = "GetProduct")
    public Product getProduct(@PathVariable("apin") String apin) throws HttpServiceException {
        if (!Strings.isNullOrEmpty(apin)) {
            return productService.getProduct(apin);
        } else {
            throw HttpServiceExceptions.badDataException("APIN is mandatory for getting details");
        }
    }
}
