package com.showcase.search.model;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.*;
import static com.showcase.search.model.Constants.*;

@Slf4j
public class ProductDeserializer implements JsonDeserializer<Product> {
    static List<String> standardFieldNames = Lists.newArrayList();

    static {
        standardFieldNames.add(APIN);
        standardFieldNames.add(MPN);
        standardFieldNames.add(TITLE);
        standardFieldNames.add(BRAND);
        standardFieldNames.add(DESCRIPTION);
        standardFieldNames.add(TIMESTAMP);
        standardFieldNames.add(SELLERS);
        standardFieldNames.add(CATEGORIES);
        standardFieldNames.add(SUGGEST);
    }

    @Override
    public Product deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        /** Get the object */
        JsonObject productJson = jsonElement.getAsJsonObject();

        /** Get the basic fields */
        final String apin = Objects.isNull(productJson.get(APIN)) || productJson.get(APIN).isJsonNull() || !productJson.get(APIN).isJsonPrimitive() ? null
                : productJson.get(APIN).getAsString();
        final String mpn = Objects.isNull(productJson.get(MPN)) || productJson.get(MPN).isJsonNull() || !productJson.get(MPN).isJsonPrimitive() ? null
                : productJson.get(MPN).getAsString();
        final String title = Objects.isNull(productJson.get(TITLE)) || productJson.get(TITLE).isJsonNull() || !productJson.get(TITLE).isJsonPrimitive() ? null
                : productJson.get(TITLE).getAsString();
        final String brand = Objects.isNull(productJson.get(BRAND)) || productJson.get(BRAND).isJsonNull() || !productJson.get(BRAND).isJsonPrimitive() ? null
                : productJson.get(BRAND).getAsString();
        final String description = Objects.isNull(productJson.get(DESCRIPTION)) || productJson.get(DESCRIPTION).isJsonNull() || !productJson.get(DESCRIPTION).isJsonPrimitive() ? null
                : productJson.get(DESCRIPTION).getAsString();
        final JsonArray categoriesArray = Objects.isNull(productJson.get(CATEGORIES)) || productJson.get(CATEGORIES).isJsonNull() || !productJson.get(CATEGORIES).isJsonArray() ? null
                : productJson.get(CATEGORIES).getAsJsonArray();
        final JsonArray sellersArray = Objects.isNull(productJson.get(SELLERS)) || productJson.get(SELLERS).isJsonNull() || !productJson.get(SELLERS).isJsonArray() ? null
                : productJson.get(SELLERS).getAsJsonArray();

        /** Build the Categories */
        List<Category> categories = Lists.newArrayList();
        if (categoriesArray != null && categoriesArray.size() > 0) {
            Type categoriesListType = new TypeToken<List<Category>>() {}.getType();
            categories = new Gson().fromJson(categoriesArray, categoriesListType);
        }


        /** Build the Sellers */
        List<Seller> sellers = Lists.newArrayList();
        if (sellersArray != null && sellersArray.size() > 0) {
            Type sellerListType = new TypeToken<List<Seller>>() {}.getType();
            sellers = new Gson().fromJson(sellersArray, sellerListType);
        }

        /** Get the date format */
        ISO8601DateFormat formatter = new ISO8601DateFormat();
        Date timestamp = null;
        try {
            timestamp = Objects.isNull(productJson.get(TIMESTAMP)) ? null : formatter.parse(productJson.get(TIMESTAMP).getAsString());
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        /** Populate the metadata*/
        Map<String, Object> attributes = new HashMap<>();
        Iterator<Map.Entry<String, JsonElement>> iterator = productJson.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            if (!standardFieldNames.contains(entry.getKey())) {
                JsonObject value = Objects.isNull(entry.getValue()) || entry.getValue().isJsonNull() || !entry.getValue().isJsonObject() ? null
                        : entry.getValue().getAsJsonObject();

                if (value != null) {
                    value.entrySet().forEach(jsonElementEntry->{
                        JsonElement attributesElement = jsonElementEntry.getValue();
                        JsonElement attributeValue = Objects.isNull(attributesElement) || attributesElement.isJsonNull() || !attributesElement.isJsonPrimitive() ? null
                                : attributesElement;
                        if (attributeValue!= null) {
                            JsonPrimitive jsonPrimitive = attributeValue.getAsJsonPrimitive();
                            if (jsonPrimitive.isBoolean()) {
                                attributes.put(jsonElementEntry.getKey(),jsonPrimitive.getAsBoolean());
                            } else if (jsonPrimitive.isNumber()) {
                                attributes.put(jsonElementEntry.getKey(),jsonPrimitive.getAsNumber());
                            } else {
                                attributes.put(jsonElementEntry.getKey(),jsonPrimitive.getAsString());
                            }
                        }
                    });

                }
            }
        }

        /** Map the JSON to the product */
        final Product product = new Product(apin, mpn, title, brand, description, timestamp, null, attributes,
                categories,sellers);

        return product;
    }
}