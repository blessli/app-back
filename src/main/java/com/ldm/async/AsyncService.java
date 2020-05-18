package com.ldm.async;

import com.ldm.dao.ActivityDao;
import com.ldm.dao.SearchActivityDao;
import com.ldm.entity.EsActivity;
import com.ldm.entity.SearchDomain;
import com.ldm.util.DateHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class AsyncService {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private SearchActivityDao searchActivityDao;
    @Async("asyncServiceExecutor")
    public void initEs(){
        log.info("elasticsearch初始化开始!!!");
        List<EsActivity> esActivityList=activityDao.selectEsActivityList();
        Iterable<SearchDomain> iterable = searchActivityDao.findAll();
        Iterator it = iterable.iterator();
        while (it.hasNext()) {
            searchActivityDao.delete((SearchDomain) it.next());
        }
        for (EsActivity activity : esActivityList) {
            SearchDomain searchDomain = new SearchDomain();
            searchDomain.setActivityName(activity.getActivityName());
            searchDomain.setActivityType(activity.getActivityType());
            searchDomain.setActivityId(activity.getActivityId());
            searchDomain.setUserNickname(activity.getUserNickname());
            searchDomain.setLocationName(activity.getLocationName());
            searchActivityDao.save(searchDomain);
        }
        log.info("elasticsearch初始化完成!!!");
    }

    @Async("asyncServiceExecutor")
    public void updateActivityScore(int activityId,String publishTime,int viewCount,int commentCount,int shareCount) throws ParseException {
        long ts= DateHandle.changeDate(publishTime);
        double z=shareCount*3+commentCount*1+viewCount*0.8f;
        double score;
        if (z>0){
            score=Math.log10(z)+ts/45000;
        }else {
            score=ts/45000;
        }
        activityDao.updateActivityScore(activityId,score);
    }

    @Async("asyncServiceExecutor")
    public void crontabAndSyncEs(){
        log.info("定时任务开始执行: 当前时间 {} 同步Es开始!!!",DateHandle.currentDate());
        List<EsActivity> esActivityList=activityDao.selectEsActivityList();
        Iterable<SearchDomain> iterable = searchActivityDao.findAll();
        Iterator it = iterable.iterator();
        while (it.hasNext()) {
            searchActivityDao.delete((SearchDomain) it.next());
        }
        for (EsActivity activity : esActivityList) {
            SearchDomain searchDomain = new SearchDomain();
            searchDomain.setActivityName(activity.getActivityName());
            searchDomain.setActivityType(activity.getActivityType());
            searchDomain.setActivityId(activity.getActivityId());
            searchDomain.setUserNickname(activity.getUserNickname());
            searchDomain.setLocationName(activity.getLocationName());
            searchActivityDao.save(searchDomain);
        }
        log.info("定时任务结束执行: 当前时间 {} 同步Es完成!!!",DateHandle.currentDate());
    }
}
