- 推荐关注用户

查询我关注的用户他们关注了哪些用户，并排除我关注的用户和自己 。有点拗口，就是类似微博的推荐关注用户
```sql
select distinct DISTINCT B.following_id from t_follow A
INNER JOIN t_follow B on A.following_id = B.follower_id
LEFT JOIN t_follow C on B.following_id = C.following_id AND C.follower_id = 1
where A.follower_id =1 AND B.following_id != 1
AND C.following_id is NULL limit 10
```
- 查询用户A的粉丝，并且这些粉丝是我关注的人
```sql
SELECT A.following_id from t_follow A INNER JOIN t_follow B on A.following_id = B.follower_id
where A.follower_id = 1 AND B.following_id =5 limit 10
```
- 共同关注
```sql
select * from t_follow t1
inner join t_follow t2
on t1.follower_id = t2.following_id and t1.following_id = t2.follower_id;
```