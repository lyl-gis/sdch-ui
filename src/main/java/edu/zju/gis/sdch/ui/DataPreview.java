package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.Main;
import edu.zju.gis.sdch.tool.Importer;
import edu.zju.gis.sdch.util.GdalHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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

    //    private HistoryField<String> historyField;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //  this.coDataType=new HistoryField<>();
        ArrayList<String> datatpye = new ArrayList<>();
        datatpye.add("poi");
        datatpye.add("entity");
        coDataType.setItems(FXCollections.observableArrayList("poi", "entity"));
        coDataType.setEditable(true);
        coDataType.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int number = 0;//如果列表中已经有这个值，则不加到列表中
                for (int i = 0; i < datatpye.size(); i++) {
                    if (newValue == datatpye.get(i))
                        number++;
                }
                if (number == 0) {
                    datatpye.add(newValue);
                    coDataType.setItems(FXCollections.observableArrayList(datatpye));
                }
            }
        });
        cbPreviewSize.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
        cbPreviewSize.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            MainPage mainPage = (MainPage) StageManager.CONTROLLER.get("dataPreviewController");
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
                    String colName = column.getText();
                    for (String srcName : fieldMapping.keySet())
                        if (colName.equals(fieldMapping.get(srcName)))
                            value = param.getValue().get(srcName).toString();
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
                    MainPage mainPage = (MainPage) StageManager.CONTROLLER.get("dataPreviewController");
                    String dtype = mainPage.cbCategory.getValue();
                    String layerName = mainPage.cbxLayers.getValue();
                    String uuidField = mainPage.cbUuidField.getValue();
                    String indexType = mainPage.cbCategory.getValue();

                    if (indexType == "框架数据")
                        indexType = "framework";
                    else
                        indexType = "topic";

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
                    boolean skipEmptyGeom = mainPage.rbSkipEmpty.isSelected();//检查单选框是否被选中
                    Layer layer = MainPage.reader.getLayer(layerName);
//                    return new Importer(Main.getHelper(), Main.getSetting(), index, dtype, layer, fields, uuidField,
//                            fieldMapping, analyzable, skipEmptyGeom, Contants.IndexType.FRAMEWORK, "");
                    return new Importer(Main.getHelper(), Main.getSetting(), index, dtype, layer, fields, uuidField,
                            fieldMapping, analyzable, skipEmptyGeom, indexType, coDataType.getValue());
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
                            AllPages.dataPreviewStage.close();
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
