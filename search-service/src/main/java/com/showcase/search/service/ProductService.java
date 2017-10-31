package com.showcase.search.service;

import com.showcase.search.exceptions.HttpServiceException;
import com.showcase.search.exceptions.HttpServiceExceptions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.showcase.search.model.*;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    JestClient jestClient;

    /**
     * Bulk upsert products
     * @return
     */
    public void bulkUpsertProducts(List<Product> productList) throws HttpServiceException {
        if (!productList.isEmpty()) {
            /** Prepare a bulk action builder */
            Bulk.Builder bulkBuilder = new Bulk.Builder()
                    .defaultIndex(Constants.INDEX_NAME)
                    .defaultType(Constants.INDEX_TYPE);

            List<Index> productIndexList = Lists.newArrayList();

            /** Enhance product with suggestion */
            productList.forEach(product -> {
                product.setSuggest(buildSuggestion(product));

                /** Update the last update timestamp */
                product.setTimestamp(DateTime.now().toDate());

                productIndexList.add(new Index.Builder(product).index(Constants.INDEX_NAME).type(Constants.INDEX_TYPE).id(product.getApin()).build());
            });

            bulkBuilder.addAction(productIndexList);

            /** Execute the command */
            try {
                JestResult result = jestClient.execute(bulkBuilder.build());
                log.debug(result.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw HttpServiceExceptions.resourceServerException(e.getMessage());
            } finally {
                log.debug("Finished executing bulk product update action");
            }
        }

    }

    /**
     * Build the suggestion
     * @param product
     * @return
     */
    Suggest buildSuggestion(Product product) {
        /** Build the input strings to do suggestion searches on */
        List<String> suggestInput = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(product.getMpn())) {
            suggestInput.add(product.getMpn());
        }
        if (!Strings.isNullOrEmpty(product.getTitle())) {
            suggestInput.add(product.getTitle());
        }
        if (!Strings.isNullOrEmpty(product.getBrand())) {
            suggestInput.add(product.getBrand());
        }
        if (!Strings.isNullOrEmpty(product.getDescription())) {
            suggestInput.add(product.getDescription());
        }

        /** Build the context using available products and associated sellers */
        Context context = new Context();
        List<Category> productCategories = product.getCategories();
        if (productCategories == null || productCategories.isEmpty()) {
            context.setProductCategories(Lists.newArrayList());
        } else {
            List<String> contextProductCategories = Lists.newArrayList();
            productCategories.forEach(productCategory -> {
                contextProductCategories.add(productCategory.getName());
            });
            context.setProductCategories(contextProductCategories);
        }
        List<Seller> sellers = product.getSellers();
        if (sellers == null || sellers.isEmpty()) {
            context.setSellerIds(Lists.newArrayList());
        } else {
            List<String> contextSellers = Lists.newArrayList();
            sellers.forEach(seller -> {
                contextSellers.add(seller.getId());
            });
            context.setSellerIds(contextSellers);
        }

        /** Explicitly add or update suggestion to the mapping for this product */
        Suggest suggest = new Suggest(suggestInput,context);
        return suggest;
    }

    /**
     * Deletes a product given an APINß
     * @param apin
     */
    public void deleteProduct(final String apin) {
        Delete delete = new Delete.Builder(apin)
                .index(Constants.INDEX_NAME)
                .type(Constants.INDEX_TYPE)
                .build();

        /** Execute the command */
        try {
            JestResult result = jestClient.execute(delete);
            log.debug(result.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HttpServiceExceptions.resourceServerException(e.getMessage());
        } finally {
            log.debug("Finished executing bulk product update action");
        }
    }

    /**
     * Deletes a product given an APINß
     * @param apin
     */
    public Product getProduct(final String apin) {
        Get getProduct = new Get.Builder(Constants.INDEX_NAME, apin)
                .type(Constants.INDEX_TYPE)
                .build();

        /** Execute the command */
        try {
            JestResult result = jestClient.execute(getProduct);
            log.debug(result.toString());

            String productsJsonString = result.getSourceAsString();

            /** Register custom deserializer */
            if (Strings.isNullOrEmpty(productsJsonString) ) {
                log.error("Product with apin = {} not found", apin);
                throw HttpServiceExceptions.notFoundException("Product with supplied apin not found");
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Product.class, new ProductDeserializer());
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            return gson.fromJson(productsJsonString, Product.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HttpServiceExceptions.resourceServerException(e.getMessage());
        } finally {
            log.debug("Finished executing bulk product update action");
        }
    }

}
