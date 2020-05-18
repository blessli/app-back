- 服务器信息
cpu 1核
内存 2G
- jvm参数查看
- 查看初始堆内存-Xms
```
jinfo -flag InitialHeapSize 4999
```
-XX:InitialHeapSize=31457280(30M)

- 查看最大堆内存-Xmx
```
-XX:MaxHeapSize=482344960(460M)
```
-XX:MaxHeapSize=482344960