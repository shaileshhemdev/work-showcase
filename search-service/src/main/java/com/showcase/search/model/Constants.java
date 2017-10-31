package com.showcase.search.model;

public class Constants {
    /** Name of the index in the Event Log Cluster */
    public static final String INDEX_NAME = "catalog";

    /** Document Type within the ES Index  */
    public static final String INDEX_TYPE = "product";

    /** Analyzers */
    public static final String INDEX_ANALYZER = "my_analyzer";
    public static final String SEARCH_ANALYZER = "my_search_analyzer";

    /** Standard Fields for event logs */
    static final String APIN = "apin";
    static final String TIMESTAMP = "timestamp";
    static final String MPN = "mpn";
    static final String TITLE = "title";
    static final String BRAND = "brand";
    static final String DESCRIPTION = "description";
    static final String CATEGORIES = "categories";
    static final String SELLERS = "sellers";
    static final String SUGGEST = "suggest";
}
