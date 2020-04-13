package com.ldm.service;

import com.ldm.dao.DynamicDao;
import com.ldm.entity.Dynamic;
import com.ldm.entity.DynamicDetail;
import com.ldm.entity.LikeNotice;
import com.ldm.request.PublishDynamic;
import com.ldm.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author lidongming
 * @ClassName DynamicService.java
 * @Description 动态服务
 * @createTime 2020年04月04日 05:05:00
 */
@Slf4j
@Service
public class DynamicService {

    @Autowired
    private DynamicDao dynamicDao;

    @Autowired
    private CacheService cacheService;

    /**
     * @title 发表动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/7 13:44
     */
    public int publish(PublishDynamic request) {
        int ans = dynamicDao.publishDynamic(request);
        if (ans <= 0) {
            return ans;
        }
        log.debug("发布动态成功,动态ID为 " + request.getDynamicId());
        List<String> imageList = Arrays.asList(request.getImages().split(","));
        cacheService.hset(RedisKeyUtil.getDynamicInfo(request.getDynamicId()), "image", imageList.get(0));
        cacheService.hset(RedisKeyUtil.getDynamicInfo(request.getDynamicId()), "userId", String.valueOf(request.getUserId()));
        return ans;
    }

    /**
     * @title 获取已关注者发表的动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/7 2:44
     */
    public List<Dynamic> selectDynamicList(int userId, int pageNum, int pageSize) {
        List<Dynamic> dynamicList = dynamicDao.selectDynamicList(userId, pageSize * (pageNum - 1), pageSize);
        for (Dynamic dynamic : dynamicList) {
            List<String> list = Arrays.asList(dynamic.getImages().split(","));
            dynamic.setImageList(list);
        }
        return dynamicList;
    }

    /**
     * @title 获取我的动态列表
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 16:49
     */
    public List<Dynamic> selectMyDynamicList(int userId, int pageNum, int pageSize) {
        List<Dynamic> dynamicList = dynamicDao.selectMyDynamicList(userId, pageSize * (pageNum - 1), pageSize);
        for (Dynamic dynamic : dynamicList) {
            List<String> list = Arrays.asList(dynamic.getImages().split(","));
            dynamic.setImageList(list);
        }
        return dynamicList;
    }

    /**
     * @title 获取某个动态详情
     * @description
     * @author lidongming
     * @updateTime 2020/4/10 20:57
     */
    public DynamicDetail selectDynamicDetail(int dynamicId, int userId) {
        DynamicDetail dynamicDetail = dynamicDao.selectDynamicDetail(dynamicId, userId);
        List<String> list = Arrays.asList(dynamicDetail.getImages().split(","));
        dynamicDetail.setImageList(list);
        return dynamicDetail;
    }

    /**
     * @title 删除动态
     * @description 使用分布式锁和事务
     * @author lidongming
     * @updateTime 2020/4/7 13:45
     */
    @Transactional
    public int deleteDynamic(int dynamicId) {
        int ans = dynamicDao.deleteDynamic(dynamicId);
        if (ans <= 0) {
            return ans;
        }
        cacheService.mdel("dynamic:" + dynamicId);
        cacheService.delete(RedisKeyUtil.getDynamicInfo(dynamicId));
        return ans;
    }

    /**
     * @title 点赞动态
     * @description
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    public int likeDynamic(int dynamicId, int userId) {
        String key = "like:dynamic:" + dynamicId + ":" + userId;
        cacheService.sadd("dynamic:" + dynamicId, key);// 为了方便清理
        if (!cacheService.exists(key) && dynamicDao.checkLiked(dynamicId, userId) == 0) {
            dynamicDao.likeDynamic(dynamicId, userId,
                    cacheService.hget(RedisKeyUtil.getDynamicInfo(dynamicId), "image"));
            cacheService.set(key, 0);
        }
        return 1;
    }

    /**
     * @title 取消点赞动态
     * @description 取消给动态点赞
     * @author lidongming
     * @updateTime 2020/4/8 1:48
     */
    public int cancelLikeDynamic(int dynamicId, int userId) {
        String key = "like:dynamic:" + dynamicId + ":" + userId;
        if (cacheService.exists(key) || dynamicDao.checkLiked(dynamicId, userId) > 0) {
            dynamicDao.cancelLikeDynamic(dynamicId, userId);
            cacheService.delete(key);
        }
        return 1;
    }

    /**
     * @title 点赞通知
     * @description
     * @author lidongming
     * @updateTime 2020/4/11 22:38
     */
    public List<LikeNotice> selectLikeNotice(int userId, int pageNum, int pageSize) {
        cacheService.set(RedisKeyUtil.getCommentNoticeUnread(1, userId), 0);
        return dynamicDao.selectLikeNotice(userId, pageSize *(pageNum -1 ), pageSize);
    }
}
