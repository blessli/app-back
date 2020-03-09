package com.ldm.async;


public interface AsyncService {
    /**
     *  执行异步任务
     *  将MySQL中活动数据表导入Elasticsearch中
     */
    void mysqlToEs();
    /**
     * 新增系统日志
     */
    void createLog();
    /**
     * 更新系统日志
     */
    void updateLog();
}
