package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.model.PoiType;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PoiTypeAdd implements Initializable {
    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfPcode;

    @FXML
    private TextField tfCode4;

    @FXML
    private TextField tfName;

    @FXML
    private Button btnConfirm;

    @FXML
    private Button btnCancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PoiTypeManage poiTypeManage = PoiTypeManage.instance;
        tfCode.setText("");
        tfCode4.setText("");
        tfPcode.setText("");
        tfName.setText("");
        btnCancel.setOnMouseClicked(event -> {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        });
        btnConfirm.setOnMouseClicked(event -> {
            if (tfCode4.getText().length() == 0 || tfCode4.getText().length() == 4) {
                PoiType poiType = new PoiType();
                poiType.setCode(tfCode.getText());
                poiType.setCode4(tfCode4.getText());
                poiType.setPCode(tfPcode.getText());
                poiType.setName(tfName.getText());
                MyPoiType myPoiType = new MyPoiType();
                myPoiType.setCode(new SimpleStringProperty(tfCode.getText()));
                myPoiType.setCode4(new SimpleStringProperty(tfCode4.getText()));
                myPoiType.setPCode(new SimpleStringProperty(tfPcode.getText()));
                myPoiType.setName(new SimpleStringProperty(tfName.getText()));
                poiTypeManage.tvPoiType.getItems().add(myPoiType);
                int result = poiTypeManage.mapper.insert(poiType);
                new Alert(Alert.AlertType.
                        INFORMATION, "成功插入" + result + "条数据", ButtonType.OK).showAndWait();
                Stage stage = (Stage) btnConfirm.getScene().getWindow();
                stage.close();
            } else {
                new Alert(Alert.AlertType.
                        INFORMATION, "code4必须为0或4位编码", ButtonType.OK).showAndWait();
            }
        });
    }
}
