package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.model.Index;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
public class IndexAdd implements Initializable {
    @FXML
    public TextField tfIndice;
    @FXML
    private ChoiceBox<Integer> cbShards;
    @FXML
    private ChoiceBox<Integer> cbReplicas;
    @FXML
    private ChoiceBox<String> cbCategory;
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
        cbShards.getItems().addAll(1,2,3,4,5,6,7,8);
        cbReplicas.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7, 8);
        cbCategory.getItems().addAll("xzm","poi","road");
        btnConfirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //表格中显示新增加的内容
                MyIndex indexNew = new MyIndex();
                indexNew.getIndice().set(tfIndice.getText());
                indexNew.getShards().set(cbShards.getValue());
                indexNew.getReplicas().set(cbReplicas.getValue());
                indexNew.getCategory().set(cbCategory.getValue());
                indexNew.getDescription().set(tfDescription.getText());
                indexManage.tvIndex.getItems().add(indexNew);
                //数据库中更新
                Index indexAdd = new Index();
                indexAdd.setIndice(indexNew.getIndice().getValue());
                indexAdd.setShards(indexNew.getShards().getValue());
                indexAdd.setReplicas(indexNew.getReplicas().getValue());
                indexAdd.setCategory(indexNew.getCategory().getValue());
                indexAdd.setDescription(indexNew.getDescription().getValue());
                indexManage.mapper.insert(indexAdd);
                try {
                    indexManage.helper.createIfNotExist(indexAdd.getIndice(), indexAdd.getShards(), indexAdd.getReplicas());//在ES中添加
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

