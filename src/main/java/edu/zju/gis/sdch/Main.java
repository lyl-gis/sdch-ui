package edu.zju.gis.sdch;

import edu.zju.gis.sdch.config.CommonSetting;
import edu.zju.gis.sdch.ui.AllPages;
import edu.zju.gis.sdch.ui.StageManager;
import edu.zju.gis.sdch.util.ElasticSearchHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.gdal.ogr.ogr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class Main extends Application {
    private static CommonSetting setting = new CommonSetting();
    private static ElasticSearchHelper helper;


    public static void main(String[] args) {
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
        Parent root = FXMLLoader.load(getClass().getResource("ui/MainPage.fxml"));
//       Parent root = FXMLLoader.load(getClass().getResource("ui/Test.fxml"));
        Scene scene = new Scene(root);
        AllPages.mainStage.setTitle("数据导入");
        AllPages.mainStage.setScene(scene);
        AllPages.mainStage.show();
        StageManager.STAGE.put("mainStage", AllPages.mainStage);
        StageManager.CONTROLLER.put("mainPage", this);

    }

    private void setMenu(BorderPane root, Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);

        // File menu - new, save, exit
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        fileMenu.getItems().addAll(newMenuItem, saveMenuItem, new SeparatorMenuItem(), exitMenuItem);

        Menu webMenu = new Menu("Web");
        CheckMenuItem htmlMenuItem = new CheckMenuItem("HTML");
        htmlMenuItem.setSelected(true);
        webMenu.getItems().add(htmlMenuItem);

        CheckMenuItem cssMenuItem = new CheckMenuItem("CSS");
        cssMenuItem.setSelected(true);
        webMenu.getItems().add(cssMenuItem);

        Menu sqlMenu = new Menu("SQL");
        ToggleGroup tGroup = new ToggleGroup();
        RadioMenuItem mysqlItem = new RadioMenuItem("MySQL");
        mysqlItem.setToggleGroup(tGroup);

        RadioMenuItem oracleItem = new RadioMenuItem("Oracle");
        oracleItem.setToggleGroup(tGroup);
        oracleItem.setSelected(true);

        sqlMenu.getItems().addAll(mysqlItem, oracleItem, new SeparatorMenuItem());

        Menu tutorialManeu = new Menu("Tutorial");
        tutorialManeu.getItems().addAll(
                new CheckMenuItem("Java"),
                new CheckMenuItem("JavaFX"),
                new CheckMenuItem("Swing"));

        sqlMenu.getItems().add(tutorialManeu);
        menuBar.getMenus().addAll(fileMenu, webMenu, sqlMenu);
    }
}
