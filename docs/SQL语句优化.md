> t_activity表大约12万条数据
- 对查询进行优化，应尽量避免全表扫描，首先应考虑在 where 及 order by 涉及的列上建立索引。

# 使用覆盖索引优化limit分页查询
> 分页查询时，我们会在 LIMIT 后面传两个参数，一个是偏移量（offset），一个是获取的条数（limit）。当偏移量很小时，查询速度很快，但是当 offset 很大时，查询速度就会变慢。
- 第一种写法
```sql
SELECT * FROM `t_activity` LIMIT 100000,10
```
```耗时: 0.134s```
- 第二种写法
```sql
SELECT * FROM t_activity t1 JOIN (SELECT activity_id FROM t_activity LIMIT 100000,10) t2
ON t1.activity_id=t2.activity_id
```
```耗时: 0.021s```
# LIMIT 1避免全表扫描
```sql
SELECT * FROM t_activity WHERE activity_name='篮球杯2019-09-23 10:37:13'
```
```耗时: 0.171s```
```sql
SELECT * FROM t_activity WHERE activity_name='篮球杯2019-09-23 10:37:13' LIMIT 1
```
```耗时: 0.001s```

