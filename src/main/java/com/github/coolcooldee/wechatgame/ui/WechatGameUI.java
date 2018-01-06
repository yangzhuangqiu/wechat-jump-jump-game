package com.github.coolcooldee.wechatgame.ui;

import com.github.coolcooldee.wechatgame.service.JumpService;
import com.github.coolcooldee.wechatgame.tools.AdbToolKit;
import com.github.coolcooldee.wechatgame.tools.LogToolKit;

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

public class WechatGameUI extends JFrame {

    static int fwidth = 600;
    static int fheight = 1000;
    static int width = 600; //default
    static int height = 1000; //default

    static double uirate = 1;
    static JLabel beginLable = new JLabel();
    static JLabel endLable = new JLabel();

    public WechatGameUI() {
    }

    public void initGUI() {
        LogToolKit.println("UI 启动中");
        Image image = getImage4WidthAndHeight();
        if (image != null) {
            int tempWidth = image.getWidth(null);
            int tempHeight = image.getHeight(null);
            StringBuffer resulotionStr = new StringBuffer();
            if (tempHeight > tempWidth) {
                resulotionStr.append(tempWidth).append("*").append(tempHeight);
            } else {
                resulotionStr.append(tempHeight).append("*").append(tempWidth);
            }
            LogToolKit.println("当前手机屏幕分辨率：" + resulotionStr);
            Double distance2timeRatio = JumpService.getDistance2timeRatioByResolution(resulotionStr.toString());
            if (distance2timeRatio != null) {
                JumpService.setDistance2timeRatio(distance2timeRatio);
            }
            Double tempUIRate = JumpService.getUIRatioByResolution(resulotionStr.toString());
            if(tempUIRate!=null){
                uirate = tempUIRate;
            }
            fwidth = (int) (tempWidth * uirate);
            fheight = (int) ((tempHeight+200) * uirate);
            width = (int) (tempWidth * uirate);
            height = (int) (tempHeight * uirate);
        }
        this.setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setBounds(0, 0, fwidth, fheight);
        //主面板
        JPanel settingPanel = new SettingJPanel();
        //主界面
        JPanel corePanel = new CoreJPanel();
        this.add(corePanel, BorderLayout.CENTER);
        this.add(settingPanel, BorderLayout.SOUTH);
        refreshCorePanelUI();
    }

    private void refreshCorePanelUI() {
        AdbToolKit.screencap();
        this.getComponent(0).validate();
        this.getComponent(0).repaint();
        beginLable.setText("起跳点：空");
        endLable.setText("目标点：空");
        LogToolKit.println("重新绘制 UI 成功, 等待操作 ...");
    }

    class SettingJPanel extends JPanel {
        public SettingJPanel() {
            this.setBorder(BorderFactory.createTitledBorder("操作"));
            this.setLayout(new BorderLayout());
            beginLable = new JLabel("起跳点：空");
            endLable = new JLabel("目标点：空");
            this.add(beginLable, BorderLayout.WEST);
            this.add(endLable, BorderLayout.EAST);
            this.add(new RefreshButton(), BorderLayout.SOUTH);
            this.setVisible(true);
        }
    }

    class MyLable extends JLabel {
        public MyLable() {

        }
    }

    class RefreshButton extends JButton {
        public RefreshButton() {
            this.setText("刷新UI");
            this.setVisible(true);
            this.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    refreshCorePanelUI();
                }

                public void mousePressed(MouseEvent e) {

                }

                public void mouseReleased(MouseEvent e) {

                }

                public void mouseEntered(MouseEvent e) {

                }

                public void mouseExited(MouseEvent e) {

                }
            });
        }

    }

    class CoreJPanel extends JPanel {

        public CoreJPanel() {
            this.setBorder(BorderFactory.createTitledBorder("主界面"));
            this.setRequestFocusEnabled(true);
            this.addMouseListener(getMyMouseListener());
        }

        public void paintComponent(Graphics g) {

        }

        public void paint(Graphics g) {
            Image image = getImage4WidthAndHeight();
            if (image == null) {
                LogToolKit.println("未找到资源图片，请检查ADB连接是否正常。");
                return;
            }
            g.drawImage(image, 0, 0, width, height, null);
        }

        private MouseListener getMyMouseListener() {
            return new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    LogToolKit.println("点击" + System.currentTimeMillis());
                    //左键
                    if (MouseEvent.BUTTON1 == e.getButton()) {
                        if (JumpService.getBeginPoint().getX() == 0 && JumpService.getBeginPoint().getY() == 0) {
                            beginLable.setText("起跳点：(" + e.getPoint().getX() + "," + e.getPoint().getY() + ")");
                            JumpService.setBeginPoint(e.getPoint());
                        } else {
                            endLable.setText("目标点：(" + e.getPoint().getX() + "," + e.getPoint().getY() + ")");
                            JumpService.setEndPoint(e.getPoint());
                            new Thread(new Runnable() {
                                public void run() {
                                    JumpService.jump(JumpService.getBeginPoint(), JumpService.getEndPoint());
                                    refreshCorePanelUI();
                                    JumpService.setEndPoint(new Point(0, 0));
                                    JumpService.setBeginPoint(new Point(0, 0));
                                }
                            }).start();
                        }
                    }
                    //右键
                    if (MouseEvent.BUTTON3 == e.getButton()) {
                        refreshCorePanelUI();
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

    private static Image getImage4WidthAndHeight() {
        File file = new File(JumpService.getScreencapPath());
        if (file.exists() && file.isFile() && file.length() > 0) {
            Image image = null;
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }
        return null;
    }
}
