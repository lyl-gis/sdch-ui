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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private RadioButton rbDeleteExisted;

    @FXML
    private Label lableAllNumber;
    @FXML
    private HBox hbox;
    private IndexMapper mapper;
    public static MainPage mainPage;
    public static String uuidField;
    private IndexTypeMapper indexTypeMapper;
    public static int importTag = 0;
    public MainPort mainPort;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPort = MainPort.instaceMainPort;
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
        coDataType.setValue("");
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
        progressBar.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (oldValue.intValue() != 0) {
                    rootLayout.setPrefWidth(newValue.doubleValue());
                    hbox.setPrefWidth(newValue.doubleValue());
//                    vbox.setPrefWidth(newValue.doubleValue());
                    progressBar.setPrefWidth(newValue.doubleValue());
                }
            }
        });
        btnImport.setOnMouseClicked(event -> {
            //开始入库后不能再选择预览数量
            cbPreviewSize.setVisible(false);
            //入库过程中不能关闭窗口及其父窗口
            Stage stageDataPreview = (Stage) btnImport.getScene().getWindow();
            stageDataPreview.setOnCloseRequest(e -> {
                e.consume();
                new Alert(Alert.AlertType.INFORMATION, "正在进行入库，无法关闭窗口", ButtonType.OK).showAndWait();
            });
            Stage stageMainPort = (Stage) mainPort.rootLayout.getScene().getWindow();
            stageMainPort.setOnCloseRequest(e -> {
                if (importTag != 2) {
                    e.consume();
                    new Alert(Alert.AlertType.INFORMATION, "正在进行入库，无法关闭窗口", ButtonType.OK).showAndWait();
                }
            });
            Stage stageMainPage = (Stage) mainPage.rootLayout.getScene().getWindow();
            stageMainPage.setOnCloseRequest(e -> {
                if (importTag != 2) {
                    e.consume();
                    new Alert(Alert.AlertType.INFORMATION, "正在进行入库，无法关闭窗口", ButtonType.OK).showAndWait();
                }
            });
            if (coDataType.getValue().equals("")) {
                new Alert(Alert.AlertType.INFORMATION, "请选择dtype的值", ButtonType.OK).showAndWait();
            } else {
                List<String> checkMapping = new ArrayList<>();
                for (String str : fieldMapping.keySet()) {
                    checkMapping.add(fieldMapping.get(str));
                }
                if (checkImporter2(checkMapping)) {
                    hbox.setVisible(true);
                    Long allNumber = layer.GetFeatureCount();//得到一共需要入库的条数;
                    //是否删除已经存在于ES中的该类型数据
                    if (rbDeleteExisted.isSelected()) {
                        String indexNames = "";
                        if (mainPage.cbCategory.getValue().equals("框架数据")) {
                            indexNames = "sdmap";
                            //强行让uuidField为lsid对应的字段
                            for (String str : fieldMapping.keySet()) {
                                if (fieldMapping.get(str).equals("lsid"))
                                    uuidField = str;
                            }
                        }
                        if (mainPage.cbCategory.getValue().equals("专题数据")) {
                            indexNames = "themes";
                            uuidField = "自动生成";//专题数据的唯一id还不确定
                        }
                        List<IndexType> listIndexType = new ArrayList<>();
                        listIndexType.addAll(indexTypeMapper.selectByIndice(indexNames));
                        List<String> listIndexTypeNames = new ArrayList<>();
                        for (int i = 0; i < listIndexType.size(); i++) {
                            listIndexTypeNames.add(listIndexType.get(i).getDtype());
                        }
                        if (listIndexTypeNames.contains(coDataType.getValue())) {
                            new Alert(Alert.AlertType.INFORMATION, "正在删除数据，请等待。", ButtonType.OK).showAndWait();
                            //数据库中删除
                            String id = "";
                            for (int m = 0; m < listIndexType.size(); m++) {
                                if (listIndexType.get(m).getDtype().equals(coDataType.getValue()))
                                    id = listIndexType.get(m).getId();
                                indexTypeMapper.deleteByPrimaryKey(id);
                            }
                            //在ES中删除该类别数据
                            long number = Main.getHelper().getDtypeDocCount(indexNames, coDataType.getValue());
                            long result = Main.getHelper().deleteDtype(indexNames, coDataType.getValue());
                            new Alert(Alert.AlertType.INFORMATION, "该类别数据在ES中已存在" + number + "条，成功删除" + result + "条,现在开始入库数据", ButtonType.OK).showAndWait();
                        } else {
                            new Alert(Alert.AlertType.INFORMATION, "数据库中无该类数据", ButtonType.OK).showAndWait();
                        }
                    }
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
//                                System.out.println(endTime);
                                Platform.runLater(() -> {
                                    long time = (endTime - startTime) / 1000;
//                                    labelTime.setText((endTime - startTime) / 1000.0 + "s");//UI界面的改变一定要写到platform.runlater中
                                    if (time <= 60) {
                                        labelTime.setText(time + "s");
                                    } else {
                                        labelTime.setText(time / 60 + "分" + (time - time / 60 * 60) + "s");
                                    }
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
                                importTag = 2;
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
            //规则暂未确定
//            if (!(checkMapping.contains("lsid") && checkMapping.contains("district") && checkMapping.contains("name") && checkMapping.contains("clasid"))) {
//                new Alert(Alert.AlertType.INFORMATION, "映射字段信息有误，请重新检查，确认包含lsid（唯一标识）、district（行政代码）、name（名称）和clasid", ButtonType.OK).showAndWait();
//                Stage stage = (Stage) btnImport.getScene().getWindow();
//                stage.close();
//                result = false;
//            }
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

    Boolean checkImporter2(List<String> checkMapping) {
        //再次校验只要框架数据包含lsid,框架数据以lsid为id进行入库，专题数据还不确定，自动生成
        Boolean result = true;
        if (mainPage.getCategory().equals("框架数据")) {
            if (!(checkMapping.contains("lsid"))) {
                new Alert(Alert.AlertType.INFORMATION, "映射字段信息有误，请重新检查，确认包含lsid（唯一标识）", ButtonType.OK).showAndWait();
                Stage stage = (Stage) btnImport.getScene().getWindow();
                stage.close();
                result = false;
            }
        }
        return result;
    }

}