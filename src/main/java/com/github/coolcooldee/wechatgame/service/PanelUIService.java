package com.github.coolcooldee.wechatgame.service;

import com.github.coolcooldee.wechatgame.tools.android.AdbToolHelper;
import com.github.coolcooldee.wechatgame.tools.log.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

/**
 * 显示界面、鼠标事件
 *
 * @Description
 * @Author Dee1024 <coolcooldee@gmail.com>
 * @Version 1.0
 * @Since 1.0
 * @Date 2018/1/3
 */

public class PanelUIService extends JFrame {

    public PanelUIService(){
    }

    public void showUI(){
        Log.println("正在绘制UI.");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setBounds(0,0,600,600);
        this.setVisible(true);
        this.add(new MyJPanel());
        refreshUI();
    }

    public void refreshUI(){
        AdbToolHelper.screencap();
        this.getComponent(0).validate();
        this.getComponent(0).repaint();
    }


    class MyJPanel extends JPanel {
        public MyJPanel(){
            this.addMouseListener(getMyMouseListener());
        }
        public void paint(Graphics g){
            Image image = null;
            try {
                image = ImageIO.read(new File(JumpService.getScreencapPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                g.drawImage(image, 0, 0, image.getWidth(null)*8/10, image.getHeight(null)*8/10, null);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        private MouseListener getMyMouseListener(){
            return new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    if(JumpService.getBeginPoint()==null){
                        JumpService.setBeginPoint(e.getPoint());
                    }else{
                        JumpService.setEndPoint(e.getPoint());
                        boolean isok = JumpService.jump(JumpService.getBeginPoint(), JumpService.getEndPoint());
                        JumpService.setEndPoint(null);
                        JumpService.setBeginPoint(null);
                        if(isok) {
                            refreshUI();
                        }
                    }
                }

                public void mousePressed(MouseEvent e) {

                }

                public void mouseReleased(MouseEvent e) {

                }

                public void mouseEntered(MouseEvent e) {

                }

                public void mouseExited(MouseEvent e) {

                }

            };
        }
    }


}
