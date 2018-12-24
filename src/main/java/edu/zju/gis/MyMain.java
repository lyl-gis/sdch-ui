package edu.zju.gis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MyMain extends Application implements Allpages {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
        Scene scene = new Scene(root);
        p1.mainstage.setTitle("数据导入");
        p1.mainstage.setScene(scene);
        p1.mainstage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
