package com.showcase.search.service;

public class SearchConstants {
    /** Parameter carrying the indication for the type of search */
    static final String PRODUCT_SEARCH_REQUEST_TYPE = "searchType";

    /** Name of the Timestamp field */
    static final String PRODUCT_TIMESTAMP_FIELD = "timestamp";

    /** Object representing the hits  */
    static final String PRODUCT_SEARCH_RESULT_HITS = "hits";

    /** Attribute within the result that has the total results */
    static final String PRODUCT_SEARCH_RESULT_TOTAL = "total";

    /** Constant carrying the current page number default */
    static final Integer PRODUCT_SEARCH_REQUEST_PAGE_NUMBER_DEFAULT = 0;

    /** Constant carrying the page size default */
    static final Integer PRODUCT_SEARCH_REQUEST_PAGE_SIZE_DEFAULT = 25;

    /** Format for the Search Request */
    static final String PRODUCT_SEARCH_REQUEST_DATE_FORMAT = "epoch_millis";

    /** Parameter carrying the current page number requested */
    static final String PRODUCT_SEARCH_REQUEST_PAGE_NUMBER = "offset";

    /** Parameter carrying the page size */
    static final String PRODUCT_SEARCH_REQUEST_PAGE_SIZE = "limit";

    /** Parameter carrying the sort */
    static final String PRODUCT_SEARCH_REQUEST_SORT = "sortOptions";

    /** Parameter carrying the input string for suggestion search */
    static final String PRODUCT_SEARCH_REQUEST_SUGGEST = "input";

    /** Parameter carrying the input string for suggestion search */
    static final String PRODUCT_SEARCH_REQUEST_APPLY_FUZZINESS = "applyFuzziness";

    /** Parameter carrying the category for the term or full text search */
    static final String PRODUCT_SEARCH_REQUEST_CATEGORY_ID = "categories.id";

    /** Parameter carrying the seller Id for the term or full text search */
    static final String PRODUCT_SEARCH_REQUEST_SELLERS_ID = "sellers.id";

    /** Parameter carrying the category for the suggestion */
    static final String PRODUCT_SEARCH_REQUEST_SUGGEST_CATEGORY = "category";

    /** Parameter carrying the seller Id for the suggestion */
    static final String PRODUCT_SEARCH_REQUEST_SUGGEST_SELLER_ID = "sellerId";

    /** Parameter specifying title*/
    static final String PRODUCT_SEARCH_REQUEST_MPN_PARAM = "mpn";

    /** Parameter specifying title*/
    static final String PRODUCT_SEARCH_REQUEST_TITLE_PARAM = "title";

    /** Parameter specifying brand */
    static final String PRODUCT_SEARCH_REQUEST_BRAND_PARAM = "brand";

    /** Parameter specifying description */
    static final String PRODUCT_SEARCH_REQUEST_DESC_PARAM = "description";

    /** Parameter indicating it is ALL category */
    static final String PRODUCT_SEARCH_REQUEST_SUGGEST_CATEGORY_ALL = "ALL";

    /** Object representing the suggest results  */
    static final String PRODUCT_SUGGEST_RESULT = "suggest";

    /** Field inside the suggest */
    static final String PRODUCT_SUGGEST_RESULT_FIELD = "suggest";

    /** Suggestion Context Field */
    static final String PRODUCT_SUGGEST_CONTEXT_FIELD_CATEGORIES = "productCategories";

    /** Suggestion Context Field */
    static final String PRODUCT_SUGGEST_CONTEXT_FIELD_SELLERS = "sellerIds";

    /** Available options */
    static final String PRODUCT_SUGGEST_OPTIONS = "options";

    /** SOURCE */
    static final String PRODUCT_SUGGEST_OPTIONS_SOURCE = "_source";
}
