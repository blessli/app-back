package com.ldm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandle {

    // 2008-12-01 00:00:00对应的时间截,用于reddit热度排名算法
    private static final long beforeTimeValue=1228060800000L;
    public static String currentDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static long changeDate(String publishTime) throws ParseException {
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long start=dateFormat.parse(publishTime).getTime();
        return (start-beforeTimeValue)/1000;
    }
}
