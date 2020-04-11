package com.ldm.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandle {
    public static String currentDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
