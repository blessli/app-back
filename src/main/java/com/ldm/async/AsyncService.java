package com.ldm.async;


public interface AsyncService {
    /**
     * @title 使用线程池异步导入es
     * @description 将MySQL中活动数据表导入Elasticsearch中
     * @author lidongming
     * @updateTime 2020/3/28 22:56
     */
    void mysqlToEs();
    /**
     * @title 添加日志
     * @description 将controller层的请求日志存储到es
     * @author lidongming
     * @updateTime 2020/3/28 22:55
     */
    void createLog();
    /**
     * @title 更新日志
     * @description 抛出异常的情况下，需要对该日志进行更新
     * @author lidongming
     * @updateTime 2020/3/28 22:25
     */
    void updateLog();
}
