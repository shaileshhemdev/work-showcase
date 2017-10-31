package com.showcase.search.service;

import com.showcase.search.exceptions.HttpServiceException;
import com.showcase.search.exceptions.HttpServiceExceptions;
import com.showcase.search.functions.PaginationFunctions;
import com.showcase.search.model.SearchResponse;
import com.google.common.collect.Lists;
import com.showcase.search.model.Constants;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.elasticsearch.search.suggest.SuggestBuilders.completionSuggestion;

@Service
@Slf4j
public class SimpleSearchService {
    @Autowired
    JestClient jestClient;

    /**
     * Execute the Simple Search Request
     * @param params The Query Parameters
     * @return
     */
    public SearchResponse executeSearchRequest(MultiValueMap<String, String> params) throws HttpServiceException {
        SearchSourceBuilder searchSourceBuilder = SearchFunctions.buildSimpleSearchQuery(params);

        log.debug("Printing the ES query {} ", searchSourceBuilder.toString());

        /** Extract the page number and page size */
        int pageNumber = Strings.isNullOrEmpty(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER))
                ? SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER_DEFAULT :
                Integer.parseInt(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER));
        int pageSize = Strings.isNullOrEmpty(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE))
                ? SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE_DEFAULT :
                Integer.parseInt(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE));

        /** Find the start position for this search */
        int startRecord = PaginationFunctions.calculateStartPosition(pageNumber, pageSize);

        /** Set the pagination attributes */
        searchSourceBuilder.from(startRecord).size(pageSize);

        /** Use default sort of descending */
        if (params.get(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT)==null || params.get(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT).isEmpty()) {
            searchSourceBuilder.sort(SearchConstants.PRODUCT_TIMESTAMP_FIELD, SortOrder.DESC);
        } else {
            List<String> sortOptions = params.get(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT);
            sortOptions.forEach( se -> {
                String [] sortElements = se.split(":");
                FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort(sortElements[0]);
                fieldSortBuilder.order(SortOrder.valueOf(sortElements[1]));
                searchSourceBuilder.sort(fieldSortBuilder);
            });
        }

        /** Prepare the ES REST Search Request */
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(Constants.INDEX_NAME)
                .addType(Constants.INDEX_TYPE)
                .build();

        /** Execute the search */
        try {
            JestResult result = jestClient.execute(search);
            return SearchFunctions.buildSearchResponse(result, pageNumber, pageSize);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HttpServiceExceptions.resourceServerException(e.getMessage());
        } finally {
            log.debug("Finished executing search for Audit logs");
        }

    }

    /**
     * Execute the Simple Search Request
     * @param params The Query Parameters
     * @return
     */
    public SearchResponse executeFullTextSearchRequest(MultiValueMap<String, String> params) throws HttpServiceException {
        SearchSourceBuilder searchSourceBuilder = SearchFunctions.buildFullTextQuery(params);

        log.debug("Printing the ES query {} ", searchSourceBuilder.toString());

        /** Extract the page number and page size */
        int pageNumber = Strings.isNullOrEmpty(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER))
                ? SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER_DEFAULT :
                Integer.parseInt(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER));
        int pageSize = Strings.isNullOrEmpty(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE))
                ? SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE_DEFAULT :
                Integer.parseInt(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE));

        /** Find the start position for this search */
        int startRecord = PaginationFunctions.calculateStartPosition(pageNumber, pageSize);

        /** Set the pagination attributes */
        searchSourceBuilder.from(startRecord).size(pageSize);

        /** Prepare the ES REST Search Request */
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(Constants.INDEX_NAME)
                .addType(Constants.INDEX_TYPE)
                .build();

        /** Execute the search */
        try {
            JestResult result = jestClient.execute(search);
            return SearchFunctions.buildSearchResponse(result, pageNumber, pageSize);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HttpServiceExceptions.resourceServerException(e.getMessage());
        } finally {
            log.debug("Finished executing search for Audit logs");
        }

    }

    /**
     * Execute the Suggest request
     * @param params The Query Parameters
     * @return
     */
    public SearchResponse executeSuggestRequest(MultiValueMap<String, String> params) throws HttpServiceException {
        /** Get the suggestion input */
        String input = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_SUGGEST);
        String category = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_SUGGEST_CATEGORY);
        String sellerId = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_SUGGEST_SELLER_ID);

        /** Check if category is specified */
        boolean isCategorySpecified = Strings.isNullOrEmpty(category) ||
                SearchConstants.PRODUCT_SEARCH_REQUEST_SUGGEST_CATEGORY_ALL.equalsIgnoreCase(category) ? false : true;

        /** Check if seller id is specified */
        boolean isSellerSpecified = Strings.isNullOrEmpty(sellerId) ? false : true;

        /** Extract the page number and page size */
        int pageSize = Strings.isNullOrEmpty(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE))
                ? SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE_DEFAULT :
                Integer.parseInt(params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE));

        /** Prepare the ES REST Search Request */
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /** Build the completion suggestion based on the input */
        CompletionSuggestionBuilder completionSuggestionBuilder = completionSuggestion(SearchConstants.PRODUCT_SUGGEST_RESULT)
                .field(SearchConstants.PRODUCT_SUGGEST_RESULT_FIELD)
                .text(input)
                .size(pageSize);

        /** Add context if specified */
        if (isCategorySpecified) {
            completionSuggestionBuilder.addContextField(SearchConstants.PRODUCT_SUGGEST_CONTEXT_FIELD_CATEGORIES,category);
        }
        if (isSellerSpecified) {
            completionSuggestionBuilder.addContextField(SearchConstants.PRODUCT_SUGGEST_CONTEXT_FIELD_SELLERS,sellerId);
        }

        /** Generate the suggestion search request */
        searchSourceBuilder.suggest().addSuggestion(completionSuggestionBuilder);
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(Constants.INDEX_NAME)
                .addType(Constants.INDEX_TYPE)
                .build();

        /** Execute the search */
        try {
            JestResult result = jestClient.execute(search);
            return SuggestFunctions.buildSuggestResponse(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HttpServiceExceptions.resourceServerException(e.getMessage());
        } finally {
            log.debug("Finished executing search for Audit logs");
        }

    }

    /**
     * Validate all the parameters for Search request
     * @param params
     * @return
     */
    public boolean validateRequest(MultiValueMap<String,String> params) throws HttpServiceException {
        boolean validationSuccess = true;
        List<ObjectError> validationErrors = Lists.newArrayList();

        /** Validate page number */
        String offsetString = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER);
        if (!Strings.isNullOrEmpty(offsetString)) {
            try {
                Integer.parseInt(offsetString);
            } catch (RuntimeException re) {
                validationSuccess = false;
                log.error(re.getMessage(),re);
                validationErrors.add(new ObjectError(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_NUMBER,"Invalid number"));
            }
        }


        /** Validate page size */
        String limitString = params.getFirst(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE);
        if (!Strings.isNullOrEmpty(limitString)) {
            try {
                Integer.parseInt(limitString);
            } catch (RuntimeException re) {
                validationSuccess = false;
                log.error(re.getMessage(),re);
                validationErrors.add(new ObjectError(SearchConstants.PRODUCT_SEARCH_REQUEST_PAGE_SIZE,"Invalid number"));
            }
        }

        /** Validate sort order */
        if (params.get(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT)!=null && !params.get(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT).isEmpty()) {
            List<String> sortOptions = params.get(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT);
            for (String se: sortOptions) {
                String [] sortElements = se.split(":");

                try {
                    SortOrder.valueOf(sortElements[1]);
                } catch (Exception e) {
                    validationSuccess = false;
                    log.error("Invalid sort order {} specified for sort field {} ", sortElements[1], sortElements[0]);
                    log.error(e.getMessage(),e);
                    validationErrors.add(new ObjectError(SearchConstants.PRODUCT_SEARCH_REQUEST_SORT,"Invalid sort order for one or more sort options"));
                    break;
                }
            }
        }

        if (validationSuccess == false) {
            throw HttpServiceExceptions.badDataException("Request did not pass validation checks",validationErrors);
        }

        return validationSuccess;
    }

}
