package com.ldm.dao;
import com.ldm.entity.search.SearchDomain;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchDao extends ElasticsearchRepository<SearchDomain,Integer> {

}