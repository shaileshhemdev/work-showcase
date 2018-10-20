package com.showcase.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchType {
    SUGGEST("suggest"),
    TERM("term"),
    FULL_TEXT("ft");

    private String value;
}
