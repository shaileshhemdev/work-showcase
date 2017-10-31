package com.showcase.search.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.searchbox.annotations.JestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel(value="CatalogProduct", description="Resource representing a product in the APN catalog")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @JestId
    @ApiModelProperty(required = true, example = "1273783", notes = "Internal unique identifier")
    private String apin;

    @ApiModelProperty(required = true, example = "XYZ-1234", notes = "Manufacturer Part Number")
    private String mpn;

    @ApiModelProperty(example = "Game of Thrones", notes = "Title for a product")
    private String title;

    @ApiModelProperty(example = "Hardcover", notes = "Brand", allowEmptyValue = true)
    private String brand;

    @ApiModelProperty(example = "Song of Fire and Ice", notes = "Description for a product entered in APN", allowEmptyValue = true)
    private String description;

    @JsonProperty
    @ApiModelProperty(notes = "Time when product was updated", allowEmptyValue = true, readOnly = true)
    private Date timestamp;

    /** Strings to make a suggestion */
    @JsonIgnore
    private Suggest suggest;

    /** Map for any additional attributes */
    @ApiModelProperty(notes = "Any additional attributes as string key value pairs", allowEmptyValue = true)
    @JsonProperty
    private Map<String, Object> attributes = Maps.newConcurrentMap();

    /** Categories for the products */
    @ApiModelProperty(notes = "One or more categories under which this product is categorized")
    @JsonProperty
    private List<Category> categories = Lists.newArrayList();

    /** Sellers that sell this product */
    @ApiModelProperty(notes = "One or more sellers selling this product", required = true)
    @JsonProperty
    private List<Seller> sellers = Lists.newArrayList();

}
