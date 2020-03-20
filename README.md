- [x] 通过Redis 存储token，实现session共享。采用Redis的zset数据类型实现限流，用于控制用户的行为

- [x] 用户动态消息采用推拉结合的方式实现，如果用户粉丝量较小则直接推送，反之则等待粉丝自行拉取

- [x] 活动和动态模块的排行榜使用Hacker News排名算法。前缀树优化敏感词匹配

- [x] 使用Elasticsearch实现搜索，日志收集功能。通过Spring Task实现定时任务，利用RabbitMQ异步发送短信通知

- [x] 使用Spring的AOP实现日志管理，并使用线程池进行优化

- [x] 在Linux上搭建Tomcat+Nginx负载均衡集群实现流量分配，并使用Nginx的limit_req限制用户请求速率

- [x] 使用Sharing-JDBC实现MySQL数据库的读写分离，成功搭建Redis Cluster集群环境

- [x] 使用基于用户投票的Reddit算法对回帖、题解文章进行评分排名

- [x] 前后端两次MD5加密，添加盐值提高安全性

- [x] 通过拦截器的模式，获取用户登录状态并注入 Bean 中，ThreadLocal 存储用户状态实现线程安全

- [x] 采用Redis的有序集合数据类型实现踩赞、相互关注功能

- [x] 采用 Redis 的列表数据类型实现了一个基于生产者/消费者的小型异步框架，例如用户点赞成功之后就立即返回了，通知被点赞者这条信息存放在异步队列供线程池处理

![](/images/redis-async-list.png)