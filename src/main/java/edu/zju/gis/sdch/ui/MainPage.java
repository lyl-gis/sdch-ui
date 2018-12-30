package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.util.FGDBReader;
import edu.zju.gis.sdch.util.GdalHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.ogr.Layer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MainPage implements Initializable {
    private static final Logger log = LogManager.getLogger(MainPage.class);
    public static MainPage instance = null;
    @FXML
    private Button btnPreview;
    @FXML
    private TextField tfChooseFile;
    @FXML
    public ComboBox<String> cbCategory;
    @FXML
    public TableView<FieldInformation> tableView;
    @FXML
    public TableColumn<FieldInformation, String> tcFieldName;
    @FXML
    public TableColumn<FieldInformation, String> tcTargetName;
    @FXML
    public TableColumn<FieldInformation, Number> tcFieldType;//写成Number,入库时强制转换成Integer
    @FXML
    public TableColumn<FieldInformation, Boolean> tcUsed;
    @FXML
    public TableColumn<FieldInformation, Boolean> tcAnalyzable;
    @FXML
    public TableColumn<FieldInformation, Number> tcBoost;
    @FXML
    public TableColumn<FieldInformation, String> tcDescription;
    @FXML
    public ComboBox<String> cbxLayers;
    @FXML
    public ComboBox<String> cbUuidField;
    @FXML
    public RadioButton rbSkipEmpty;

    public static FGDBReader reader;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        tfChooseFile.setOnMouseClicked(event -> {
            File file = directoryChooser.showDialog(tfChooseFile.getParent().getScene().getWindow());
            if (file == null)
                return;
            String path = file.getPath();//选择的文件夹路径
            tfChooseFile.setText(path);
            reader = new FGDBReader(path);
            String[] layerNames = reader.getLayerNames();//返回所有图层名称，将这些初始化到图层选择列表中
            cbxLayers.getItems().clear();
            cbxLayers.getItems().addAll(layerNames);
            cbxLayers.valueProperty().addListener((observable, oldValue, newValue) -> {
                Layer SelectedLayer = reader.getLayer(newValue);
                Map<String, Integer> fields = GdalHelper.getFieldTypes(SelectedLayer);//得到一个图层中所有字段名称和类型
                ObservableList<FieldInformation> informations = tableView.getItems();
                informations.clear();
                //将所选择的图层中的字段信息呈现在表中
                fields.forEach((key, value) -> {
                    FieldInformation fieldRow = new FieldInformation();
                    fieldRow.getName().set(key);
                    fieldRow.getTargetName().set(key);
                    fieldRow.getType().set(value);
                    fieldRow.getUsed().set(true);
                    fieldRow.getAnalyzable().set(false);
                    fieldRow.getBoost().set(1f);
                    fieldRow.getDesc().set("");
                    informations.add(fieldRow);
                });
                cbUuidField.getItems().clear();
                cbUuidField.getItems().addAll(fields.keySet().stream().sorted().toArray(String[]::new));
                cbUuidField.getItems().add(0, "--自动生成--");
                cbUuidField.setValue("--自动生成--");
                btnPreview.setDisable(false);
            });
        });
        cbCategory.setItems(FXCollections.observableArrayList("专题数据", "框架数据"));
        tcFieldName.setCellValueFactory(cellData -> cellData.getValue().getName());
        tcFieldType.setCellValueFactory(cellData -> cellData.getValue().getType());
        tcFieldType.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return GdalHelper.getTypeName(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                for (int type : GdalHelper.getFieldTypes().keySet())
                    if (string.equals(GdalHelper.getTypeName(type)))
                        return type;
                return -1;
            }
        }));
        tcFieldType.setEditable(false);
        tcTargetName.setCellFactory(TextFieldTableCell.forTableColumn());
        tcTargetName.setCellValueFactory(cellData -> cellData.getValue().getTargetName());
        //用已有的CheckBoxTableCell填充单元格jfxrt.javafx.scene.control.cell
        tcUsed.setCellFactory(CheckBoxTableCell.forTableColumn(tcUsed));
        tcUsed.setCellValueFactory(o -> o.getValue().getUsed());
        tcAnalyzable.setCellFactory(CheckBoxTableCell.forTableColumn(tcAnalyzable));
        tcAnalyzable.setCellValueFactory(o -> o.getValue().getAnalyzable());

        tcBoost.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.toString();
            }

            @Override
            public Number fromString(String string) {
                return Float.parseFloat(string);
            }
        }));
        tcBoost.setCellValueFactory(cellData -> cellData.getValue().getBoost());
        tcDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDescription.setCellValueFactory(cellData -> cellData.getValue().getDesc());
        btnPreview.setDisable(true);
        btnPreview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    if ("".equals(cbCategory.getValue())) {
                        new Alert(Alert.AlertType.CONFIRMATION, "请选择分类体系", ButtonType.OK)
                                .showAndWait();
                    }
                    Parent root = FXMLLoader.load(getClass().getResource("DataPreview.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("数据预览");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    log.error("数据预览窗体加载失败", e);
                    new Alert(Alert.AlertType.ERROR, "数据预览窗体加载失败：" + e.getMessage(), ButtonType.OK)
                            .showAndWait();
                }

            }
        });

    }


}