package edu.zju.gis.sdch.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPort implements Initializable {
    @FXML
    private MenuBar mbSelectPage;
    @FXML
    private BorderPane rootLayout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Menu toolMenu = new Menu("工具");
        Menu manageMenu = new Menu("管理");
        mbSelectPage.getMenus().addAll(toolMenu, manageMenu);
        MenuItem toolMenuImport = new MenuItem("入库工具");
        MenuItem manageMenuIndex = new MenuItem("索引管理");
        MenuItem adminAreaMenuIndex = new MenuItem("政区信息管理");
        MenuItem entityTypeMenuIndex = new MenuItem("实体类型管理");
        MenuItem poiTypeMenuIndex = new MenuItem("POI类型管理");
        toolMenu.getItems().add(toolMenuImport);
        manageMenu.getItems().addAll(manageMenuIndex, adminAreaMenuIndex, entityTypeMenuIndex, poiTypeMenuIndex);
        toolMenuImport.setOnAction(evnet -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                stage.initOwner(primaryStage);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        manageMenuIndex.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("IndexManage.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                stage.initOwner(primaryStage);
                stage.setScene(scene);
                stage.setTitle("索引管理");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        adminAreaMenuIndex.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("AdminAreaManage.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                stage.initOwner(primaryStage);
                stage.setScene(scene);
                stage.setTitle("政区信息管理");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        entityTypeMenuIndex.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("EntityTypeManage.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                stage.initOwner(primaryStage);
                stage.setTitle("实体类型管理");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        poiTypeMenuIndex.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("PoiTypeManage.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                stage.initOwner(primaryStage);
                stage.setScene(scene);
                stage.setTitle("POI类型管理");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
