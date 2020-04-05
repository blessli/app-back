> 面对日益增加的系统访问量，数据库的吞吐量面临着巨大瓶颈。 对于同一时间有大量并发读操作和较少写操作类型的应用系统来说，将单一的数据库拆分为主库和从库，主库负责处理事务性的增删改操作，从库负责处理查询操作，能够有效的避免由数据更新导致的行锁，使得整个系统的查询性能得到极大的改善。 通过一主多从的配置方式，可以将查询请求均匀的分散到多个数据副本，能够进一步的提升系统的处理能力。 使用多主多从的方式，不但能够提升系统的吞吐量，还能够提升系统的可用性，可以达到在任何一个数据库宕机，甚至磁盘物理损坏的情况下仍然不影响系统的正常运行。
虽然读写分离可以提升系统的吞吐量和可用性，但同时也带来了数据不一致的问题，这包括多个主库之间的数据一致性，以及主库与从库之间的数据一致性的问题。并且，读写分离也带来了与数据分片同样的问题，它同样会使得应用开发和运维人员对数据库的操作和运维变得更加复杂。透明化读写分离所带来的影响，让使用方尽量像使用一个数据库一样使用主从数据库，是读写分离中间件的主要功能。
- 原理
> 读写分离，简单来说，就是将DML交给主数据库去执行，将更新结果同步至各个从数据库保持主从数据一致，DQL分发给从数据库去查询，从数据库只提供读取查询操作。读写分离特别适用于读多写少的场景下，通过分散读写到不同的数据库实例上来提高性能，缓解单机数据库的压力。

首先，主从复制涉及到三个线程，分别是binlog线程、I/O线程、SQL线程   
1、在主库上把数据更改记录到二进制日志（binary log）中  
2、备库将主库上的二进制日志复制到自己的中继日志（relay log）中    
3、备库读取中继日志中的事件，将其重放到备库数据库上
- 实现
```
spring.shardingsphere.datasource.names=master,slave

# 主数据源
spring.shardingsphere.datasource.master.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.master.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.master.url=jdbc:mysql://192.168.164.134:3306/test?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=GMT&useSSL=false
spring.shardingsphere.datasource.master.username=root
spring.shardingsphere.datasource.master.password=123456

# 从数据源
spring.shardingsphere.datasource.slave.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.slave.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.slave.url=jdbc:mysql://192.168.164.129:3306/test?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=GMT&useSSL=false
spring.shardingsphere.datasource.slave.username=root
spring.shardingsphere.datasource.slave.password=123456

# 读写分离配置
spring.shardingsphere.masterslave.load-balance-algorithm-type=round_robin
spring.shardingsphere.masterslave.name=dataSource
spring.shardingsphere.masterslave.master-data-source-name=master
spring.shardingsphere.masterslave.slave-data-source-names=slave

# 显示SQL
spring.shardingsphere.props.sql.show=true
spring.main.allow-bean-definition-overriding=true
```
```
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
    <version>4.0.0-RC1</version>
</dependency>
        
```
- 遇到的问题

Sharding-JDBC目前仅支持一主多从的结构  
Sharding-JDBC没有提供主从同步的实现，该功能需要自己额外搭建  
主库和从库的数据同步延迟导致的数据不一致问题需要自己去解决