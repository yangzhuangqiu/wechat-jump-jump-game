package com.github.coolcooldee.wechatgame.service;
import com.github.coolcooldee.wechatgame.tools.android.AdbToolHelper;
import com.github.coolcooldee.wechatgame.tools.log.Log;
import java.awt.*;

/**
 * 跳跃的核心逻辑
 *
 * @Description
 * @Author Dee1024 <coolcooldee@gmail.com>
 * @Version 1.0
 * @Since 1.0
 * @Date 2018/1/3
 */

public class JumpService {

    static final double DISTANCE_2_TIME_RATIO = 2.65;
    static final String SCREENCAP_PATH = "jumpgame.png";


    static Point beginPoint = null;
    static Point endPoint = null;
    static PanelUIService panel;

    /**
     * 初始化UI
     */
    public static void init(){
        boolean isok = AdbToolHelper.setting();
        if(!isok){
            Log.println("应用启动失败.");
            return;
        }
        Log.println("应用启动成功.");
        panel = new PanelUIService();
        refreshUI();
    }

    /**
     * 进行跳跃，同时等待一会，等到其停止，方便下一步截屏
     * @param beginPoint
     * @param endPoint
     */
    public static boolean jump(Point beginPoint, Point endPoint){
        int d = getDistance(beginPoint, endPoint);
        Log.println("跳跃距离 "+d);
        if(d<100){
            Log.println("距离太小，重新跳跃 "+d);
            return false;
        }
        AdbToolHelper.screentouch(Math.floor(d * DISTANCE_2_TIME_RATIO));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return true;
    }

    /**
     * 每次跳完，重新截图， 刷新界面
     */
    public static void refreshUI(){
        AdbToolHelper.screencap();
        panel.getComponent(0).validate();
        panel.getComponent(0).repaint();
    }


    private static int getDistance(Point a, Point b) {
        double _x = Math.abs(a.x - b.x);
        double _y = Math.abs(a.y - b.y);
        return (int)Math.sqrt(_x*_x+_y*_y);
    }

    public static String getScreencapPath() {
        return SCREENCAP_PATH;
    }

    public static Point getBeginPoint() {
        return beginPoint;
    }

    public static void setBeginPoint(Point beginPoint) {
        JumpService.beginPoint = beginPoint;
        if(beginPoint!=null){
            Log.println("起跳点 (" + beginPoint.getX() + ", " + beginPoint.getY() + ")");
        }
    }

    public static Point getEndPoint() {
        return endPoint;
    }

    public static void setEndPoint(Point endPoint) {
        JumpService.endPoint = endPoint;
        if(endPoint!=null){
            Log.println("目标点 ("+endPoint.getX()+", "+endPoint.getY()+")");
        }

    }
}
