package com.showcase.search.pacts;


import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.showcase.search.config.ServiceUrls;
import org.apache.http.entity.ContentType;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ProductServiceTest {
    static final String SINGLE_PRODUCT_REQUEST = "{\n" +
            "\t\"products\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"apin\": \"ContractsTest1\",\n" +
            "\t\t\t\"mpn\":\"XYZ-1234\",\n" +
            "\t\t\t\"title\":\"Game of Thrones\",\n" +
            "\t\t\t\"description\":\"Some desc\",\n" +
            "\t\t\t\"brand\":\"ContractsTest\",\n" +
            "\t\t\t\"categories\":[\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"id\":\"1\",\n" +
            "\t\t\t\t\t\"name\":\"Books\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"sellers\":[\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"id\":\"1\",\n" +
            "\t\t\t\t\t\"name\":\"Seller1\",\n" +
            "\t\t\t\t\t\"sku\":\"SKU-1-1\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"attributes\":{\n" +
            "\t\t\t\t\t\"attribute1\": \"attribute1Value\",\n" +
            "\t\t\t\t\t\"attribute2\": \"attribute2Value\",\n" +
            "\t\t\t\t\t\"attribute3\": 34,\n" +
            "\t\t\t\t\t\"attribute4\": 34.2,\n" +
            "\t\t\t\t\t\"attribute5\": true\n" +
            "\t\t\t\t}\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}\n";

    static final String MULTIPLE_PRODUCTS_REQUEST = "{\n" +
            "\t\"products\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"apin\": \"ContractTest1\",\n" +
            "\t\t\t\"mpn\":\"XYZ-1234\",\n" +
            "\t\t\t\"title\":\"Game of Thrones\",\n" +
            "\t\t\t\"description\":\"Some desc\",\n" +
            "\t\t\t\"brand\":\"ContractTest\",\n" +
            "\t\t\t\"categories\":[\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"id\":\"1\",\n" +
            "\t\t\t\t\t\"name\":\"Books\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"sellers\":[\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"id\":\"1\",\n" +
            "\t\t\t\t\t\"name\":\"Seller1\",\n" +
            "\t\t\t\t\t\"sku\":\"SKU-1-1\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t],\n" +
            "\t\t\t\"attributes\":{\n" +
            "\t\t\t\t\t\"attribute1\": \"attribute1Value\",\n" +
            "\t\t\t\t\t\"attribute2\": \"attribute2Value\",\n" +
            "\t\t\t\t\t\"attribute3\": 34,\n" +
            "\t\t\t\t\t\"attribute4\": 34.2,\n" +
            "\t\t\t\t\t\"attribute5\": true\n" +
            "\t\t\t\t}\n" +
            "\t\t},\n" +
            "\t{\n" +
            "\t\t\"apin\": \"ContractTest2\",\n" +
            "\t\t\"mpn\":\"ABC-5678\",\n" +
            "\t\t\"title\":\"Goblet of Fire\",\n" +
            "\t\t\"description\":\"ContractTest\",\n" +
            "\t\t\"brand\":\"\",\n" +
            "\t\t\"categories\":[\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"id\":\"1\",\n" +
            "\t\t\t\t\"name\":\"Books\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"id\":\"2\",\n" +
            "\t\t\t\t\"name\":\"Movies\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"sellers\":[\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"id\":\"1\",\n" +
            "\t\t\t\t\"sku\":\"SKU-1-2\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"id\":\"2\",\n" +
            "\t\t\t\t\"sku\":\"SKU-2-2\"\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t}\n" +
            "\t]\n" +
            "}\n";

    static final String BAD_REQUEST = "";

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("ProductsService", "localhost", 8080, this);

    @Pact(provider="ProductsService", consumer="CatalogService")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap();
        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        return builder
                .given("ContractsVerificationState")
                .uponReceiving("Single Product Upsert Request")
                .path(ServiceUrls.PRODUCT_RESOURCES)
                .headers(headers)
                .method(HttpMethod.POST.name())
                .body(SINGLE_PRODUCT_REQUEST)
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .uponReceiving("Multiple Product Upsert Request")
                .path(ServiceUrls.PRODUCT_RESOURCES)
                .headers(headers)
                .method(HttpMethod.POST.name())
                .body(MULTIPLE_PRODUCTS_REQUEST)
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .uponReceiving("Bad Product Upsert Request")
                .path(ServiceUrls.PRODUCT_RESOURCES)
                .headers(headers)
                .method(HttpMethod.POST.name())
                    .body(BAD_REQUEST)
                .willRespondWith()
                    .status(HttpStatus.BAD_REQUEST.value())
                .toPact();
    }

    @Test
    @PactVerification(value = "ProductsService")
    public void runUpsertTest() throws IOException {
        ConsumerClient consumerClient = new ConsumerClient(mockProvider.getUrl());
        assertEquals(consumerClient.postAndReturnCode(ServiceUrls.PRODUCT_RESOURCES, SINGLE_PRODUCT_REQUEST,
                ContentType.APPLICATION_JSON), 200);
        assertEquals(consumerClient.postAndReturnCode(ServiceUrls.PRODUCT_RESOURCES, MULTIPLE_PRODUCTS_REQUEST,
                ContentType.APPLICATION_JSON), 200);
        assertEquals(consumerClient.postAndReturnCode(ServiceUrls.PRODUCT_RESOURCES, BAD_REQUEST, ContentType.APPLICATION_JSON), 400);
    }

}
