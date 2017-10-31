package com.showcase.search.service;

import com.showcase.search.exceptions.HttpServiceExceptions;
import com.showcase.search.model.Product;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.Analyze;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.indices.settings.GetSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.elasticsearch.common.settings.Settings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static com.showcase.search.model.Constants.INDEX_ANALYZER;
import static com.showcase.search.model.Constants.INDEX_NAME;
import static com.showcase.search.model.Constants.INDEX_TYPE;

@Service
@Slf4j
public class IndexService {
    /** Default index properties */
    static final String NO_OF_SHARDS = "number_of_shards";
    static final String NO_OF_REPLICAS= "number_of_replicas";

    /** Settings */
    static final String SETTINGS = "{\n" +
            "   \"settings\":{\n" +
            "      \"number_of_shards\":5,\n" +
            "      \"number_of_replicas\":1,\n" +
            "      \"analysis\":{\n" +
            "         \"analyzer\":{\n" +
            "            \"my_analyzer\":{\n" +
            "               \"tokenizer\":\"my_tokenizer\",\n" +
            "               \"filter\": [\"lowercase\"]" +
            "            },\n" +
            "            \"my_search_analyzer\": {\n" +
            "               \"tokenizer\": \"whitespace\",\n" +
            "               \"filter\": [\"lowercase\"]" +
            "            }\n" +
            "         },\n" +
            "         \"tokenizer\":{\n" +
            "            \"my_tokenizer\":{\n" +
            "               \"type\":\"edge_ngram\",\n" +
            "               \"min_gram\":2,\n" +
            "               \"max_gram\":50,\n" +
            "               \"token_chars\":[\n" +
            "                  \"letter\",\n" +
            "                  \"digit\"\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   }\n" +
            "}";

    /** Mapping JSON */
    static final String PRODUCTS_MAPPING_FILE = "products_mappings_default.json";

    @Autowired
    JestClient jestClient;

    @Autowired
    ProductService productService;

    /**
     * Create an index with default properties
     * @return
     */
    public void createIndex() throws Exception {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(NO_OF_SHARDS,5);
        settingsBuilder.put(NO_OF_REPLICAS,1);

        try {
            JestResult jestResult = jestClient.execute(new CreateIndex.Builder(INDEX_NAME).settings(SETTINGS).build());
            log.debug(jestResult.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HttpServiceExceptions.resourceServerException(e.getMessage());
        } finally {
            log.debug("Finished creating the index");
        }
    }

    /**
     * Gets details for an index
     * @return
     */
    public JestResult getIndex() throws Exception {
        JestResult jestResult = jestClient.execute(new GetMapping.Builder().addIndex(INDEX_NAME).build());
        return jestResult;
    }

    /**
     * Gets details for an index
     * @return
     */
    public JestResult testIndex(final String textToAnalyze) throws Exception {
        Analyze analyze = new Analyze.Builder()
                .analyzer(INDEX_ANALYZER)
                .index(INDEX_NAME)
                .text(textToAnalyze)
                .build();
        JestResult jestResult = jestClient.execute(analyze);
        return jestResult;
    }

    /**
     * Gets details for an index
     * @return
     */
    public JestResult getIndexSettings() throws Exception {
        GetSettings settings = new GetSettings.Builder().addIndex(INDEX_NAME).build();
        JestResult jestResult = jestClient.execute(settings);
        return jestResult;
    }

    /**
     * Deletes an index
     * @return
     */
    public void deleteIndex() throws Exception {
        jestClient.execute(new DeleteIndex.Builder(INDEX_NAME).build());
    }

    /**
     * Creates mapping for a product
     *
     * @throws Exception
     */
    public void createProductMapping() throws Exception {
        URL url = Resources.getResource(PRODUCTS_MAPPING_FILE);
        String expectedMappingSource = Resources.toString(url, Charsets.UTF_8);
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                expectedMappingSource
        ).build();

        /** Execute the command */
        try {
            JestResult result = jestClient.execute(putMapping);
            log.debug(result.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw HttpServiceExceptions.resourceServerException(e.getMessage());
        } finally {
            log.debug("Finished executing search for Audit logs");
        }
    }

    /**
     *
     */
    public void bootstrapData() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("products.csv");

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        try {
            final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());

            List<Product> productList = Lists.newArrayList();
            for (final CSVRecord record : parser) {
                final String apin = UUID.randomUUID().toString();
                final String mpn = record.get(0);
                final String title = record.get(1);
                final String brand = "";
                final String description = "";

                Product product = new Product(apin, mpn, title, brand, description, DateTime.now().toDate(), null, null,
                        Lists.newArrayList(),Lists.newArrayList());
                productList.add(product);

                if (productList.size() == 100) {
                    productService.bulkUpsertProducts(productList);
                    productList.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
