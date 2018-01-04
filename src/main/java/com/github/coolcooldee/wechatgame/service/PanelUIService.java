package com.github.coolcooldee.wechatgame.service;

import com.github.coolcooldee.wechatgame.tools.android.AdbToolHelper;
import com.github.coolcooldee.wechatgame.tools.log.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

    public void initGUI(){
        Log.println("UI 启动成功.");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setBounds(0,0,600,1280);
        this.setVisible(true);
        this.setResizable(true);
        this.add(new MyJPanel());
        refreshUI();
    }

    private void refreshUI(){
        AdbToolHelper.screencap();
        this.getComponent(0).validate();
        this.getComponent(0).repaint();
        Log.println("重新绘制 UI 成功, 等待操作 ...");
    }

    class MyJPanel extends JScrollPane {
        public MyJPanel(){
            this.addMouseListener(getMyMouseListener());
        }

        public void paint(Graphics g){
            String filePath = JumpService.genAndGetScreencapPath();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File file = new File(filePath);
            if(file.exists()) {
                Image image = null;
                try {
                    image = ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    int width = image.getWidth(null);
                    int height = image.getHeight(null);
                    StringBuffer resulotionStr = new StringBuffer();
                    if(height>width){
                        resulotionStr.append(width).append("*").append(height);
                    }else{
                        resulotionStr.append(height).append("*").append(width);
                    }
                    Log.println("屏幕分辨率："+resulotionStr);
                    Double distance2timeRatio = JumpService.getDistance2timeRatioByResolution(resulotionStr.toString());
                    if(distance2timeRatio!=null){
                        JumpService.setDistance2timeRatio(distance2timeRatio);
                    }

                    g.drawImage(image, 0, 0, width, height, null);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }else{
                Log.println("未找到截图");
            }
        }

        private MouseMotionListener getMouseMotionListener() {
            return new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {

                }

                public void mouseMoved(MouseEvent e) {
                    Log.println("当前坐标("+e.getX()+","+e.getY()+")");
                }
            };
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


    class MousePanel extends JPanel
    {
        int x_pos,y_pos;
        public int getPointX()
        {
            return x_pos;
        }
        public int getPointY()
        {
            return y_pos;
        }
        public MousePanel()
        {
            addMouseListener(new MouseListener()
            {
                //mouseClicked():鼠标单击
                public void mouseClicked(MouseEvent e)
                {
                    x_pos=e.getX();
                    y_pos=e.getY();
                    repaint();
                }
                //mouseEntered():鼠标进入时
                public void mouseEntered(MouseEvent e)
                {
                }
                //mouseExited():鼠标离开时
                public void mouseExited(MouseEvent e)
                {
                }
                //mousePressed():鼠标按下去
                public void mousePressed(MouseEvent e)
                {

                }
                //mouseReleased():鼠标松开时
                public void mouseReleased(MouseEvent e) {}
            });
            addMouseMotionListener(new MouseMotionListener()
            {
                public void mouseMoved(MouseEvent e)
                {
                }
                public void mouseDragged(MouseEvent e){}
            });
        }
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            //g.drawString("current location is:["+x_pos+","+y_pos+"]",x_pos,y_pos);//在界面上显示
            System.out.printf("current location is:["+x_pos+","+y_pos+"]\n",x_pos,y_pos);//在控制台显示
            g.setColor(Color.RED);
            g.drawRect(x_pos-25, y_pos-25, 50, 50);
            //g.fillOval(x_pos,y_pos,8,8);
        }
    }


}
