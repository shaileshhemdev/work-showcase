package com.showcase.search.web;

import com.showcase.search.service.IndexService;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.showcase.search.config.ServiceUrls;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Product Catalog ES Management Service", description = "Service to interact with Catalog Index")
public class CatalogIndexController {

    @Autowired
    IndexService indexService;

    @RequestMapping(method = RequestMethod.POST, value = ServiceUrls.PRODUCT_INDEX)
    public void createIndex() throws Exception {
        indexService.createIndex();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = ServiceUrls.PRODUCT_INDEX)
    public void deleteIndex() throws Exception {
        indexService.deleteIndex();
    }

    @RequestMapping(method = RequestMethod.GET, value = ServiceUrls.PRODUCT_INDEX)
    public @JsonRawValue String getIndex() throws Exception {
        return indexService.getIndex().getJsonString();
    }

    @RequestMapping(method = RequestMethod.GET, value = ServiceUrls.PRODUCT_INDEX_SETTINGS)
    public @JsonRawValue String getIndexSettings() throws Exception {
        return indexService.getIndexSettings().getJsonString();
    }

    @RequestMapping(method = RequestMethod.GET, value = ServiceUrls.PRODUCT_INDEX_ANALYZE)
    public @JsonRawValue String testAnalyzer(@RequestParam String text) throws Exception {
        return indexService.testIndex(text).getJsonString();
    }

    @RequestMapping(method = RequestMethod.POST, value = ServiceUrls.PRODUCT_INDEX_MAPPING)
    public void createMapping() throws Exception {
        indexService.createProductMapping();
    }

    @RequestMapping(method = RequestMethod.POST, value = ServiceUrls.PRODUCT_BOOTSTRAP)
    public void initDataSet() throws Exception {
        indexService.bootstrapData();
    }

}
