package com.ldm.dao;
import com.ldm.entity.SearchDomain;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SearchActivityDao extends ElasticsearchRepository<SearchDomain,Integer> {
    List<SearchDomain> findByActivityIdEquals(int activityId);
    void deleteByActivityIdEquals(int activityId);

}