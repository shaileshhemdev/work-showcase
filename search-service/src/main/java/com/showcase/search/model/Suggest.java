package com.showcase.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Suggest {
    /** List of strings to suggest */
    private List<String> input;

    /** Context object */
    private Context contexts;
}
