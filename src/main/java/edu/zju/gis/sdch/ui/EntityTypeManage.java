package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.mapper.EntityTypeMapper;
import edu.zju.gis.sdch.model.EntityType;
import edu.zju.gis.sdch.util.MyBatisUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EntityTypeManage implements Initializable {

    @FXML
    private BorderPane rootLayout;
    @FXML
    private Button btnSelect;

    @FXML
    private TextField tfCode;

    @FXML
    private TableView<MyEntityType> tvEntityType;

    @FXML
    private TableColumn<MyEntityType, String> tcCode;

    @FXML
    private TableColumn<MyEntityType, String> tcPcode;

    @FXML
    private TableColumn<MyEntityType, String> tcName;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnSaveModify;

    @FXML
    private Button btnRefresh;
    public EntityTypeMapper mapper;
    public static EntityTypeManage instance = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        mapper = MyBatisUtil.getMapper(EntityTypeMapper.class);
        tvEntityType.setEditable(true);
        tfCode.setText("");
        tvEntityType.setRowFactory(param -> new TableRowControl());
        tvEntityType.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//设置表格中数据可以多选
        tcCode.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCode.setCellValueFactory(cellData -> cellData.getValue().getCode());
        tcPcode.setCellFactory(TextFieldTableCell.forTableColumn());
        tcPcode.setCellValueFactory(cellData -> cellData.getValue().getPCode());
        tcName.setCellFactory(TextFieldTableCell.forTableColumn());
        tcName.setCellValueFactory(cellData -> cellData.getValue().getName());

        ObservableList<MyEntityType> myEntityTypes = tvEntityType.getItems();
        //一进入页面所有数据在表格中
        List<EntityType> typeList = mapper.selectAll();
        for (int i = 0; i < typeList.size(); i++) {
            MyEntityType entityType = new MyEntityType();
            entityType.getCode().set(typeList.get(i).getCode());
            entityType.getPCode().set(typeList.get(i).getPCode());
            entityType.getName().set(typeList.get(i).getName());
            myEntityTypes.add(entityType);
        }

        btnSelect.setOnMouseClicked(event -> {
            myEntityTypes.clear();
            String code = tfCode.getText();
            if (code.equals("")) {
                new Alert(Alert.AlertType.
                        INFORMATION, "请输入实体类型代码", ButtonType.OK).showAndWait();
            } else {
                EntityType entityType = mapper.selectByPrimaryKey(code);
                MyEntityType type = new MyEntityType();
                type.getCode().set(entityType.getCode());
                type.getPCode().set(entityType.getPCode());
                type.getName().set(entityType.getName());
                myEntityTypes.add(type);
            }
        });
//刷新按钮显示所有数据
        btnRefresh.setOnMouseClicked(event -> {
            myEntityTypes.clear();
            List<EntityType> typeLists = mapper.selectAll();
            for (int i = 0; i < typeLists.size(); i++) {
                MyEntityType entityType = new MyEntityType();
                entityType.getCode().set(typeLists.get(i).getCode());
                entityType.getPCode().set(typeLists.get(i).getPCode());
                entityType.getName().set(typeLists.get(i).getName());
                myEntityTypes.add(entityType);
            }
        });

        btnAdd.setOnMouseClicked(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("EntityTypeAdd.fxml"));

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                stage.initOwner(primaryStage);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnSaveModify.setOnMouseClicked(event -> {
            int erro = 0;
            for (int i = 0; i < tvEntityType.getItems().size(); i++) {
                EntityType entityType = new EntityType();
                entityType.setName(tvEntityType.getItems().get(i).getName().getValue());
                entityType.setCode(tvEntityType.getItems().get(i).getCode().getValue());
                entityType.setPCode(tvEntityType.getItems().get(i).getPCode().getValue());
                int result = mapper.updateByPrimaryKey(entityType);
                if (result != 1)
                    erro++;
            }
            new Alert(Alert.AlertType.
                    INFORMATION, "更新失败数目" + erro + "条", ButtonType.OK).showAndWait();
        });
    }

    class TableRowControl extends TableRow<MyEntityType> {
        TableRowControl() {
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.SECONDARY)
                            && event.getClickCount() == 1
                            && TableRowControl.this.getIndex() < tvEntityType.getItems().size()) {
                        final ContextMenu contextMenu = new ContextMenu();
                        MenuItem deleteAdminArea = new MenuItem("删除");
                        contextMenu.getItems().addAll(deleteAdminArea);
                        deleteAdminArea.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "确认删除?");
                                Optional<ButtonType> optional = confirmation.showAndWait();
                                if (optional.isPresent() && optional.get() == ButtonType.OK) {
                                    int result = 0;
                                    for (int i = 0; i < tvEntityType.getSelectionModel().getSelectedItems().size(); i++) {
                                        int curresult = mapper.deleteByPrimaryKey(tvEntityType.getSelectionModel().getSelectedItems().get(i).getCode().getValue());
                                        result = result + curresult;
                                        tvEntityType.getItems().remove(tvEntityType.getSelectionModel().getSelectedItems().get(i));//在表中删除只要这一句话即可
                                    }
                                    new Alert(Alert.AlertType.
                                            INFORMATION, "成功删除" + result + "条数据", ButtonType.OK).showAndWait();

                                }
                            }
                        });
                        tvEntityType.setContextMenu(contextMenu);
                    }
                }
            });
        }
    }
}
