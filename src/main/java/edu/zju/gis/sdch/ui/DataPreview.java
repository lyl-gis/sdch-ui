package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.Main;
import edu.zju.gis.sdch.mapper.IndexMapper;
import edu.zju.gis.sdch.mapper.IndexTypeMapper;
import edu.zju.gis.sdch.model.Index;
import edu.zju.gis.sdch.model.IndexType;
import edu.zju.gis.sdch.tool.Importer;
import edu.zju.gis.sdch.util.GdalHelper;
import edu.zju.gis.sdch.util.MyBatisUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gdal.ogr.Layer;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

import java.net.URL;
import java.util.*;

public class DataPreview implements Initializable {
    private static final Logger log = LogManager.getLogger(DataPreview.class);
    public static final String TITLE = "数据预览";
    @FXML
    private BorderPane rootLayout;
    @FXML
    private Button btnImport;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private ComboBox<String> coDataType;
    @FXML
    private TableView<Map<String, Object>> tvPreview;
    @FXML
    private ChoiceBox<Integer> cbPreviewSize;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelNumber;

    @FXML
    private Label lableAllNumber;
    @FXML
    private HBox hbox;
    private IndexMapper mapper;
    public static MainPage mainPage;
    public static String uuidField;
    private IndexTypeMapper indexTypeMapper;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPage = MainPage.instance;
        tvPreview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mapper = MyBatisUtil.getMapper(IndexMapper.class);
        indexTypeMapper = MyBatisUtil.getMapper(IndexTypeMapper.class);
        ObservableList<String> codatatype = coDataType.getItems();
        codatatype.addAll("fe_road", "f_poi", "fe_zq");
        List<Index> allIndex = mapper.selectAll();
        List<String> allIndexNames = new ArrayList<>();
        for (int i = 0; i < allIndex.size(); i++) {
            allIndexNames.add(allIndex.get(i).getIndice());
        }
        if (allIndexNames.contains("sdmap") || allIndexNames.contains("themes")) {
            String indexName = "";
            if (mainPage.cbCategory.getValue().equals("框架数据")) {
                indexName = "sdmap";
            }
            if (mainPage.cbCategory.getValue().equals("专题数据")) {
                indexName = "themes";
            }
            List<IndexType> selectTypes = indexTypeMapper.selectByIndice(indexName);
            List<String> types = new ArrayList<>();
            for (int i = 0; i < selectTypes.size(); i++)
                types.add(selectTypes.get(i).getDtype());
            for (int i = 0; i < types.size(); i++) {
                if (!codatatype.contains(types.get(i)))
                    codatatype.add(types.get(i));
            }
            Collections.sort(codatatype);
        }
        coDataType.setEditable(true);
        coDataType.valueProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<String> dataTypes = coDataType.getItems();
            if (!dataTypes.contains(newValue))
                dataTypes.add(newValue);
        });

        Layer layer = mainPage.getReader().getLayer(mainPage.getSelectedLayer());
        //1. 读取字段配置
        Map<String, Integer> fields = new HashMap<>();
        Map<String, String> fieldMapping = new HashMap<>();//选取字段名与别名的映射
        Map<String, Float> analyzable = new HashMap<>();
        mainPage.getFieldInfos().forEach(fieldInformation -> {
            if (fieldInformation.getUsed().get()) {
                fields.put(fieldInformation.getName().get(), fieldInformation.getType().intValue());
                fieldMapping.put(fieldInformation.getName().get(), fieldInformation.getTargetName().get());
                if (fieldInformation.getAnalyzable().get()) {
                    analyzable.put(fieldInformation.getTargetName().get(), fieldInformation.getBoost().floatValue());
                }
            }
        });
        //2. 构造预览表格的结构
        List<TableColumn<Map<String, Object>, String>> tableColumns = new ArrayList<>();
        fieldMapping.values().forEach(targetName -> {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(targetName);
            tableColumns.add(column);
            column.setCellValueFactory(param -> {
                String value = "";
                for (String srcName : fieldMapping.keySet())
                    if (targetName.equals(fieldMapping.get(srcName)))
                        value = param.getValue().get(srcName).toString();
                return new SimpleStringProperty(value);
            });
        });
        tvPreview.getColumns().clear();
        tvPreview.getColumns().addAll(tableColumns);
        //添加行号
        TableColumn<Map<String, Object>, String> idColumn = new TableColumn<>();
        idColumn.setCellFactory(column -> new TableCell<Map<String, Object>, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty) {
                    this.setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        tvPreview.getColumns().add(0, idColumn);


        cbPreviewSize.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //3. 读取数据并填充表格
            SpatialReference sr = layer.GetSpatialRef();
            CoordinateTransformation transformation = null;
            if (sr.IsProjected() == 1)
                transformation = osr.CreateCoordinateTransformation(sr, sr.CloneGeogCS());
            layer.ResetReading();
            Map<String, Map<String, Object>> records = GdalHelper.getNextNFeatures(layer, newValue, fields, "--自动生成--", mainPage.skipEmpty(), transformation);
            tvPreview.getItems().clear();
            tvPreview.getItems().addAll(records.values());
        });
        cbPreviewSize.getItems().addAll(10, 25, 50, 100);
        cbPreviewSize.setValue(10);

        btnImport.setOnMouseClicked(event -> {

            List<String> checkMapping = new ArrayList<>();
            for (String str : fieldMapping.keySet()) {
                checkMapping.add(fieldMapping.get(str));
            }
            if (checkImporter(checkMapping)) {
                //强行让uuidField为lsid对应的字段
                for (String str : fieldMapping.keySet()) {
                    if (fieldMapping.get(str).equals("lsid"))
                        uuidField = str;
                }
                hbox.setVisible(true);
                Long allNumber = layer.GetFeatureCount();//得到一共需要入库的条数;
                Service<Double> service = new Service<Double>() {
                    @Override
                    protected Task<Double> createTask() {
                        String esIndex = "";
                        if (mainPage.cbCategory.getValue().equals("框架数据")) {
                            esIndex = "sdmap";
                        }
                        if (mainPage.cbCategory.getValue().equals("专题数据")) {
                            esIndex = "themes";
                        }
                        MainPage mainPage = MainPage.instance;
                        String category = mainPage.getCategory();
                        String layerName = mainPage.getSelectedLayer();
                        String dtype = coDataType.getValue().trim();
                        if ("框架数据".equals(category))
                            category = "framework";
                        else
                            category = "topic";

//                        String uuidField = mainPage.getUuidField();

                        boolean skipEmptyGeom = mainPage.skipEmpty();//检查单选框是否被选中
                        Layer layer = mainPage.getReader().getLayer(layerName);
                        return new Importer(Main.getHelper(), Main.getSetting(), esIndex, dtype, layer, fields, uuidField, fieldMapping, analyzable, skipEmptyGeom, category, dtype);
                    }
                };
                lableAllNumber.setText(allNumber.toString() + "条数据");
                progressBar.progressProperty().bind(service.progressProperty());
                service.start();
                service.progressProperty().addListener((observable, oldValue, newValue) -> {
//             long endTime = System.currentTimeMillis();    //获取结束时间
//             labelTime.setText((endTime - startTime) / 1000.0 + "s");
                    labelNumber.setText((int) (newValue.doubleValue() * allNumber) + "条数据");
                });
                long startTime = System.currentTimeMillis();    //获取开始时间
                Thread thread = new Thread() {
                    public void run() {
                        while (progressBar.progressProperty().get() < 1) {
                            long endTime = System.currentTimeMillis();    //获取结束时间
                            System.out.println(endTime);
                            Platform.runLater(() -> {
                                labelTime.setText((endTime - startTime) / 1000.0 + "s");//UI界面的改变一定要写到platform.runlater中
                            });
                            try {
                                //睡眠1000毫秒,即每秒更新一下时间
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
                service.stateProperty().addListener((observable, oldValue, newValue) -> {
                    switch (newValue) {
                        case READY:
                            break;
                        case SUCCEEDED:
                            long error = service.getValue().longValue();
                            if (error > 0)
                                new Alert(Alert.AlertType.INFORMATION, error + "条数据入库失败", ButtonType.OK)
                                        .showAndWait();
                            else {
                                new Alert(Alert.AlertType.INFORMATION, "数据全部成功入库", ButtonType.OK)
                                        .showAndWait();
                                DataPreview.this.btnImport.getScene().getWindow().hide();
                            }
                            break;
                        case FAILED:
                            break;
                    }
                });
            }
        });
    }

    Boolean checkImporter(List<String> checkMapping) {
        //入库前字段检验
        Boolean result = true;
        if (mainPage.getSelectedLayer().equals("poi")) {
            if (!(checkMapping.contains("lsid") && checkMapping.contains("district") && checkMapping.contains("name") && checkMapping.contains("priority") && checkMapping.contains("address") && checkMapping.contains("kind") && checkMapping.contains("rev_geo") && checkMapping.contains("tc"))) {
                new Alert(Alert.AlertType.INFORMATION, "映射字段信息有误，请重新检查，确认包含lsid（唯一标识）、district（行政代码）、name（名称）、priority, address，kind, rev_geo,tc", ButtonType.OK).showAndWait();
                Stage stage = (Stage) btnImport.getScene().getWindow();
                stage.close();
                result = false;
            }
        } else if (mainPage.getCategory().equals("专题数据")) {
            if (!(checkMapping.contains("lsid") && checkMapping.contains("district") && checkMapping.contains("name") && checkMapping.contains("clasid"))) {
                new Alert(Alert.AlertType.INFORMATION, "映射字段信息有误，请重新检查，确认包含lsid（唯一标识）、district（行政代码）、name（名称）和clsid", ButtonType.OK).showAndWait();
                Stage stage = (Stage) btnImport.getScene().getWindow();
                stage.close();
                result = false;
            }
        } else {
            if (!(checkMapping.contains("lsid") && checkMapping.contains("district") && checkMapping.contains("name"))) {
                new Alert(Alert.AlertType.INFORMATION, "映射字段信息有误，请重新检查,确认包含lsid（唯一标识）、district（行政代码）、name（名称）", ButtonType.OK).showAndWait();
                Stage stage = (Stage) btnImport.getScene().getWindow();
                stage.close();
                result = false;
            }
        }
        return result;
    }
}
