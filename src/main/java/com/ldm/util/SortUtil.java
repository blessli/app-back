package com.ldm.util;

public class SortUtil {
    /**
     * @title 基于Reddit算法
     * @description 由于没有反对票,因此使用三个维度进行权重计算:viewCount,commentCount,shareCount
     * @author lidongming
     * @updateTime 2020/4/16 14:27
     */
    public static void reddit(int viewCount,int commentCount,int shareCount){
        // 帖子的新旧程度t
        long ts=(System.currentTimeMillis()-1583661840991L)/1000;
        // 赞成票与反对票的差x
        long x=0;
        // 投票方向y,y是一个符号变量，表示对文章的总体看法。如果赞成票居多，y就是+1；如果反对票居多，y就是-1；如果赞成票和反对票相等，y就是0。
        long y=x>0?1:(x==0?0:-1);
        /**
         * 帖子的受肯定（否定）的程度z,
         * z表示赞成票与反对票之间差额的绝对值。如果对某个帖子的评价，越是一边倒，z就越大。如果赞成票等于反对票，z就等于1。
         */
        long z=x==0?1:Math.abs(x);
        double score=Math.log10(z)+y*ts/45000;
        System.out.println(score);
    }
    public static void hackerNews(){

    }
}
