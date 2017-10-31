package com.showcase.search.model;


import lombok.Data;

import java.util.List;

@Data
public class ProductsRequest {
    List<Product> products;
}
