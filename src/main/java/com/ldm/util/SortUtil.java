package com.ldm.util;

public class SortUtil {
    /**
     * Reddit的排名算法，基于用户投票
     * @param like
     * @param unlike
     */
    public static void reddit(int like,int unlike){
        // 帖子的新旧程度t
        long t=System.currentTimeMillis()-1583661840991L;
        // 赞成票与反对票的差x
        long x=like-unlike;
        // 投票方向y,y是一个符号变量，表示对文章的总体看法。如果赞成票居多，y就是+1；如果反对票居多，y就是-1；如果赞成票和反对票相等，y就是0。
        long y=x>0?1:(x==0?0:-1);
        /**
         * 帖子的受肯定（否定）的程度z,
         * z表示赞成票与反对票之间差额的绝对值。如果对某个帖子的评价，越是一边倒，z就越大。如果赞成票等于反对票，z就等于1。
         */
        long z=x==0?1:Math.abs(x);
        double score=Math.log10(z)+y*t/45000;
        System.out.println(score);
    }
    public static void hackerNews(){

    }
}
