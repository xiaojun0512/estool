package com.cetc10.utils.service;

import org.elasticsearch.action.search.SearchResponse;

public interface EsService {
    void insert();

    SearchResponse search();
}
