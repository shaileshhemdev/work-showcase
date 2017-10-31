package com.showcase.search.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@ApiModel(value="SearchResponse", description="Resource representing a search response from a catalog search request")
public class SearchResponse {
    /** Current page number */
    @ApiModelProperty(dataType = "int", example = "0", notes = "Gives you the current page number. This is 0 based which means 1st page has a value of 0")
    Integer pageNumber;

    /** Total number of matching results */
    @ApiModelProperty(dataType = "int", example = "251", notes = "Gives you the total number of records irrespective of page size. In case of suggestion searches since we don't send pages this corresponds to the number of records sent")
    Integer totalRecords;

    /** Boolean result to indicate whether */
    @ApiModelProperty(dataType = "boolean", example = "false", notes = "Indicates if there are more pages. Will always be false for suggestion searches")
    Boolean hasMore;

    /** Event Logs */
    @ApiModelProperty(notes = "List of product for the current page matching the search criteria")
    List<Product> items;
}
