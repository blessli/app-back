# 需求沟通
和产品沟通后，综合考虑开发时限，性能要求和产品逻辑，协商后调整的需求细节总结以下几点：

1. 添加关注后，需要显示关注者最近的发帖动态(包括关注前)
2. 取消关注后， 需要从好友动态列表删除
3. 取消关注后重新关注，同添加关注逻辑处理
4. 好友动态保留最近300条
# 方案设计
好友动态的原理同微博的Feed流，常见有以下几种模式：

- 集中模式。将所有用户的动态汇集到统一的流，不同用户拉取后各自根据关注信息筛选处理。性能要求和复杂度较高，此方案暂不作考虑。
- 推模式。即写扩散。每当用户发帖，对所有粉丝推送一条该用户的动态消息记录。优点是查看动态的读取场景下效率最高。缺点是对关注列表的变动不符合需求。
- 拉模式。即读扩散。每当请求好友动态接口，拉取用户所有关注者的最近动态，然后汇总排序。优点是对关注列表的变化能实时体现。缺点是拉取所有关注者的数据，做汇总排序分页的代价较大。
- 推拉模式。即读+写扩散。是上面两种模式的结合，依据业务需求对推模式做补充，在开销较小的部分使用拉模式。
# 技术选型
## 选型原因
最终选择推拉模式，原因如下：

- 推模式无法满足需求，拉模式开销大于收益。
- 用户关注数量统计，最大用户关注数量为四位数，平均关注数量也在推模式可接受的开销范围内。
- 好友动态功能可接受延迟显现，耗时操作可拆分为队列异步处理。
- 对好友动态的一致性要求较高，关注和取关后，好友动态列表需要保持正确的显示效果。
- 读需求大于写需求。在可预期的范围内，社区活跃用户的发帖动态数量在可接受范围。
- 当前版本开发周期较短，推拉模式在满足前期需求和性能的条件上可较为迅速实现，且之后可做功能扩展。
## 实现机制
实现机制为使用Redis的SortedSets实现。主要考虑以下原因：
- 基于内存操作，性能和吞吐量优于使用MySQL。
- 项目处于平稳增长期，活跃用户带来的操作性能使用Redis尚有余力。
- 用户发布的数据已有其他缓存做持久化存储，好友动态仅需展示，不用作其他后续分析，过期后可直接丢弃。
- 目前线上业务对Redis依赖较多，有现成闲置的缓存服务器可用，搭建适合消息订阅分发的分布式环境需要额外成本。
## 缓存结构
- 每个用户一个收Feed有序集用于存放接收到的好友动态ID。有序集key名包含UID，成员为动态记录(动态ID和动态发布者UID的封装)，分数为时间戳。
- 每个用户一个发Feed有序集用于存放该用户自己发的动态ID。有序集key名包含UID，成员为动态ID，分数为时间戳。
- 一个动态发布处理队列，用于在用户发帖时处理动态推送
- 一个关注取关处理队列，用于在用户关注/取关时处理动态的增减
- 一个数据清理脚本，用于定时清理回收过时数据
## 设计实现
### 用户发帖
- 用户发帖时触发事件通知，将帖子ID，用户UID和时间戳封装为一条消息。 
- 消息放入动态发布处理队列，交给队列进行异步处理。
### 用户关注/取消关注
- 当用户执行关注或者取消关注动作时，将用户UID，关注者UID和动作标识封装为一条消息。 
- 动作标识为数字，表示当前操作为关注，取消关注，后续可扩展。   
- 消息放入关注取关处理队列，交给队列进行异步处理。
### 动态发布处理队列
- 动态发布处理队列发现新消息时，取队首消息出队列。
- 根据消息中的发布者UID，遍历其粉丝列表(当以后全站粉丝量较大时，可扩展为选择性推送)。
- 给每个粉丝推送一条动态，将动态ID和时间戳写入粉丝的收Feed有序集中。
- 消息处理完成，检查队列是否还有消息，无则阻塞。
### 关注取关处理队列
- 关注取关处理队列发现新消息时，取队首消息出队列。
- 根据动作标识判断是关注还是取关操作。
- 如果是关注，拉取关注者的发Feed有序集中的动态，将最近的动态ID写入用户自己的收Feed中。
- 如果是取关，遍历用户自己的收Feed，剔除其中取关UID的动态记录。
- 消息处理完成，检查队列是否还有消息，无则阻塞。
### Feed数据清理
- 脚本作为定时任务启动，时间间隔由功能上线后数据增长情况决定
- 脚本遍历用户的收Feed和发Feed
- 判断每组有序集的数量，对大于300条的数据，从最早的记录开始剔除，直到数量小于等于300条为止
# 功能扩展
## 操作合并
当关注/取关操作量较大，或有用户频繁重复执行关注/取关时，可能需要对关注取关处理队列的操作进行合并
入队列时，可判断队列中是否已存在相同uid和feed_uid的记录在等待消费，若存在，则剔除之前的，以最后操作为准
## 选择性推送
- 当用户的粉丝量较大时，动态发布队列处理延迟会比较久
- 可扩展为选择性推送，优先发送给当前在线的用户，对不在线的用户等待其上线后再执行拉取
- 需要客户端配合，对社区用户在线状态进行处理，通知给服务端
!(https://quericy.me/blog/861/)