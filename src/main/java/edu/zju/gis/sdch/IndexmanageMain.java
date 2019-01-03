package edu.zju.gis.sdch;

import edu.zju.gis.sdch.ui.IndexManage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IndexmanageMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ui/IndexManage.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle(IndexManage.TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

