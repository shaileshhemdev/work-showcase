package com.showcase.search.service;

import com.showcase.search.functions.PaginationFunctions;
import com.showcase.search.model.Product;
import com.showcase.search.model.ProductDeserializer;
import com.showcase.search.model.SearchResponse;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.MultiValueMap;

import java.util.List;

public final class SearchFunctions {
    /**
     * This function will calculate the start position
     * @param <Params> The various parameters to form the query
     * @param <SearchSourceBuilder> The start position
     */
    interface BuildSimpleSearchQuery<Params, SearchSourceBuilder> {
        /** */
        SearchSourceBuilder apply (Params params);
    }

    /**
     * This function will calculate the start position
     * @param <Params> The various parameters to form the query
     * @param <SearchSourceBuilder> The start position
     */
    interface BuildFullTextSearchQuery<Params, SearchSourceBuilder> {
        /** */
        SearchSourceBuilder apply (Params params);
    }

    /**
     * Function that will construct a Search Response from a JestResult
     * @param <JestResult> The actual JestClient result
     * @param <PageNumber> The current page number
     * @param <PageSize> The size of each page
     * @param <SearchResponse> The response encapsulated in an SearchResponse instance
     */
    interface BuildSearchResponse<JestResult,PageNumber, PageSize, SearchResponse> {
        /** */
        SearchResponse apply (JestResult jestResult,PageNumber pageNumber, PageSize pageSize);
    }

    /**
     * Build the SearchResponse from the search result
     * @param resultParam The actual JestClient result
     * @param pageNumberParam The current page number
     * @param pageSizeParam The size of each page
     * @return
     */
    public static SearchResponse buildSearchResponse(JestResult resultParam, int pageNumberParam, int pageSizeParam) {
        BuildSearchResponse<JestResult,Integer, Integer, SearchResponse> searchResponseResult = (result,pageNumber, pageSize) -> {
            /** Get the JsonObject for the result */
            JsonObject jsonObject = result.getJsonObject();

            /** Obtain the hits and the totals */
            JsonObject hits = jsonObject.getAsJsonObject(SearchConstants.PRODUCT_SEARCH_RESULT_HITS);
            if (hits == null) {
                return new SearchResponse(pageNumber,0 ,false, Lists.newArrayList());
            }

            /** Calculate total number of records */
            Integer totalRecords = hits.getAsJsonPrimitive(SearchConstants.PRODUCT_SEARCH_RESULT_TOTAL).getAsInt();

            /** Determine if there are more pages */
            boolean hasMore = PaginationFunctions.hasMorePages(totalRecords, pageNumber, pageSize);

            /** Extract the actual logs */
            List<Product> products = Lists.newArrayList();
            List<String> productsJsonString = result.getSourceAsStringList();

            /** Register custom deserializer */
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Product.class, new ProductDeserializer());
            builder.setPrettyPrinting();
            Gson gson = builder.create();

            for (String jsonString: productsJsonString) {
                products.add(gson.fromJson(jsonString, Product.class));
            }

            /** Build and return the response */
            SearchResponse searchResponse = new SearchResponse(pageNumber,totalRecords ,hasMore, products);
            return searchResponse;
        };

        return searchResponseResult.apply(resultParam,pageNumberParam,pageSizeParam);
    }

    /**
     * For simple search we will build the search predicate from query parameters
     * @param paramsPassed The parameters to search on
     * @return
     */
    public static SearchSourceBuilder buildFullTextQuery(MultiValueMap<String, String> paramsPassed) {
        BuildFullTextSearchQuery <MultiValueMap<String, String>,SearchSourceBuilder> buildSimpleSearchQueryResult= (params) -> {
            /** Initialize a Search Source Builder */
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            /** Get the suggestion input */
            String input = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_SUGGEST);
            Boolean isApplyFuzziness = Boolean.parseBoolean(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_APPLY_FUZZINESS));
            String categoryId = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_CATEGORY_ID);
            String sellerId = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_SELLERS_ID);

            /** Initialize a Boolean Query */
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(input, SearchConstants.PRODUCT_SEARCH_REQUEST_TITLE_PARAM,
                    SearchConstants.PRODUCT_SEARCH_REQUEST_BRAND_PARAM, SearchConstants.PRODUCT_SEARCH_REQUEST_DESC_PARAM, SearchConstants.PRODUCT_SEARCH_REQUEST_MPN_PARAM)
                    .operator(MatchQueryBuilder.Operator.AND);
                   // .analyzer(Constants.SEARCH_ANALYZER);
            if (isApplyFuzziness) {
                multiMatchQueryBuilder.fuzziness(Fuzziness.AUTO);
            }
            boolQueryBuilder.must(multiMatchQueryBuilder);

            /** Add category id to the match if it has been provided */
            if (!Strings.isNullOrEmpty(categoryId)) {
                MatchQueryBuilder categoryMatchBuilder = QueryBuilders
                        .matchQuery(SearchConstants.PRODUCT_SEARCH_REQUEST_CATEGORY_ID, categoryId);
                boolQueryBuilder.must(categoryMatchBuilder);
            }

            /** Add seller id to the match if it has been provided */
            if (!Strings.isNullOrEmpty(sellerId)) {
                MatchQueryBuilder sellerMatchBuilder = QueryBuilders
                        .matchQuery(SearchConstants.PRODUCT_SEARCH_REQUEST_SELLERS_ID, sellerId);
                boolQueryBuilder.must(sellerMatchBuilder);
            }

            /** Assemble the boolean query */
            searchSourceBuilder.query(boolQueryBuilder);

            return searchSourceBuilder;
        };


        return buildSimpleSearchQueryResult.apply(paramsPassed);
    }

    /**
     * Match for specific terms. Always sort on relevance
     *
     * @param paramsPassed
     * @return
     */
    public static SearchSourceBuilder buildSimpleSearchQuery(MultiValueMap<String, String> paramsPassed) {
        BuildSimpleSearchQuery <MultiValueMap<String, String>,SearchSourceBuilder> buildSimpleSearchQueryResult= (params) -> {
            /** Initialize a Search Source Builder */
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            /** Check if fuzziness is to be applied */
            Boolean isApplyFuzziness = Boolean.parseBoolean(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_APPLY_FUZZINESS));

            /** Initialize a Boolean Query */
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            /** For the remaining parameters make use of match search */
            for (String key : params.keySet()) {
                if (!key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER) && !key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE)
                        && !key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT) && !key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_TYPE)
                        && !key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_APPLY_FUZZINESS)) {
                    List<String> values = params.get(key);

                    for (String value: values) {
                        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                                .matchQuery(key, value)
                                .operator(MatchQueryBuilder.Operator.AND);

                        if (key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_TITLE_PARAM) || key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_BRAND_PARAM)
                                || key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_DESC_PARAM) || key.equals(SearchConstants.PRODUCT_SEARCH_REQUEST_MPN_PARAM)) {

                            matchQueryBuilder.type(MatchQueryBuilder.Type.PHRASE);
                            if (isApplyFuzziness) {
                                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                            }
                        }

                        boolQueryBuilder.must(matchQueryBuilder);
                    }
                }
            }

            /** Assemble the boolean query */
            searchSourceBuilder.query(boolQueryBuilder);

            return searchSourceBuilder;
        };


        return buildSimpleSearchQueryResult.apply(paramsPassed);
    }
}