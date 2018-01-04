package com.github.coolcooldee.wechatgame.service;
import com.github.coolcooldee.wechatgame.tools.AdbToolKit;
import com.github.coolcooldee.wechatgame.tools.LogToolKit;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 跳跃的核心逻辑
 *
 * @Description
 * @author Dee1024 <coolcooldee@gmail.com>
 * @Version 1.0
 * @Since 1.0
 * @Date 2018/1/3
 */

public abstract class JumpService {

    private static Double distance2timeRatio = null;
    private static final String SCREENCAP_PATH = "jumpgame.png";
    private static final Map<String, Double> resolutionMapDistance2timeRatio = new HashMap<String, Double>();
    static {
        resolutionMapDistance2timeRatio.put("1600*2560",0.92*2);
        resolutionMapDistance2timeRatio.put("1440*2560",1.039*2);
        resolutionMapDistance2timeRatio.put("1080*2220",1.3903*2);
        resolutionMapDistance2timeRatio.put("1080*1920",1.3903*2);//debug
        resolutionMapDistance2timeRatio.put("720*1280",2.078*2);
    }

    private static final Map<String, Double> resolutionMapUIRate = new HashMap<String, Double>();
    static {
        resolutionMapUIRate.put("1600*2560",0.05);
        resolutionMapUIRate.put("1440*2560",0.1);
        resolutionMapUIRate.put("1080*2220",0.25);
        resolutionMapUIRate.put("1080*1920",0.5);
        resolutionMapUIRate.put("720*1280",0.5);
    }

    static Point beginPoint = null;
    static Point endPoint = null;


    /**
     * 进行跳跃，同时等待一会，等到其停止，方便下一步截屏
     * @param beginPoint
     * @param endPoint
     */
    public static boolean jump(Point beginPoint, Point endPoint){
        int d = getDistance(beginPoint, endPoint);
        LogToolKit.println("跳跃距离 "+d);
        if(d<50){
            LogToolKit.println("距离太小，重新跳跃 "+d);
            return false;
        }
        AdbToolKit.screentouch(Math.floor(d * getDistance2timeRatio()));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return true;
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
            LogToolKit.println("起跳点 (" + beginPoint.getX() + ", " + beginPoint.getY() + ")");
        }
    }

    public static Double getDistance2timeRatio() {
        return distance2timeRatio;
    }

    public static void setDistance2timeRatio(double distance2timeRatio) {
        JumpService.distance2timeRatio = distance2timeRatio;
    }

    public static Double getDistance2timeRatioByResolution(String resolution){
        return resolutionMapDistance2timeRatio.get(resolution);
    }

    public static Double getUIRatioByResolution(String resolution){
        return resolutionMapUIRate.get(resolution);
    }

    public static Point getEndPoint() {
        return endPoint;
    }

    public static void setEndPoint(Point endPoint) {
        JumpService.endPoint = endPoint;
        if(endPoint!=null){
            LogToolKit.println("目标点 ("+endPoint.getX()+", "+endPoint.getY()+")");
        }

    }
}
