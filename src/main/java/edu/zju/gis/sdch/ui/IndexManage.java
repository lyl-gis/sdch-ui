package edu.zju.gis.sdch.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class IndexManage implements Initializable {
    @FXML
    private Button btnSelect;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnModified;
    @FXML
    private Button btnDelete;
    @FXML
    public TableView<Index> tvIndex;
    @FXML
    private TableColumn<Index, String> tcIndice;
    @FXML
    private TableColumn<Index, Number> tcShards;
    @FXML
    private TableColumn<Index, Number> tcReplicas;
    @FXML
    private TableColumn<Index, String> tcDescription;
    @FXML
    private TableColumn<Index, String> tcCategory;
    public static final String TITLE = "索引管理";
    public static IndexManage instance = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        tcIndice.setCellFactory(TextFieldTableCell.forTableColumn());
        tcIndice.setCellValueFactory(cellData -> cellData.getValue().getIndice());
        tcShards.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        tcShards.setCellValueFactory(cellData -> cellData.getValue().getShards());
        tcReplicas.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        tcReplicas.setCellValueFactory(cellData -> cellData.getValue().getReplicas());
        tcCategory.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCategory.setCellValueFactory(cellData -> cellData.getValue().getCategory());
        tcDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescription.setCellValueFactory(cellData -> cellData.getValue().getDescription());
        tcIndice.setEditable(false);
        tcShards.setEditable(false);
        tcCategory.setEditable(false);
        tcCategory.setEditable(false);
        tcDescription.setEditable(false);
        //将数据库中的索引信息呈现在表格中
        ObservableList<Index> indices = tvIndex.getItems();//必须放在列与数据绑定之后
        btnSelect.setOnMouseClicked(event -> {
            indices.clear();
            Index index1 = new Index();
            index1.getDescription().set("描述");
            index1.getCategory().set("类别");
            index1.getReplicas().set(1);
            index1.getShards().set(4);
            index1.getIndice().set("新索引");
            indices.add(index1);

        });
        btnAdd.setOnMouseClicked(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("IndexAdd.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(DataPreview.TITLE);
            stage.setScene(scene);
            stage.show();
        });
        btnDelete.setOnMouseClicked(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("IndexDelete.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(DataPreview.TITLE);
            stage.setScene(scene);
            stage.show();
        });
        btnModified.setOnMouseClicked(event -> {
            new Alert(Alert.AlertType.INFORMATION, "进入可编辑状态", ButtonType.OK)
                    .showAndWait();
            tcIndice.setEditable(true);
            tcShards.setEditable(true);
            tcCategory.setEditable(true);
            tcCategory.setEditable(true);
            tcDescription.setEditable(true);
        });
    }
}

