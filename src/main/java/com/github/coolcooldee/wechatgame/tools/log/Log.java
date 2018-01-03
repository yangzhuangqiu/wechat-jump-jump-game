package com.github.coolcooldee.wechatgame.tools.log;

/**
 * @Description
 * @Author Dee1024 <coolcooldee@gmail.com>
 * @Version 1.0
 * @Since 1.0
 * @Date 2018/1/3
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Log {

    final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    public static void println(String msg){
        System.out.println("["+df.format(new Date())+"]:"+msg);
    }
}
