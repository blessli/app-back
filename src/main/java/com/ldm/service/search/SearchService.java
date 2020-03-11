package com.ldm.service.search;

import com.github.pagehelper.PageInfo;
import com.ldm.entity.search.LogDomain;
import com.ldm.entity.search.SearchDomain;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

public interface SearchService {
    List<String> selectHistorySearchList(String userId,String type);
    List<String> selectHotSearchList(String type);
    void updateHistorySearch(String userId,String keyword,String type);
    PageInfo<SearchDomain> searchActivity(@PathVariable("pageNum") int pageNum,
                                          @PathVariable("pageSize") int pageSize, @PathVariable("key") String key);
    void saveActivity(SearchDomain searchDomain);
    void deleteActivity(SearchDomain searchDomain);
    /**
     * 新增系统日志
     */
    void createLog(LogDomain log);
    /**
     * 更新系统日志
     */
    void updateLog(LogDomain log);
}
