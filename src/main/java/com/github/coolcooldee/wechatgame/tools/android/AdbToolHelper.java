package com.github.coolcooldee.wechatgame.tools.android;

/**
 * @Description
 * @Author Dee1024 <coolcooldee@gmail.com>
 * @Version 1.0
 * @Since 1.0
 * @Date 2018/1/3
 */

import com.github.coolcooldee.wechatgame.service.JumpService;
import com.github.coolcooldee.wechatgame.service.PropertiesService;
import com.github.coolcooldee.wechatgame.tools.log.Log;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * 封装ADB功能，通过执行ADB命令行实现
 * 需要先下载 Android Debug Bridge 到本地，参考地址：https://developer.android.com/studio/command-line/adb.html#forwardports
 *
 */
public abstract class AdbToolHelper {

    //请使用本地的ABD工具路径替换，该值需要在启动时，按照引导配置
    static String adbPath = "/Users/root/Downloads/platform-tools/adb";

    final static String SCRIPT_SCREEN_CAP = "${adbpath} exec-out screencap -p > ${imagename}";
    final static String SCRIPT_SCREEN_TOUCH = "${adbpath} shell input swipe ${x1} ${y1} ${x2} ${y2} ${time}";
    final static String SCRIPT_DEVICES = "${adbpath} devices";
    static boolean isSetting = false;

    public static boolean init(){
        boolean isok = setting();
        if(!isok){
            Log.println("应用启动失败.");
            return false;
        }
        Log.println("应用启动成功.");
        return true;
    }

    private static String[] genBaseSysParams(){
        String[] args = new String[3];
        String os = System.getProperty("os.name");
        if (os.toLowerCase().trim().startsWith("win")) {
            //Log.println("系统检测 win.");
            args[0] = "cmd.exe";
            args[1] = "/c";
        }else{
            //Log.println("系统检测 Linux / Mac os.");
            args[0] = "bash";
            args[1] = "-c";
        }
        return args;
    }

    /**
     * ADB手机屏幕截图
     */
    public static void screencap() {
        String[] args = genBaseSysParams();
        args[2] = SCRIPT_SCREEN_CAP.replace("${adbpath}", adbPath).replace("${imagename}", JumpService.getScreencapPath());
        try {
            Runtime.getRuntime().exec(args).waitFor();
            //Log.println("截屏成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ADB手机屏幕长按，长按时间
     * @param time  长按时间，精确到毫秒
     * Andorid 版本需要高于 4.4
     */
    public static void screentouch(double time) {
        String times = (int)time + "";
        String x1 = ((new Random()).nextInt(100) + (new Random()).nextInt(100) + 1) + "";
        String y1 = ((new Random()).nextInt(100) + (new Random()).nextInt(100) + 1) + "";
        String x2 = ((new Random()).nextInt(100) + (new Random()).nextInt(100) + 1) + "";
        String y2 = ((new Random()).nextInt(100) + (new Random()).nextInt(100) + 1) + "";

        String args = SCRIPT_SCREEN_TOUCH.replace("${adbpath}", adbPath).replace("${time}", times)
                        .replace("${x1}", x1).replace("${y1}", y1).replace("${x2}", x2).replace("${y2}", y2);
        try {
            Runtime.getRuntime().exec(args).waitFor();
            Log.println("长按"+time+"毫秒");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测ADB工具以及 Android 设备
     *
     * 返回值说明：
     *  1：成功；
     * -1：ADB工具找不到；
     * -2：设备连接异常
     *
     * @param path
     * @return
     */
    public static int checkAdbAndDevice(String path){
        if(path==null || "".equals(path)){
            Log.println("ADB工具路径未设置.");
            return -1;
        }
        String[] args = genBaseSysParams();
        args[2] =SCRIPT_DEVICES.replace("${adbpath}", path);

        int stauts = 0;

        try {
            Process process = Runtime.getRuntime().exec(args);
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<String> lineList = new ArrayList<String>();
            Log.println("check result:");
            while ((line = reader.readLine()) != null) {
                lineList.add(line);
                Log.println(line);
            }
            if(!lineList.isEmpty()) {
                if (lineList.get(0).indexOf("List of devices attached") > -1) {
                    if (lineList.size() > 1 && lineList.get(1).length() > 0) {
                        stauts = 1;
                        Log.println("ADB检测 和 Android设备检测正常.");
                    } else {
                        stauts = -2;
                        Log.println("设备检测异常，请确认是否连接正常.");
                    }
                } else {
                    stauts = -1;
                    Log.println("ADB工具检测异常，未找到.");
                }
            }else{
                stauts = -1;
                Log.println("ADB工具检测异常，未找到.");
            }
            process.waitFor();
            is.close();
            reader.close();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return stauts;
    }

    public static boolean setting(){
        String tempADBPath = PropertiesService.getSettingADBPath();
        int checkR = AdbToolHelper.checkAdbAndDevice(tempADBPath);
        if(checkR==1) {
            setAdbPath(tempADBPath);
            Log.println("ADB工具地址设置成功：" + adbPath);
            return true;
        }else if(checkR == -1){
            if(isSetting) {
                JOptionPane.showMessageDialog(null, "未找到ADB工具，请重新配置ADB工具地址！", "提示", JOptionPane.ERROR_MESSAGE);
            }
            isSetting = true;
            Object adbpathObject = JOptionPane.showInputDialog(null,"请输入ADB工具地址：\n","系统参数设置",JOptionPane.PLAIN_MESSAGE,null,null,"例如：/Users/dee/Downloads/platform-tools/adb");
            if(adbpathObject!=null){
                tempADBPath = adbpathObject.toString();
                PropertiesService.setSettingADBPath(tempADBPath);
                setting();
                return true;
            }else{
                System.exit(0);
            }
        }else if(checkR == -2){
            JOptionPane.showMessageDialog(null, "未找接入的 Android 设备，请检测设备连接情况，确认后再点击确认！", "提示",JOptionPane.ERROR_MESSAGE);
            setting();
            return true;
        }
        PropertiesService.setSettingADBPath(tempADBPath);
        setAdbPath(tempADBPath);
        Log.println("ADB工具地址设置成功：" + adbPath);
        return true;
    }



    public static void setAdbPath(String adbPath) {
        AdbToolHelper.adbPath = adbPath;
    }
}
