package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.Main;
import edu.zju.gis.sdch.tool.Importer;
import edu.zju.gis.sdch.util.GdalHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.gdal.ogr.Layer;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

import java.net.URL;
import java.util.*;

public class DataPreview implements Initializable {
    @FXML
    private Button btnImport;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextField tfIndex;
    @FXML
    private ComboBox<String> coDataType;
    @FXML
    private TableView<Map<String, Object>> tvPreview;
    @FXML
    private ChoiceBox<Integer> cbPreviewSize;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coDataType.getItems().add("poi");
        coDataType.getItems().add("entity");
        coDataType.setEditable(true);
        coDataType.valueProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<String> dataTypes = coDataType.getItems();
            if (!dataTypes.contains(newValue))
                dataTypes.add(newValue);
        });
        cbPreviewSize.getItems().addAll(10, 25, 50, 100);
        cbPreviewSize.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            MainPage mainPage = MainPage.instance;
            Layer layer = MainPage.reader.getLayer(mainPage.cbxLayers.getValue());
            //1. 读取字段配置
            Map<String, Integer> fields = new HashMap<>();
            Map<String, String> fieldMapping = new HashMap<>();//选取字段名与别名的映射
            for (FieldInformation fieldInformation : mainPage.tableView.getItems()) {
                if (fieldInformation.getUsed().get()) {
                    fields.put(fieldInformation.getName().get(), fieldInformation.getType().intValue());
                    fieldMapping.put(fieldInformation.getName().get(), fieldInformation.getTargetName().get());
                }
            }
            //2. 构造预览表格的结构
            List<TableColumn<Map<String, Object>, String>> tableColumns = new ArrayList<>();
            fieldMapping.values().forEach(targetName -> {
                TableColumn<Map<String, Object>, String> column = new TableColumn<>(targetName);
                tableColumns.add(column);
                column.setCellValueFactory(param -> {
                    String value = "";
                    for (String srcName : fieldMapping.keySet())
                        try {
                            if (targetName.equals(fieldMapping.get(srcName)))
                                value = param.getValue().get(srcName).toString();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    return new SimpleStringProperty(value);
                });
            });
            tvPreview.getColumns().clear();
            tvPreview.getColumns().addAll(tableColumns);
            //3. 读取数据并填充表格
            SpatialReference sr = layer.GetSpatialRef();
            CoordinateTransformation transformation = null;
            if (sr.IsProjected() == 1)
                transformation = osr.CreateCoordinateTransformation(sr, sr.CloneGeogCS());
            Map<String, Map<String, Object>> records = GdalHelper.getNextNFeatures(layer, newValue, fields, mainPage.cbUuidField.getValue(), mainPage.rbSkipEmpty.isSelected(), transformation);
            tvPreview.getItems().clear();
            tvPreview.getItems().addAll(records.values());
        });
        cbPreviewSize.setValue(10);
        btnImport.setOnMouseClicked(event -> {
            Service<Double> service = new Service<Double>() {
                @Override
                protected Task<Double> createTask() {
                    String index = tfIndex.getText();
                    MainPage mainPage = MainPage.instance;
                    String category = mainPage.cbCategory.getValue().trim();
                    String layerName = mainPage.cbxLayers.getValue();
                    String dtype = coDataType.getValue().trim();
                    if ("框架数据".equals(category))
                        category = "framework";
                    else
                        category = "topic";
                    Map<String, String> fieldMapping = new HashMap<>();//选取字段名与别名的映射
                    Map<String, Integer> fields = new HashMap<>();//选取字段名与类型的映射
                    Map<String, Float> analyzable = new HashMap<>();
                    mainPage.tableView.getItems().forEach(fieldInformation -> {
                        if (fieldInformation.getUsed().get()) {
                            fields.put(fieldInformation.getName().get(), fieldInformation.getType().intValue());
                            fieldMapping.put(fieldInformation.getName().get(), fieldInformation.getTargetName().get());
                            if (fieldInformation.getAnalyzable().get()) {
                                analyzable.put(fieldInformation.getTargetName().get(), fieldInformation.getBoost().floatValue());
                            }
                        }
                    });
                    String uuidField = mainPage.cbUuidField.getValue();
                    boolean skipEmptyGeom = mainPage.rbSkipEmpty.isSelected();//检查单选框是否被选中
                    Layer layer = MainPage.reader.getLayer(layerName);
                    return new Importer(Main.getHelper(), Main.getSetting(), index, dtype, layer, fields, uuidField,
                            fieldMapping, analyzable, skipEmptyGeom, category, dtype);
                }
            };
            progressBar.progressProperty().bind(service.progressProperty());
            service.start();
            service.stateProperty().addListener((observable, oldValue, newValue) -> {
                switch (newValue) {
                    case READY:
                        break;
                    case SUCCEEDED:
                        long error = ((Service<Double>) ((SimpleObjectProperty) observable).getBean()).getValue().longValue();
                        if (error > 0)
                            new Alert(Alert.AlertType.INFORMATION, error + "条数据入库失败", ButtonType.OK)
                                    .showAndWait();
                        else {

                            DataPreview.this.btnImport.getScene().getWindow().hide();
                            new Alert(Alert.AlertType.INFORMATION, "数据全部成功入库", ButtonType.OK)
                                    .showAndWait();
                        }
                        //todo alert
                        break;
                    case FAILED:
                        break;
                }
            });
        });
    }

}
