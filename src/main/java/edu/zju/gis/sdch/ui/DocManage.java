package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class DocManage implements Initializable {
    @FXML
    private Button btnSelectDoc;

    @FXML
    private TextField tfSelectedIndex;

    @FXML
    private Button btnAddDoc;

    @FXML
    private Button btnDeleteDoc;

    @FXML
    private Button btnModifyDoc;

    @FXML
    private Button btnConfirmDeletedocs;

    @FXML
    private Button btnSaveModifieddocs;
    @FXML
    private TableView<Map<String, Object>> tvDocs;
    public static final String TITLE = "文档编辑";
    static public List<Map<String, Object>> result;
    public static DocManage instance = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        IndexManage indexManage = IndexManage.instance;
        btnSelectDoc.setOnMouseClicked(event -> {
            try {
                result = indexManage.helper.getAsMap("sdmap", "_doc", 0, 5);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<TableColumn<Map<String, Object>, String>> tableColumns = new ArrayList<>();
            //将查到的数据显示在表格中
            //result的size是一共有多少行，result中的每条记录中是个map，map中key的数量是一共有多少列
            Map<String, Object> map = result.get(0);
            for (String string : map.keySet()) {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(string);
                tableColumns.add(column);
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setCellValueFactory(param -> {
                    String value = "";
                    for (String string2 : param.getValue().keySet())
                        if (string.equals(string2)) {
                            value = param.getValue().get(string2).toString();
                        }
                    return new SimpleStringProperty(value);
                });
            }
            tvDocs.getColumns().clear();
            tvDocs.getColumns().addAll(tableColumns);
            tvDocs.getItems().clear();
            for (int i = 0; i < result.size(); i++)
                tvDocs.getItems().add(result.get(i));
        });
        btnAddDoc.setOnMouseClicked(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("DocAdd.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(DocAdd.TITLE);
            stage.setScene(scene);
            stage.show();
        });
        btnDeleteDoc.setOnMouseClicked(event -> {
            TableColumn<Map<String, Object>, Boolean> tcDeletedDoc = new TableColumn<>("是否删除");
            for (int i = 0; i < result.size(); i++) {
                result.get(i).put("是否删除", new Docs());
            }
            Map<String, Object> map = result.get(0);
            tcDeletedDoc.setCellFactory(CheckBoxTableCell.forTableColumn(tcDeletedDoc));
            tcDeletedDoc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, Boolean>, ObservableValue<Boolean>>() {
                Docs deleteddocs = new Docs();

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Map<String, Object>, Boolean> param) {
                    for (String string : param.getValue().keySet()) {
                        if (string.equals("是否删除"))
                            deleteddocs = (Docs) param.getValue().get(string);
                    }
                    return deleteddocs.getIfDeleted();
                }
            });
            tvDocs.getColumns().add(tcDeletedDoc);//在表中增加一列
            new Alert(Alert.AlertType.INFORMATION, "在要删除的内容后打钩", ButtonType.OK)
                    .showAndWait();

        });
        btnConfirmDeletedocs.setOnMouseClicked(event -> {
            for (int i = 0; i < tvDocs.getItems().size(); i++) {
                if (((Docs) tvDocs.getItems().get(i).get("是否删除")).getIfDeleted().getValue()) {
                    //在ES中删除
                    Boolean bool = indexManage.helper.delete("sdmap", "_doc", tvDocs.getItems().get(i).get("lsid").toString());
                    System.out.println(bool);
                    tvDocs.getItems().remove(i);//在表中删除打钩的那一行
                    i--;
                }
            }
            tvDocs.getColumns().remove(tvDocs.getColumns().size() - 1);//删去新增的一列
        });
        btnModifyDoc.setOnMouseClicked(event -> {
            TableColumn<Map<String, Object>, Boolean> tcModifiedDoc = new TableColumn<>("是否修改");
            for (int i = 0; i < result.size(); i++) {
                result.get(i).put("是否修改", new Docs());
            }
            Map<String, Object> map = result.get(0);
            tcModifiedDoc.setCellFactory(CheckBoxTableCell.forTableColumn(tcModifiedDoc));
            tcModifiedDoc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, Boolean>, ObservableValue<Boolean>>() {
                Docs modifidDocs = new Docs();

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Map<String, Object>, Boolean> param) {
                    for (String string : param.getValue().keySet()) {
                        if (string.equals("是否修改"))
                            modifidDocs = (Docs) param.getValue().get(string);
                    }
                    return modifidDocs.ifModified;
                }
            });
            tvDocs.getColumns().add(tcModifiedDoc);//在表中增加一列
            new Alert(Alert.AlertType.INFORMATION, "在要编辑的内容后打钩", ButtonType.OK)
                    .showAndWait();
        });
        //修改docs的部分还有问题，无法得到修改后的值
        btnSaveModifieddocs.setOnMouseClicked(event -> {
            Map<String, Map<String, Object>> kvIdDoc = new HashMap<>();
            for (int i = 0; i < tvDocs.getItems().size(); i++) {
                if (((Docs) tvDocs.getItems().get(i).get("是否修改")).getIfModified().getValue()) {
                    kvIdDoc.put(" ", tvDocs.getItems().get(i));
                }
            }
            int number = indexManage.helper.updateFromMap("sdmap", "_doc", kvIdDoc);
            System.out.println(number);
            tvDocs.getColumns().remove(tvDocs.getColumns().size() - 1);//删去新增的一列
        });
    }
}