```
#端口7000,7001,7002
port 7000

#默认ip为127.0.0.1，需要改为其他节点机器可访问的ip，否则创建集群时无法访问对应的端口，无法创建集群
bind 192.168.252.101

#redis后台运行
daemonize yes

#pidfile文件对应7000，7001，7002
pidfile /var/run/redis_7000.pid

#开启集群，把注释#去掉
cluster-enabled yes

#集群的配置，配置文件首次启动自动生成 7000，7001，7002          
cluster-config-file nodes_7000.conf

#请求超时，默认15秒，可自行设置 
cluster-node-timeout 10100    
        
#aof日志开启，有需要就开启，它会每次写操作都记录一条日志
appendonly yes
```
