package com.github.coolcooldee.wechatgame;

/**
 * @Description
 * @Author Dee1024 <coolcooldee@gmail.com>
 * @Version 1.0
 * @Since 1.0
 * @Date 2018/1/3
 */

import com.github.coolcooldee.wechatgame.service.PanelUIService;
import com.github.coolcooldee.wechatgame.tools.android.AdbToolHelper;

/**
 * 应用启动
 */
public class Application {
    public static void main(String[] args) {
        AdbToolHelper.init();
        new PanelUIService().initGUI();
        //JumpService.init();
    }

}
