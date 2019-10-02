package com.ldm.service.search;

import java.util.List;

public interface SearchService {
    List<String> selectHistorySearchList(String userId,String type);
    List<String> selectHotSearchList(String type);
    void updateHistorySearch(String userId,String keyword,String type);
}
