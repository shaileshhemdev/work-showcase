package com.showcase.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchType {
    SUGGEST("suggest"),
    TERM("term");

    private String value;
}
