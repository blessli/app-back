# reality(小程序后端)
## Introduction
本项目为reality小程序后端部分,该项目提供组团服务，用户通过不同类型的组团社交形式进行交友
## Technical points
- redis实现分布式session
- 热度排序算法reddit(改进版)
- 好友动态模块采用推拉结合的方式实现
- SQL语句优化,如覆盖索引
- Elasticsearch实现全文检索
- RabbitMQ异步更新feed流
- Spring AOP实现日志管理
- (接入层)Nginx反向代理,负载均衡,限流
- (存储层)MySQL读写分离,Redis Cluster集群
## Difficult points
- redis数据一致性问题
- reddit算法时效性问题
- 线程池参数设置
## Bug record
- [ ] Netty内存泄漏
- [x] pipeline错误使用
- [x] Jedis连接失败
- [x] @Scheduled单线程阻塞
## TODO
- [ ] 个性化推荐
- [ ] 微服务架构
- [ ] JedisCluster支持管道
- [ ] CDN加速