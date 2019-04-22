package edu.zju.gis.sdch;

import edu.zju.gis.sdch.config.CommonSetting;
import edu.zju.gis.sdch.ui.MainPage;
import edu.zju.gis.sdch.ui.MainPort;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.gdal.ogr.ogr;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class Main extends Application {
    private static CommonSetting setting = new CommonSetting();
    private static ElasticSearchHelper helper;
    private static JFrame frame;

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
        ogr.RegisterAll();
        Properties props = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream("config.properties")) {
            props.load(is);
            setting.setEsName(props.getProperty("es.name", "elasticsearch"));
            setting.setEsHosts(Arrays.asList(props.getProperty("es.hosts").split(",")));
            setting.setEsPort(Integer.parseInt(props.getProperty("es.port", "9300")));
            setting.setEsShards(Integer.parseInt(props.getProperty("es.number_of_shards", "4")));
            setting.setEsReplicas(Integer.parseInt(props.getProperty("es.number_of_replicas", "0")));
            setting.setEsFieldBoostDefault(Float.parseFloat(props.getProperty("es.field_boost_default", "4.0f")));
            helper = new ElasticSearchHelper(setting.getEsHosts(), setting.getEsPort(), setting.getEsName());
        } catch (IOException e) {
            throw new RuntimeException("读取配置文件config.properties异常", e);
        }
        launch(args);
    }

    public static CommonSetting getSetting() {
        return setting;
    }

    public static ElasticSearchHelper getHelper() {
        return helper;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ui/MainPort.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle(MainPage.TITLE);
        primaryStage.setScene(scene);
        if (MainPort.ifConnect) {
            primaryStage.show();
        }
        frame.dispose();
    }
}
