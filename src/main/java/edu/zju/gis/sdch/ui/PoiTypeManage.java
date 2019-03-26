package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.mapper.PoiTypeMapper;
import edu.zju.gis.sdch.model.PoiType;
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

public class PoiTypeManage implements Initializable {
    @FXML
    private BorderPane rootLayout;
    @FXML
    private Button btnSelect;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnSaveModify;

    @FXML
    private Button btnRefresh;

    @FXML
    private TextField tfCode;

    @FXML
    private TableView<MyPoiType> tvPoiType;

    @FXML
    private TableColumn<MyPoiType, String> tcCode;

    @FXML
    private TableColumn<MyPoiType, String> tcPcode;

    @FXML
    private TableColumn<MyPoiType, String> tcCode4;

    @FXML
    private TableColumn<MyPoiType, String> tcName;
    public PoiTypeMapper mapper;
    public static PoiTypeManage instance = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        mapper = MyBatisUtil.getMapper(PoiTypeMapper.class);
        tvPoiType.setEditable(true);
        tfCode.setText("");
        tvPoiType.setRowFactory(param -> new TableRowControl());
        tvPoiType.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//设置表格中数据可以多选
        tcCode.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCode.setCellValueFactory(cellData -> cellData.getValue().getCode());
        tcPcode.setCellFactory(TextFieldTableCell.forTableColumn());
        tcPcode.setCellValueFactory(cellData -> cellData.getValue().getPCode());
        tcCode4.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCode4.setCellValueFactory(cellData -> cellData.getValue().getCode4());
        tcName.setCellFactory(TextFieldTableCell.forTableColumn());
        tcName.setCellValueFactory(cellData -> cellData.getValue().getName());

        ObservableList<MyPoiType> myPoiTypes = tvPoiType.getItems();
        //一进入页面所有数据在表格中
        List<PoiType> typeList = mapper.selectAll();
        for (int i = 0; i < typeList.size(); i++) {
            MyPoiType poiType = new MyPoiType();
            poiType.getCode().set(typeList.get(i).getCode());
            poiType.getPCode().set(typeList.get(i).getPCode());
            poiType.getCode4().set(typeList.get(i).getCode4());
            poiType.getName().set(typeList.get(i).getName());
            myPoiTypes.add(poiType);
        }
        btnSelect.setOnMouseClicked(event -> {
            myPoiTypes.clear();
            String code = tfCode.getText();
            if (code.equals("")) {
                new Alert(Alert.AlertType.
                        INFORMATION, "请输入poi类型代码", ButtonType.OK).showAndWait();
            } else {
                PoiType poiType = mapper.selectByPrimaryKey(code);
                MyPoiType type = new MyPoiType();
                type.getCode().set(poiType.getCode());
                type.getCode4().set(poiType.getCode());
                type.getPCode().set(poiType.getPCode());
                type.getName().set(poiType.getName());
                myPoiTypes.add(type);
            }
        });
        btnRefresh.setOnMouseClicked(event -> {
            myPoiTypes.clear();
            List<PoiType> typeLists = mapper.selectAll();
            for (int i = 0; i < typeLists.size(); i++) {
                MyPoiType poiType = new MyPoiType();
                poiType.getCode().set(typeLists.get(i).getCode());
                poiType.getPCode().set(typeLists.get(i).getPCode());
                poiType.getCode4().set(typeLists.get(i).getCode4());
                poiType.getName().set(typeLists.get(i).getName());
                myPoiTypes.add(poiType);
            }
        });
        btnAdd.setOnMouseClicked(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("PoiTypeAdd.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                stage.initOwner(primaryStage);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        btnSaveModify.setOnMouseClicked(event -> {
            int erro = 0;
            for (int i = 0; i < tvPoiType.getItems().size(); i++) {
                PoiType poiType = new PoiType();
                poiType.setName(tvPoiType.getItems().get(i).getName().getValue());
                poiType.setCode(tvPoiType.getItems().get(i).getCode().getValue());
                poiType.setCode4(tvPoiType.getItems().get(i).getCode4().getValue());
                poiType.setPCode(tvPoiType.getItems().get(i).getPCode().getValue());
                int result = mapper.updateByPrimaryKey(poiType);
                if (result != 1)
                    erro++;
            }
            new Alert(Alert.AlertType.
                    INFORMATION, "更新失败数目" + erro + "条", ButtonType.OK).showAndWait();
        });
    }

    class TableRowControl extends TableRow<MyPoiType> {
        TableRowControl() {
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.SECONDARY)
                            && event.getClickCount() == 1
                            && TableRowControl.this.getIndex() < tvPoiType.getItems().size()) {
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
                                    for (int i = 0; i < tvPoiType.getSelectionModel().getSelectedItems().size(); i++) {
                                        int curresult = mapper.deleteByPrimaryKey(tvPoiType.getSelectionModel().getSelectedItems().get(i).getCode().getValue());
                                        result = result + curresult;
                                        tvPoiType.getItems().remove(tvPoiType.getSelectionModel().getSelectedItems().get(i));//在表中删除只要这一句话即可
                                    }
                                    new Alert(Alert.AlertType.
                                            INFORMATION, "成功删除" + result + "条数据", ButtonType.OK).showAndWait();

                                }
                            }
                        });
                        tvPoiType.setContextMenu(contextMenu);
                    }
                }
            });
        }
    }
}
