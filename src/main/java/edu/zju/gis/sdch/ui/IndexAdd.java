package edu.zju.gis.sdch.ui;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
public class IndexAdd implements Initializable {
    @FXML
    public TextField tfIndice;
    @FXML
    public TextField tfShards;
    @FXML
    public TextField tfReplicas;
    @FXML
    public TextField tfCategory;
    @FXML
    public TextField tfDescription;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;

    public static final String TITLE = "添加索引";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IndexManage indexManage = IndexManage.instance;
        btnConfirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Index indexnew = new Index();
                indexnew.getIndice().set(tfIndice.getText());
                indexnew.getShards().set(Integer.parseInt(tfShards.getText()));
                indexnew.getReplicas().set(Integer.parseInt(tfReplicas.getText()));
                indexnew.getCategory().set(tfCategory.getText());
                indexnew.getDescription().set(tfDescription.getText());
                indexManage.tvIndex.getItems().add(indexnew);
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.close();
            }
        });
        btnCancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.close();
            }
        });
    }
}

