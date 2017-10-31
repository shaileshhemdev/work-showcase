package com.showcase.search.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Context {
    /** Associated Categories */
    private List<String> productCategories;

    /** List of strings to suggest */
    private List<String> sellerIds;
}
