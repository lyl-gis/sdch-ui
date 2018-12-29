package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.util.FGDBReader;
import edu.zju.gis.sdch.util.GdalHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.ogr.Layer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class MainPage implements Initializable {
    private static final Logger log = LogManager.getLogger(MainPage.class);
    @FXML
    private Button btnPreview;
    @FXML
    private TextField tfChooseFile;
    @FXML
    public ChoiceBox<String> cbCategory;
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
    public ChoiceBox<String> cbxLayers;
    @FXML
    public ChoiceBox<String> cbUuidField;
    @FXML
    public RadioButton rbSkipEmpty;

    public static FGDBReader reader;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfChooseFile.setOnMouseClicked(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(AllPages.mainStage);
            String path = file.getPath();//选择的文件夹路径
            tfChooseFile.setText(path);
            reader = new FGDBReader(path);
            String[] layer = reader.getLayerNames();//返回所有图层名称，将这些初始化到图层选择列表中
            cbxLayers.setItems(FXCollections.observableArrayList(layer));
            cbxLayers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    Layer SelectedLayer = reader.getLayer(newValue);
                    Map<String, Integer> fields = GdalHelper.getFieldTypes(SelectedLayer);//得到一个图层中所有字段名称和类型
                    //将所选择的图层中的字段信息呈现在表中
                    ArrayList<FieldInformation> mylayer = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : fields.entrySet()) {
                        FieldInformation fieldRow = new FieldInformation();
                        fieldRow.getName().set(entry.getKey());
                        ((SimpleIntegerProperty) fieldRow.getType()).set(entry.getValue());
                        mylayer.add(fieldRow);
                    }
                    tableView.setItems(FXCollections.observableArrayList(mylayer));
                    fields.put("自动生成", 1);
                    cbUuidField.setItems(FXCollections.observableArrayList(fields.keySet()));
                    btnPreview.setDisable(false);
                }
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
        tcTargetName.setCellValueFactory(cellData -> {
            SimpleStringProperty cell = cellData.getValue().getTargetName();
            if (cell.get() == null)
                cell.set(cellData.getValue().getName().get());
            return cell;
        });
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
                StageManager.CONTROLLER.put("dataPreviewController", MainPage.this);
                try {
                    if (cbCategory.getValue() == "") {
                        new Alert(Alert.AlertType.CONFIRMATION, "请选择分类体系", ButtonType.OK)
                                .showAndWait();
                    }
                    Parent root = FXMLLoader.load(getClass().getResource("DataPreview.fxml"));
                    Scene scene = new Scene(root);
                    AllPages.dataPreviewStage.setTitle("数据预览");
                    AllPages.dataPreviewStage.setScene(scene);
                    AllPages.dataPreviewStage.show();
                    //将datapreview窗口和控制器保存到map中
                    StageManager.STAGE.put("dataPreviewStage", AllPages.dataPreviewStage);
                } catch (IOException e) {
                    log.error("数据预览窗体加载失败", e);
                    new Alert(Alert.AlertType.ERROR, "数据预览窗体加载失败：" + e.getMessage(), ButtonType.OK)
                            .showAndWait();
                }

            }
        });

    }


}