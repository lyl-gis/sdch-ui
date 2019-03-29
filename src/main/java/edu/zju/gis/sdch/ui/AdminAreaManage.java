package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.mapper.AdminAreaMapper;
import edu.zju.gis.sdch.model.AdminArea;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminAreaManage implements Initializable {
    @FXML
    private BorderPane rootLayout;
    @FXML
    private TableView<MyAdminArea> tvAdminArea;
    @FXML
    private TableColumn<MyAdminArea, String> tcCode;

    @FXML
    private TableColumn<MyAdminArea, String> tcPcode;

    @FXML
    private TableColumn<MyAdminArea, String> tcName;

    @FXML
    private TableColumn<MyAdminArea, String> tcAbbre;

    @FXML
    private TableColumn<MyAdminArea, String> tcFullname;

    @FXML
    private TableColumn<MyAdminArea, Number> tcLon;

    @FXML
    private TableColumn<MyAdminArea, Number> tcLat;

    @FXML
    private TableColumn<MyAdminArea, String> tcWkt;

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
    public AdminAreaMapper mapper;
    public static AdminAreaManage instance = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        mapper = MyBatisUtil.getMapper(AdminAreaMapper.class);
        tvAdminArea.setEditable(true);
        tvAdminArea.setRowFactory(param -> new TableRowControl());
        tfCode.setText("");
//        cbPcity.getItems().addAll("所有","济南市 3701","青岛市 3702","淄博市 3703","枣庄市3104",
//                "东营市 3705","烟台市 3706","潍坊市 3707","济宁市 3708","泰安市 3709","威海市 3710","日照市 3711","莱芜市 3712");
        tvAdminArea.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//设置表格中数据可以多选
        tcCode.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCode.setCellValueFactory(cellData -> cellData.getValue().getCode());
        tcPcode.setCellFactory(TextFieldTableCell.forTableColumn());
        tcPcode.setCellValueFactory(cellData -> cellData.getValue().getPCode());
        tcName.setCellFactory(TextFieldTableCell.forTableColumn());
        tcName.setCellValueFactory(cellData -> cellData.getValue().getName());
        tcAbbre.setCellFactory(TextFieldTableCell.forTableColumn());
        tcAbbre.setCellValueFactory(cellData -> cellData.getValue().getAbbreviation());
        tcFullname.setCellFactory(TextFieldTableCell.forTableColumn());
        tcFullname.setCellValueFactory(cellData -> cellData.getValue().getFullName());
        tcWkt.setCellFactory(TextFieldTableCell.forTableColumn());
        tcWkt.setCellValueFactory(cellData -> cellData.getValue().getWkt());
        tcLat.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        tcLat.setCellValueFactory(cellData -> cellData.getValue().getLat());
        tcLon.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        }));
        tcLon.setCellValueFactory(cellData -> cellData.getValue().getLon());
        ObservableList<MyAdminArea> adminAreas = tvAdminArea.getItems();
        //一进入页面所有数据在表格中
        List<AdminArea> areaList = mapper.selectAll();
        for (int i = 0; i < areaList.size(); i++) {
            MyAdminArea area = new MyAdminArea();
            area.getCode().set(areaList.get(i).getCode());
            area.getPCode().set(areaList.get(i).getPCode());
            area.getName().set(areaList.get(i).getName());
            area.getAbbreviation().set(areaList.get(i).getAbbreviation());
            area.getFullName().set(areaList.get(i).getFullName());
            area.getWkt().set(areaList.get(i).getWkt());
            area.getLon().set(areaList.get(i).getLon().floatValue());
            area.getLat().set(areaList.get(i).getLat().floatValue());
            adminAreas.add(area);
        }

        btnSelect.setOnMouseClicked(event -> {
            adminAreas.clear();
            String code = tfCode.getText();
            if (code.equals("")) {
                new Alert(Alert.AlertType.
                        INFORMATION, "请输入政区代码", ButtonType.OK).showAndWait();
            } else {
                AdminArea adminarea = mapper.selectByPrimaryKey(code);
                MyAdminArea area = new MyAdminArea();
                area.getCode().set(adminarea.getCode());
                area.getPCode().set(adminarea.getPCode());
                area.getName().set(adminarea.getName());
                area.getAbbreviation().set(adminarea.getAbbreviation());
                area.getFullName().set(adminarea.getFullName());
                area.getWkt().set(adminarea.getWkt());
                area.getLon().set(adminarea.getLon().floatValue());
                area.getLat().set(adminarea.getLat().floatValue());
                adminAreas.add(area);
            }
        });

        btnRefresh.setOnMouseClicked(event -> {
            adminAreas.clear();
            List<AdminArea> areas = mapper.selectAll();
            for (int i = 0; i < areas.size(); i++) {
                MyAdminArea area = new MyAdminArea();
                area.getCode().set(areas.get(i).getCode());
                area.getPCode().set(areas.get(i).getPCode());
                area.getName().set(areas.get(i).getName());
                area.getAbbreviation().set(areas.get(i).getAbbreviation());
                area.getFullName().set(areas.get(i).getFullName());
                area.getWkt().set(areas.get(i).getWkt());
                area.getLon().set(areas.get(i).getLon().floatValue());
                area.getLat().set(areas.get(i).getLat().floatValue());
                adminAreas.add(area);
            }
        });
        btnAdd.setOnMouseClicked(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("AdminAreaAdd.fxml"));
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
            for (int i = 0; i < tvAdminArea.getItems().size(); i++) {
                AdminArea area = new AdminArea();
                area.setLon(new BigDecimal(tvAdminArea.getItems().get(i).getLon().getValue()));
                area.setLat(new BigDecimal(tvAdminArea.getItems().get(i).getLat().getValue()));
                area.setWkt(tvAdminArea.getItems().get(i).getWkt().getValue());
                area.setName(tvAdminArea.getItems().get(i).getName().getValue());
                area.setFullName(tvAdminArea.getItems().get(i).getFullName().getValue());
                area.setCode(tvAdminArea.getItems().get(i).getCode().getValue());
                area.setPCode(tvAdminArea.getItems().get(i).getPCode().getValue());
                area.setAbbreviation(tvAdminArea.getItems().get(i).getAbbreviation().getValue());
                int result = mapper.updateByPrimaryKeyWithBLOBs(area);
                if (result != 1)
                    erro++;
            }
            new Alert(Alert.AlertType.
                    INFORMATION, "更新失败数目" + erro + "条", ButtonType.OK).showAndWait();
        });

    }

    class TableRowControl extends TableRow<MyAdminArea> {
        TableRowControl() {
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.SECONDARY)
                            && event.getClickCount() == 1
                            && TableRowControl.this.getIndex() < tvAdminArea.getItems().size()) {
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
                                    for (int i = 0; i < tvAdminArea.getSelectionModel().getSelectedItems().size(); i++) {
                                        int curresult = mapper.deleteByPrimaryKey(tvAdminArea.getSelectionModel().getSelectedItems().get(i).getCode().getValue());
                                        result = result + curresult;
                                        tvAdminArea.getItems().remove(tvAdminArea.getSelectionModel().getSelectedItems().get(i));//在表中删除只要这一句话即可
                                    }
                                    new Alert(Alert.AlertType.
                                            INFORMATION, "成功删除" + result + "条数据", ButtonType.OK).showAndWait();

                                }
                            }
                        });
                        tvAdminArea.setContextMenu(contextMenu);
                    }
                }
            });
        }
    }

}
