package com.ldm.dao;
import com.ldm.entity.SearchDomain;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface SearchActivityDao extends ElasticsearchRepository<SearchDomain,Integer> {
    void deleteByActivityIdEquals(int activityId);

}