package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.util.FGDBReader;
import edu.zju.gis.sdch.util.GdalHelper;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.ogr.Layer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainPage implements Initializable {
    private static final Logger log = LogManager.getLogger(MainPage.class);
    public static final String TITLE = "数据入库";
    public static MainPage instance = null;
    @FXML
    public BorderPane rootLayout;
    @FXML
    private Button btnPreview;
    @FXML
    private TextField tfChooseFile;
    @FXML
    public ComboBox<String> cbCategory;
    @FXML
    private TableView<FieldInformation> tableView;
    @FXML
    private TableColumn<FieldInformation, String> tcFieldName;
    @FXML
    private TableColumn<FieldInformation, String> tcTargetName;
    @FXML
    private TableColumn<FieldInformation, Number> tcFieldType;//写成Number,入库时强制转换成Integer
    @FXML
    private TableColumn<FieldInformation, Boolean> tcUsed;
    @FXML
    private TableColumn<FieldInformation, Boolean> tcAnalyzable;
    @FXML
    private TableColumn<FieldInformation, Number> tcBoost;
    @FXML
    private TableColumn<FieldInformation, String> tcDescription;
    @FXML
    private ComboBox<String> cbxLayers;
    //    @FXML
//    private ComboBox<String> cbUuidField;
    @FXML
    private RadioButton rbSkipEmpty;

    private FGDBReader reader;

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
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException ex) {
                    log.error("", ex);
                }
            reader = new FGDBReader(path);
            String[] layerNames = reader.getLayerNames();//返回所有图层名称，将这些初始化到图层选择列表中
            cbxLayers.getItems().clear();
            cbxLayers.getItems().addAll(layerNames);
        });
        cbxLayers.valueProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<FieldInformation> informations = tableView.getItems();
            informations.clear();
            if (newValue == null)
                return;
            Layer SelectedLayer = reader.getLayer(newValue);
            Map<String, Integer> fields = GdalHelper.getFieldTypes(SelectedLayer);//得到一个图层中所有字段名称和类型
            //将所选择的图层中的字段信息呈现在表中
            fields.forEach((key, value) -> {
                FieldInformation fieldRow = new FieldInformation();
                fieldRow.getName().set(key);
                String targetName = key.toLowerCase();
                fieldRow.getTargetName().set(targetName);
                fieldRow.getType().set(value);
                fieldRow.getUsed().set(FieldInformation.fixedFields.contains(targetName));
                fieldRow.getAnalyzable().set(targetName.startsWith("name") || targetName.equals("address"));
                float boost = 1f;
                if (targetName.startsWith("name"))
                    boost = 4;
                else if (targetName.equals("address"))
                    boost = 2;
                fieldRow.getBoost().set(boost);
                fieldRow.getDesc().set("");
                informations.add(fieldRow);
            });
            informations.sort(Comparator.comparing(f -> f.getName().get()));
//            cbUuidField.getItems().clear();
//            cbUuidField.getItems().addAll(fields.keySet().stream().sorted().toArray(String[]::new));
//            cbUuidField.getItems().add(0, "--自动生成--");
//            cbUuidField.setValue("--自动生成--");
            btnPreview.setDisable(false);
        });
        cbCategory.getItems().addAll("框架数据", "专题数据");
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
                if ("".equals(cbCategory.getValue())) {
                    new Alert(Alert.AlertType.CONFIRMATION, "请选择分类体系", ButtonType.OK)
                            .showAndWait();
                    return;
                }
                String content = "";
                if (instance.getSelectedLayer().equals("poi") && instance.getCategory().equals("框架数据")) {
                    content = "确认字段映射名中含有lsid（唯一标识）、district（行政代码）、name（名称）、priority, address，kind,rev_geo和tc";
                } else if (instance.getCategory().equals("专题数据")) {
                    content = "确认字段映射名中含有lsid（唯一标识）、district（行政代码）、name（名称）、clasid";
                } else {
                    content = "确认字段映射名中含有lsid（唯一标识）、district（行政代码）、name（名称）";
                }
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, content);
                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("DataPreview.fxml"));
                        Scene scene = new Scene(root);
                        Stage primaryStage = (Stage) rootLayout.getScene().getWindow();
                        Stage stage = new Stage();
                        stage.initOwner(primaryStage);
                        stage.setTitle(DataPreview.TITLE);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        log.error("数据预览窗体加载失败", e);
                        new Alert(Alert.AlertType.ERROR, "数据预览窗体加载失败：" + e.getMessage(), ButtonType.OK)
                                .showAndWait();
                    }
                }
            }
        });
    }

    public FGDBReader getReader() {
        return reader;
    }

    public String getCategory() {
        return cbCategory.getValue().trim();
    }

    public String getSelectedLayer() {
        return cbxLayers.getValue().trim();
    }

//    public String getUuidField() {
//        return cbUuidField.getValue().trim();
//    }

    public boolean skipEmpty() {
        return rbSkipEmpty.isSelected();
    }

    public List<FieldInformation> getFieldInfos() {
        return tableView.getItems();
    }
}