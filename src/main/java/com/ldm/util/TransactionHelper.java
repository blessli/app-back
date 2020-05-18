package com.ldm.util;

import com.ldm.dao.ActivityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lidongming
 * @ClassName TransactionHelper.java
 * @Description TODO
 * @createTime 2020年04月21日 01:00:00
 */
@Service
public class TransactionHelper {
    @Autowired
    private ActivityDao activityDao;

    @Transactional
    public int handleViewCount(int activityId,int userId){
        int ans=activityDao.firstClickActivity(activityId, userId);
        int res=activityDao.addViewCount(activityId);
        if (ans>0&&res>0){
            return 1;
        }
        return 0;
    }

}
