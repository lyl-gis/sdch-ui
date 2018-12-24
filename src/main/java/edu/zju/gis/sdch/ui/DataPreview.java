package edu.zju.gis.sdch.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DataPreview implements AllPages, Initializable {
    @FXML
    private Button Dataimport;
    @FXML
    private ProgressBar progressbar;
    @FXML
    private TextField indexxname;
    @FXML
    private TextField indexdescription;

    @FXML
    void dataimport(ActionEvent event) throws IOException {
        //数据入库
        //显示进度条

        progressbar.setProgress(0.7);
        //将失败的数据返回到一个表格中，跳出failedlog界面
        System.out.println("加载前");
        Parent root = FXMLLoader.load(getClass().getResource("Failedlog.fxml"));
        System.out.println("无法加载fxml文件");
        Scene scene = new Scene(root);
        AllPages.failedLogStage.setTitle("入库失败记录");
        AllPages.failedLogStage.setScene(scene);
        AllPages.failedLogStage.show();
    }

    public void initialize(URL location, ResourceBundle resources) {

//        onecolumn.setCellValueFactory(cellData -> cellData.getValue().layernumber);
//        twocolumn.setCellValueFactory(cellData -> cellData.getValue().layername);

    }

}
