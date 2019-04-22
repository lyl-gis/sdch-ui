package edu.zju.gis.sdch;

import javax.swing.*;
import java.awt.*;

public class Start {
    static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame();
        Container container = frame.getContentPane();
        container.setLayout(null);
        ImageIcon icon1 = new ImageIcon(ClassLoader.getSystemResource("2.png"));
        JLabel jLabel = new JLabel(icon1);
        jLabel.setSize(icon1.getIconWidth(), icon1.getIconHeight());
        jLabel.setLocation(0, 0);
        container.add(jLabel);
        jLabel.setIcon(icon1);
        frame.pack();
        frame.setSize(icon1.getIconWidth(), icon1.getIconHeight());
        int windowWidth = frame.getWidth();                    //获得窗口宽
        int windowHeight = frame.getHeight();                  //获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit();             //定义工具包
        Dimension screenSize = kit.getScreenSize();            //获取屏幕的尺寸
        int screenWidth = screenSize.width;                    //获取屏幕的宽
        int screenHeight = screenSize.height;                  //获取屏幕的高
        frame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);//设置窗口居中显示
        frame.setVisible(true);
        Main.main(new String[]{});
    }

}
