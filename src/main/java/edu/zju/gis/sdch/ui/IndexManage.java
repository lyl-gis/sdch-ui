package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.mapper.IndexMapper;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.util.MyBatisUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    public TableView<MyIndex> tvIndex;
    @FXML
    private TableColumn<MyIndex, String> tcIndice;
    @FXML
    private TableColumn<MyIndex, Number> tcShards;
    @FXML
    private TableColumn<MyIndex, Number> tcReplicas;
    @FXML
    private TableColumn<MyIndex, String> tcDescription;
    @FXML
    private TableColumn<MyIndex, String> tcCategory;
    @FXML
    private Button btnConfirmDelete;
    @FXML
    private Button btnSaveModified;
    public static final String TITLE = "索引管理";
    public static IndexManage instance = null;
    public IndexMapper mapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        mapper = MyBatisUtil.getMapper(IndexMapper.class);
        btnConfirmDelete.setVisible(false);
        btnSaveModified.setVisible(false);
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
        ObservableList<MyIndex> indices = tvIndex.getItems();//必须放在列与数据绑定之后
        btnSelect.setOnMouseClicked(event -> {
            indices.clear();
            List<Index> indexList=mapper.selectAll();
            for(int i=0;i<indexList.size();i++){
                MyIndex index = new MyIndex();
                index.getDescription().set(indexList.get(i).getDescription());
                index.getCategory().set(indexList.get(i).getCategory());
                index.getReplicas().set(indexList.get(i).getReplicas());
                index.getShards().set(indexList.get(i).getShards());
                index.getIndice().set(indexList.get(i).getIndice());
                indices.add(index);
            }
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
            TableColumn<MyIndex, Boolean> tcDeleted = new TableColumn<>("是否删除");
            tcDeleted.setCellFactory(CheckBoxTableCell.forTableColumn(tcDeleted));
            tcDeleted.setCellValueFactory(o -> o.getValue().getDeleted());
            tvIndex.getColumns().add(tcDeleted);//在表中增加一列
            new Alert(Alert.AlertType.INFORMATION, "在要删除的内容后打钩", ButtonType.OK)
                    .showAndWait();
            btnConfirmDelete.setVisible(true);
        });

        btnConfirmDelete.setOnMouseClicked(event -> {

            for (int i = 0; i <tvIndex.getItems().size(); i++) {
                if (tvIndex.getItems().get(i).getDeleted().getValue() == true) {
                   mapper.deleteByPrimaryKey(tvIndex.getItems().get(i).getIndice().getValue());
                    tvIndex.getItems().remove(i);//在表中删除打钩的那一行
                    i--;
                }
            }
            tvIndex.getColumns().remove(tvIndex.getColumns().size() - 1);//删去新增的一列
            btnConfirmDelete.setVisible(false);
        });
        btnModified.setOnMouseClicked(event -> {
            new Alert(Alert.AlertType.INFORMATION, "进入可编辑状态,在要编辑的内容后面打钩", ButtonType.OK)
                    .showAndWait();
            tcIndice.setEditable(true);
            tcShards.setEditable(true);
            tcCategory.setEditable(true);
            tcCategory.setEditable(true);
            tcDescription.setEditable(true);
            btnSaveModified.setVisible(true);
            TableColumn<MyIndex, Boolean> tcModified = new TableColumn<>("是否修改");
            tcModified.setCellFactory(CheckBoxTableCell.forTableColumn(tcModified));
            tcModified.setCellValueFactory(o -> o.getValue().getModified());
            tvIndex.getColumns().add(tcModified);//在表中增加一列

        });
        btnSaveModified.setOnMouseClicked(event->{
            for (int i = 0; i < tvIndex.getItems().size(); i++) {
                if (tvIndex.getItems().get(i).getModified().getValue() == true) {
                    Index index = new Index();
                    index.setDescription(tvIndex.getItems().get(i).getDescription().getValue());
                    index.setCategory(tvIndex.getItems().get(i).getCategory().getValue());
                    index.setReplicas(tvIndex.getItems().get(i).getReplicas().getValue());
                    index.setShards(tvIndex.getItems().get(i).getShards().getValue());
                    index.setIndice(tvIndex.getItems().get(i).getIndice().getValue());
                    mapper.updateByPrimaryKey(index);
                }
            }
            tvIndex.getColumns().remove(tvIndex.getColumns().size() - 1);//删去新增的一列
            btnSaveModified.setVisible(false);
        });
    }
}

