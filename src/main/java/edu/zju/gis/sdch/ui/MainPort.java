package edu.zju.gis.sdch.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPort implements Initializable {
    @FXML
    private MenuBar mbSelectPage;

    @FXML
    private ImageView IVimage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Menu toolMenu = new Menu("工具");
        Menu manageMenu = new Menu("管理");
        mbSelectPage.getMenus().addAll(toolMenu, manageMenu);
        MenuItem toolMenuImport = new MenuItem("入库工具");
        MenuItem manageMenuIndex = new MenuItem("索引管理");
//        MenuItem manageMenuDocs = new MenuItem("poi管理");
        toolMenu.getItems().add(toolMenuImport);
//      manageMenu.getItems().addAll(manageMenuIndex, manageMenuDocs);
        manageMenu.getItems().addAll(manageMenuIndex);
        toolMenuImport.setOnAction(evnet -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });
        manageMenuIndex.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("IndexManage.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });
    }
}
