# reality(小程序后端)
## Introduction
本项目为reality小程序后端部分,该项目提供组团服务，用户通过不同类型的组团社交形式进行交友

队友负责的前端部分: [传送门](https://github.com/KWskrrrr/reality)
## Technical points
- [分布式session](/docs/session共享.md)
- 热度排序算法reddit(改进版)
- 好友动态模块采用推拉结合的方式实现
- [SQL语句优化](/docs/SQL语句优化.md)
- [Elasticsearch实现全文检索](/docs/Elasticsearch全文检索.md)
- RabbitMQ异步更新feed流
- [Spring AOP实现日志管理](/docs/AOP日志管理.md)
- [Nginx负载均衡,限流](/docs/Nginx负载均衡&限流.md)
- [MySQL读写分离](/docs/MySQL读写分离.md)
- [Redis Cluster](/docs/Redis%20Cluster.md)
## Difficult points
- redis数据一致性问题
- reddit算法时效性问题
- 线程池参数设置
- 分布式的数据同步问题
## Bug record
- [ ] Netty内存泄漏
- [x] pipeline错误使用
- [x] Jedis连接失败
- [x] @Scheduled单线程阻塞
## TODO
- [ ] 个性化推荐
- [ ] 微服务架构
- [ ] JedisCluster支持管道
- [ ] 设计模式
- [ ] Nginx配置优化
- [ ] CDN加速