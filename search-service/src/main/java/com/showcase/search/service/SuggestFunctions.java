package com.showcase.search.service;

import com.showcase.search.model.Product;
import com.showcase.search.model.ProductDeserializer;
import com.google.common.collect.Lists;
import com.google.gson.*;
import io.searchbox.client.JestResult;
import lombok.extern.slf4j.Slf4j;
import com.showcase.search.model.SearchResponse;

import java.util.List;
import static com.showcase.search.service.SearchConstants.*;

@Slf4j
public final class SuggestFunctions {

    /**
     * Function that will construct a Suggest Response from a JestResult
     * @param <JestResult> The actual JestClient result
     */
    interface BuildSuggestResponse<JestResult, SearchResponse> {
        /** */
        SearchResponse apply(JestResult jestResult);
    }


    /**
     * Build the SuggestResponse from the suggest result
     * @param resultParam The actual JestClient result
     * @return
     */
    public static SearchResponse buildSuggestResponse(JestResult resultParam) {
        BuildSuggestResponse<JestResult,SearchResponse> suggestResponseResult = (result) -> {
            /** Build the list of Products */
            List<Product> suggestedProducts = Lists.newArrayList();

            /** Get the JsonObject for the result */
            JsonObject jsonObject = result.getJsonObject();

            /** Obtain the hits and the totals */
            JsonObject suggestResponseJson = jsonObject.getAsJsonObject(PRODUCT_SUGGEST_RESULT);
            if (suggestResponseJson == null) {
                return new SearchResponse(0, 0,false, suggestedProducts);
            } else {
                JsonArray suggestResultsJson = suggestResponseJson.getAsJsonArray(PRODUCT_SUGGEST_RESULT_FIELD);
                if (suggestResultsJson == null) {
                    return new SearchResponse(0, 0,false, suggestedProducts);
                } else {
                    for (JsonElement jsonElement : suggestResultsJson) {
                        /** Register custom deserializer to have more control */
                        GsonBuilder builder = new GsonBuilder();
                        builder.registerTypeAdapter(Product.class, new ProductDeserializer());
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        JsonArray options = jsonElement.getAsJsonObject().getAsJsonArray(PRODUCT_SUGGEST_OPTIONS);
                        if (options != null) {
                            for (JsonElement option : options) {
                                JsonObject optionsAsJson = (JsonObject) option;
                                JsonObject productAsJson = optionsAsJson.getAsJsonObject(PRODUCT_SUGGEST_OPTIONS_SOURCE);
                                suggestedProducts.add(gson.fromJson(productAsJson,Product.class));
                            }
                        }
                    }
                }
            }

            return new SearchResponse(0, suggestedProducts.size(),false, suggestedProducts);
        };

        return suggestResponseResult.apply(resultParam);
    }


}