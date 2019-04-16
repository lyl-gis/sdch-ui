package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.model.AdminArea;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminAreaAdd implements Initializable {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnConfirm;
    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfpCode;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfAbb;

    @FXML
    private TextField tfFullname;

    @FXML
    private TextField tfLat;

    @FXML
    private TextField tfLon;

    @FXML
    private TextField tfWKT;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AdminAreaManage adminAreaManage = AdminAreaManage.instance;
        tfAbb.setText("");
        tfCode.setText("");
        tfFullname.setText("");
        tfLat.setText("0");
        tfLon.setText("0");
        tfWKT.setText("");
        tfpCode.setText("");
        tfName.setText("");
        btnCancel.setOnMouseClicked(event -> {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        });
        btnConfirm.setOnMouseClicked(event -> {
            AdminArea adminArea = new AdminArea();
            adminArea.setAbbreviation(tfAbb.getText());
            adminArea.setCode(tfCode.getText());
            adminArea.setPCode(tfpCode.getText());
            adminArea.setFullName(tfFullname.getText());
            adminArea.setName(tfName.getText());
            adminArea.setWkt(tfWKT.getText());
            adminArea.setLat(new BigDecimal(tfLat.getText()));
            adminArea.setLon(new BigDecimal(tfLon.getText()));
            MyAdminArea myAdminArea = new MyAdminArea();
            myAdminArea.setAbbreviation(new SimpleStringProperty(tfAbb.getText()));
            myAdminArea.setCode(new SimpleStringProperty(tfCode.getText()));
            myAdminArea.setPCode(new SimpleStringProperty(tfpCode.getText()));
            myAdminArea.setFullName(new SimpleStringProperty(tfFullname.getText()));
            myAdminArea.setName(new SimpleStringProperty(tfName.getText()));
            myAdminArea.setWkt(new SimpleStringProperty(tfWKT.getText()));
            myAdminArea.setLat(new SimpleFloatProperty(Float.parseFloat(tfLat.getText())));
            myAdminArea.setLon(new SimpleFloatProperty(Float.parseFloat(tfLon.getText())));
            adminAreaManage.tvAdminArea.getItems().add(myAdminArea);
            int result = adminAreaManage.mapper.insert(adminArea);
            new Alert(Alert.AlertType.
                    INFORMATION, "成功插入" + result + "条数据", ButtonType.OK).showAndWait();
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        });
    }
}
