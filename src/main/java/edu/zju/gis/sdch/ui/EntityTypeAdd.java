package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.model.EntityType;
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

public class EntityTypeAdd implements Initializable {
    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfpCode;

    @FXML
    private TextField tfName;

    @FXML
    private Button btnConfirm;

    @FXML
    private Button btnCancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EntityTypeManage entityTypeManage = EntityTypeManage.instance;
        tfCode.setText("");
        tfpCode.setText("");
        tfName.setText("");
        btnCancel.setOnMouseClicked(event -> {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        });
        btnConfirm.setOnMouseClicked(event -> {
            EntityType entityType = new EntityType();
            entityType.setCode(tfCode.getText());
            entityType.setPCode(tfpCode.getText());
            entityType.setName(tfName.getText());

            MyEntityType myEntityType = new MyEntityType();
            myEntityType.setCode(new SimpleStringProperty(tfCode.getText()));
            myEntityType.setPCode(new SimpleStringProperty(tfpCode.getText()));
            myEntityType.setName(new SimpleStringProperty(tfName.getText()));
            entityTypeManage.tvEntityType.getItems().add(myEntityType);
            int result = entityTypeManage.mapper.insert(entityType);
            new Alert(Alert.AlertType.
                    INFORMATION, "成功插入" + result + "条数据", ButtonType.OK).showAndWait();
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        });

    }

}

