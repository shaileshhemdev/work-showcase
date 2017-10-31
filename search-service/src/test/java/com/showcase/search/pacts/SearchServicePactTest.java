package com.showcase.search.pacts;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.showcase.search.config.ServiceUrls;
import com.showcase.search.model.SearchResponse;
import com.google.common.collect.Lists;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SearchServicePactTest extends ConsumerPactTestMk2 {
    /** Provider for search service */
    static final String PROVIDER_NAME = "SuggestService";

    /** APN Catalog Service Consumer */
    static final String CONSUMER_NAME = "APNWebApp";

    static final String SUGGEST_RESPONSE = "{\n" +
            "    \"pageNumber\": 0,\n" +
            "    \"totalRecords\": 2,\n" +
            "    \"hasMore\": false,\n" +
            "    \"items\": [\n" +
            "        {\n" +
            "            \"apin\": \"ContractTest1\",\n" +
            "            \"mpn\": \"XYZ-1234\",\n" +
            "            \"title\": \"Game of Thrones\",\n" +
            "            \"brand\": \"ContractTest\",\n" +
            "            \"description\": \"Some desc\",\n" +
            "            \"attributes\": {\n" +
            "                \"attribute5\": true,\n" +
            "                \"attribute4\": 34.2,\n" +
            "                \"attribute1\": \"attribute1Value\",\n" +
            "                \"attribute3\": 34,\n" +
            "                \"attribute2\": \"attribute2Value\"\n" +
            "            },\n" +
            "            \"categories\": [\n" +
            "                {\n" +
            "                    \"id\": \"1\",\n" +
            "                    \"name\": \"Books\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"sellers\": [\n" +
            "                {\n" +
            "                    \"id\": \"1\",\n" +
            "                    \"name\": \"Seller1\",\n" +
            "                    \"sku\": \"SKU-1-1\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"apin\": \"ContractTest2\",\n" +
            "            \"mpn\": \"ABC-5678\",\n" +
            "            \"title\": \"Goblet of Fire\",\n" +
            "            \"brand\": \"\",\n" +
            "            \"description\": \"ContractTest\",\n" +
            "            \"attributes\": {},\n" +
            "            \"categories\": [\n" +
            "                {\n" +
            "                    \"id\": \"1\",\n" +
            "                    \"name\": \"Books\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"2\",\n" +
            "                    \"name\": \"Movies\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"sellers\": [\n" +
            "                {\n" +
            "                    \"id\": \"1\",\n" +
            "                    \"name\": null,\n" +
            "                    \"sku\": \"SKU-1-2\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": \"2\",\n" +
            "                    \"name\": null,\n" +
            "                    \"sku\": \"SKU-2-2\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    @Override
    protected RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap();
        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        final String query = "searchType=suggest&input=ContractTest";

        return builder
                .given("Test State")
                .uponReceiving("Example Interaction for Suggest Search")
                    .path(ServiceUrls.PRODUCT_RESOURCES)
                    .method(HttpMethod.GET.name())
                    .query(query)
                .willRespondWith()
                    .status(HttpStatus.OK.value())
                    .body(SUGGEST_RESPONSE)
                .toPact();
    }

    @Override
    protected String providerName() {
        return PROVIDER_NAME;
    }

    @Override
    protected String consumerName() {
        return CONSUMER_NAME;
    }

    @Override
    protected void runTest(MockServer mockServer) throws IOException {
        final String query = "searchType=suggest&input=ContractTest";
        SearchResponse expectedSearchResponse = new SearchResponse(0, 2, false, Lists.newArrayList());
        ConsumerClient consumerClient = new ConsumerClient(mockServer.getUrl());
        SearchResponse actualSearchResponse = consumerClient.get(ServiceUrls.PRODUCT_RESOURCES, query, new HashMap<>(), SearchResponse.class);
        assertEquals(actualSearchResponse.getHasMore(), expectedSearchResponse.getHasMore());
        assertEquals(actualSearchResponse.getPageNumber(), expectedSearchResponse.getPageNumber());
        assertEquals(actualSearchResponse.getTotalRecords(), expectedSearchResponse.getTotalRecords());
        assertEquals(actualSearchResponse.getItems().size(), 2);
    }
}
