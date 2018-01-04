package com.github.coolcooldee.wechatgame.tools.android;

/**
 * @Description
 * @Author Dee1024 <coolcooldee@gmail.com>
 * @Version 1.0
 * @Since 1.0
 * @Date 2018/1/3
 */

import com.github.coolcooldee.wechatgame.service.JumpService;
import com.github.coolcooldee.wechatgame.tools.log.Log;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * 封装ADB功能，通过执行ADB命令行实现
 * 需要先下载 Android Debug Bridge 到本地，参考地址：https://developer.android.com/studio/command-line/adb.html#forwardports
 *
 */
public abstract class AdbToolHelper {

    //请使用本地的ABD工具路径替换，该值需要在启动时，按照引导配置
    static String adbPath = "/Users/dee/Downloads/platform-tools/adb";

    final static String SCRIPT_SCREEN_CAP = "${adbpath} exec-out screencap -p > ${imagename}";
    final static String SCRIPT_SCREEN_TOUCH = "${adbpath} shell input swipe ${x1} ${y1} ${x2} ${y2} ${time}";
    final static String SCRIPT_DEVICES = "${adbpath} devices";

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
            args[0] = "cmd.exe";
            args[1] = "/c";
        }else{
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
            Log.println("截屏成功");
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

    public static boolean device() {
        String[] args = genBaseSysParams();
        args[2] =SCRIPT_DEVICES.replace("${adbpath}", adbPath);
        try {
            Process process = Runtime.getRuntime().exec(args);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            boolean isok = true;
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                isok = false;
                System.out.println(s);
            }
            process.waitFor();
            if(isok){
                Log.println("ADB检测成功.");
            }else{
                Log.println("ADB检测失败.");
            }
            return isok;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.println("ADB检测异常.");
        return false;
    }

    public static boolean setting(){
        //判断配置文件是否存在


        Object adbpath = JOptionPane.showInputDialog(null,"请输入ADB工具地址：\n","系统参数设置",JOptionPane.PLAIN_MESSAGE,null,null,"例如：/Users/dee/Downloads/platform-tools/adb");
        Log.println("ADB工具地址：" + adbpath);
        if(adbpath!=null && !"".equals(adbpath)) {
            AdbToolHelper.setAdbPath(adbpath.toString());
            return AdbToolHelper.device();
        }
        return false;
    }

    public static void setAdbPath(String adbPath) {
        AdbToolHelper.adbPath = adbPath;
    }
}
