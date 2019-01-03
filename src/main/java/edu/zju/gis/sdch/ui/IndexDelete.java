package edu.zju.gis.sdch.ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class IndexDelete implements Initializable {
    public static final String TITLE = "删除索引";

    @FXML
    private ChoiceBox<String> cbIndexLIst;


    @FXML
    private Button btnConfirmDelete;

    @FXML
    private Button btnCancelDelete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IndexManage indexManage = IndexManage.instance;
        for (int i = 0; i < indexManage.tvIndex.getItems().size(); i++)
            cbIndexLIst.getItems().add(indexManage.tvIndex.getItems().get(i).getIndice().getValue());
        cbIndexLIst.setValue("");
        btnConfirmDelete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (cbIndexLIst.getValue().equals("")) {
                    new Alert(Alert.AlertType.INFORMATION, "请选择要删除的索引名称", ButtonType.OK)
                            .showAndWait();
                } else {
                    for (int i = 0; i < indexManage.tvIndex.getItems().size(); i++)
                        if (cbIndexLIst.getValue().equals(indexManage.tvIndex.getItems().get(i).getIndice().getValue())) {
                            indexManage.tvIndex.getItems().remove(i);
                        }
                    Stage stage = (Stage) btnCancelDelete.getScene().getWindow();
                    stage.close();
                }
            }
        });
        btnCancelDelete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) btnCancelDelete.getScene().getWindow();
                stage.close();
            }
        });
    }
}

