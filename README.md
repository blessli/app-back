# 组团互动社交平台（Reality小程序）

# 项目描述

本项目为Reality小程序后端部分,该项目提供组团服务，用户通过不同类型的组团社交形式进行交友

队友负责的前端部分: [传送门](https://github.com/KWskrrrr/reality)

网速不好的话，建议前往[gitee](https://gitee.com/d1705030118/app-back)

# 系统架构
![系统架构图](/images/架构图.PNG)

# 交互图
![交互图](/images/交互图.PNG)

## 服务化

- [活动服务](https://github.com/blessli/app-back)
- [通讯服务](https://github.com/blessli/app-back-chat)
- [定时服务](https://github.com/blessli/app-back-notice)

# 模块化&技术栈
| 模块名 | 技术栈 |
| :-----| :---- |
| 活动 | Reddit算法 |
| 好友动态 | RabbitMQ |
| 通讯 | Netty |
| 搜索 | Elasticsearch |
| 缓存 | Redis |
| 流控 | Redis |
| 日志 | logback |
| 定时任务 | Spring Task |
| 接入层 | Nginx |

# TODO

- [ ] MySQL读写分离
- [ ] Redis Cluster
- [ ] 个性化推荐
- [ ] 微服务架构
- [ ] JedisCluster支持管道
- [ ] 设计模式
- [ ] Nginx配置优化
- [ ] CDN加速
- [ ] 网关层
# 成员情况
- 前端：[**霞](https://github.com/KWskrrrr)
- 后端：[**明](https://github.com/blessli)

# 声明
> 由于该小程序涉及个人小程序未开放类目，一直无法完成上线
