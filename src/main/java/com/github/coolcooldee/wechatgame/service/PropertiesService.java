package com.github.coolcooldee.wechatgame.service;

import com.github.coolcooldee.wechatgame.tools.log.Log;

import java.io.*;
import java.util.Properties;

/**
 * @author Dee1024 <coolcooldee@gmail.com>
 * @version 1.0
 * @description
 * @date 2018/1/4
 * @since 1.0
 */

public abstract class PropertiesService {

    final static String PRO_FILE_NAME = "setting.properties";
    final static String PRO_ADB_PATH = "adb_path";

    public static String getSettingADBPath(){
        return getSettingProperties().getProperty(PRO_ADB_PATH);
    }

    public static void setSettingADBPath(String path){
        Properties pro = getSettingProperties();
        pro.setProperty(PRO_ADB_PATH, path);
        FileOutputStream oFile = null;
        try {
            oFile = new FileOutputStream(PRO_FILE_NAME);
            pro.store(oFile, "app setting");
            oFile.close();
            Log.println("保存ADB地址配置信息到 "+PRO_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getSettingProperties(){
        Properties pro = new Properties();
        File file = new File(PRO_FILE_NAME);
        if(file.exists()){
            Log.println("读取配置文件 "+PRO_FILE_NAME);
            try {
                FileInputStream in = new FileInputStream(PRO_FILE_NAME);
                pro.load(in);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pro;
        }else{
            try {
                boolean r =file.createNewFile();
                if(r) {
                    FileInputStream in = new FileInputStream(PRO_FILE_NAME);
                    pro.load(in);
                    in.close();
                    Log.println("成功创建配置文件 "+PRO_FILE_NAME);
                }else{
                    Log.println(" 配置文件创建失败！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pro;
        }
    }
}
