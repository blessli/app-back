package com.ldm.dao;

import com.ldm.entity.search.LogDomain;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LogDao  extends ElasticsearchRepository<LogDomain,Integer> {
}