package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DocAdd implements Initializable {

    @FXML
    private TableView<DocAdded> tvDocsAdd;

    @FXML
    private TableColumn<DocAdded, String> tcDocKeys;

    @FXML
    private TableColumn<DocAdded, String> tcDocValues;

    @FXML
    private Button btnCancelAddDoc;

    @FXML
    private Button btnConfirmAddDoc;
    public static final String TITLE = "文档增加";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DocManage docManage = DocManage.instance;
        IndexManage indexManage = IndexManage.instance;
        tcDocKeys.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDocKeys.setCellValueFactory(cellData -> cellData.getValue().getKeys());
        tcDocValues.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDocValues.setCellValueFactory(cellData -> cellData.getValue().getValues());
        ObservableList<DocAdded> docAddeds = tvDocsAdd.getItems();//必须放在列与数据绑定之后
        for (String string : docManage.result.get(0).keySet()) {
            DocAdded docAdded = new DocAdded();
            docAdded.setKeys(new SimpleStringProperty(string));
            docAdded.setValues(new SimpleStringProperty(""));
            docAddeds.add(docAdded);
        }
        btnConfirmAddDoc.setOnMouseClicked(event -> {
            Map<String, Object> mapAdd = new HashMap<>();
            for (int i = 0; i < docAddeds.size(); i++) {
                mapAdd.put(docAddeds.get(i).getKeys().getValue(), docAddeds.get(i).getValues().getValue());
            }
            Map<String, Map<String, Object>> AddDocs = new HashMap<>();
            AddDocs.put("0", mapAdd);
            int number = indexManage.helper.upsert("sdmap", "_doc", AddDocs);
            System.out.println(number);
            Stage stage = (Stage) btnCancelAddDoc.getScene().getWindow();
            stage.close();
        });
        btnCancelAddDoc.setOnMouseClicked(event -> {
            Stage stage = (Stage) btnCancelAddDoc.getScene().getWindow();
            stage.close();
        });
    }
}

