package com.showcase.search.pacts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.UrlEscapers;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConsumerClient{
    private String url;

    public ConsumerClient(String url) {
        this.url = url;
    }

    private String encodePath(String path) {
        return Arrays.asList(path.split("/"))
                .stream().map(UrlEscapers.urlPathSegmentEscaper()::escape).collect(Collectors.joining("/"));
    }

    /**
     * Build a complete URL using the path and query string
     *
     * @param path
     * @param queryString
     * @return
     */
    protected String buildUrl(String path, String queryString) {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath(path);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (StringUtils.isNotEmpty(queryString)) {
            uriBuilder.setParameters(parseQueryString(queryString));
        }

        return uriBuilder.toString();
    }

    /**
     * Parse query string to ensure basic name value pairs
     *
     * @param queryString
     * @return
     */
    protected List<NameValuePair> parseQueryString(String queryString) {
        return Arrays.stream(queryString.split("&")).map(s -> s.split("="))
                .map(p -> new BasicNameValuePair(p[0], UrlEscapers.urlFormParameterEscaper().escape(p[1])))
                .collect(Collectors.toList());
    }

    /**
     * Add headers if present
     *
     * @param request The actual request to operate on
     * @param headers The map of headers with key being the header name and value being the value
     * @return
     */
    protected Request addHeaders(Request request , Map<String, String> headers) {
        if (headers!= null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
               request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return request;
    }
    protected String executeGetAndReturnResponseAsString(String path, String queryString, Map<String, String> headers) throws IOException {
        Request request = addHeaders(Request.Get(buildUrl(path, queryString)), headers);
        return request.execute().returnContent().asString();
    }

    public <T> T get(String path, String queryString, Map<String, String> headers, Class<T> clazz) throws IOException {
        return jsonToType(executeGetAndReturnResponseAsString(path, queryString, headers), clazz);
    }

    public <T> T post(String path, String body, ContentType mimeType, Map<String, String> headers, Class<T> clazz) throws IOException {
        Request request = addHeaders(Request.Post(url + encodePath(path)), headers);
        String respBody = request.bodyString(body, mimeType)
                .execute().returnContent().asString();
        return jsonToType(respBody, clazz);
    }

    public int postAndReturnCode(String path, String body, ContentType mimeType) throws IOException {
        return Request.Post(url + encodePath(path))
                .bodyString(body, mimeType)
                .execute().returnResponse().getStatusLine().getStatusCode();
    }

    private HashMap jsonToMap(String respBody) throws IOException {
        if (respBody.isEmpty()) {
            return new HashMap();
        }
        return new ObjectMapper().readValue(respBody, HashMap.class);
    }

    private <T> T jsonToType(String respBody, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(respBody, clazz);
    }
}
