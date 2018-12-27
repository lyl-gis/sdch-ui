package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.util.FGDBReader;
import edu.zju.gis.sdch.util.GdalHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import org.gdal.ogr.Layer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class MainPage implements Initializable {
    @FXML
    private Button btnPreview;
    @FXML
    private Button btnChooseFile;
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
        btnPreview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                StageManager.CONTROLLER.put("dataPreviewController", MainPage.this);
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("DataPreview.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(root);
                System.out.println("跳转前");
                AllPages.dataPreviewStage.setTitle("数据预览");
                AllPages.dataPreviewStage.setScene(scene);
                AllPages.dataPreviewStage.show();
                //将datapreview窗口和控制器保存到map中
                StageManager.STAGE.put("dataPreviewStage", AllPages.dataPreviewStage);
                System.out.println("加入到controller中");
            }
        });
        MyCategory c1 = new MyCategory(1, 1, "xzm");
        MyCategory c2 = new MyCategory(1, 1, "road");
        MyCategory c3 = new MyCategory(1, 1, "添加");
        MyCategory c4 = new MyCategory(1, 1, "删除");
        MyCategory c5 = new MyCategory(1, 1, "管理");
        AllParams.manageCategory.Addcategory(c1);
        AllParams.manageCategory.Addcategory(c2);
        AllParams.manageCategory.Addcategory(c3);
        AllParams.manageCategory.Addcategory(c4);
        AllParams.manageCategory.Addcategory(c5);
        // AllParams.manageCategory.Allallcategorydescribe(AllParams.manageCategory.allcategory);
        cbCategory.setItems(FXCollections.observableArrayList(
                AllParams.manageCategory.Allallcategorydescribe(AllParams.manageCategory.allcategory)));
        //layers.setItems(FXCollections.observableArrayList(""));
//        layers.setItems(FXCollections.observableArrayList(
//                alllayernames));
//        cbsy.setItems(FXCollections.observableArrayList(
//                "sdmap", "newmap", "mymap"));
//        onecolumn.setCellValueFactory(cellData -> cellData.getValue().layernumber);
//        twocolumn.setCellValueFactory(cellData -> cellData.getValue().layername);
        // this.showlayerTable(this.getlayerdata());
        this.ManageTuceng();
        tcFieldName.setCellValueFactory(cellData -> cellData.getValue().getName());
        tcFieldType.setCellValueFactory(cellData -> cellData.getValue().getType());
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
    }

    @FXML
    void choose(ActionEvent event) throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(AllPages.mainStage);
        String path = file.getPath();//选择的文件夹路径
        btnChooseFile.setText(path);
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
                cbUuidField.setItems(FXCollections.observableArrayList(fields.keySet()));
            }
        });

    }

    @FXML
//    void datapreview(ActionEvent event) throws IOException {
//        //跳转到数据预览页面
//        Parent root = FXMLLoader.load(getClass().getResource("DataPreview.fxml"));
//        Scene scene = new Scene(root);
//        AllPages.dataPreviewStage.setTitle("数据预览");
//        AllPages.dataPreviewStage.setScene(scene);
//        AllPages.dataPreviewStage.show();
//        //将datapreview窗口和控制器保存到map中
//        StageManager.CONTROLLER.put("dataPreviewController", this);
//        StageManager.STAGE.put("dataPreviewStage", AllPages.dataPreviewStage);
//        System.out.println("加入到controller中");
//
//        //初始化数据预览表格
//
//
//    }

    //关于数据类型管理
    public void ManageTuceng() {
        System.out.println("进入函数略略略略");
        cbCategory.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldVal, newVal) -> {
                    // System.out.println(oldVal);oldVal的值为“选择数据类型”
                    System.out.println(newVal);
                    if (newVal == "添加") {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("CategoryManager.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);
                        AllPages.categoryMangageStage.setTitle("增加分类体系");
                        AllPages.categoryMangageStage.setScene(scene);
                        AllPages.categoryMangageStage.show();
                        System.out.println("1");
                        //AllParams.manageCategory.Addcategory(CategoryManager.MySureAdd());
                        System.out.println("运行到断点");
//                        AllParams.mtg.SureAdd();
//                        AllParams.mtg.cateid.setText("1");
//                        AllParams.mtg.parcateid.setText("2");
//                        AllParams.mtg.catedes.setText("3");
//                        Integer id1=Integer.parseInt(AllParams.mtg.cateid.getText());
//                        Integer id2=Integer.parseInt(AllParams.mtg.parcateid.getText());
//                        String  des=AllParams.mtg.catedes.getText();
//                        MyCategory newcate= new MyCategory(id1,id2,des);
//                        MyCategory newcate= new MyCategory(1,2,"自己的数据");
//                        AllParams.manageCategory.Addcategory(newcate);
//                        System.out.println("已添加");
//                        System.out.println("添加成功");
//                        cb.setItems(FXCollections.observableArrayList(
//                                AllParams.manageCategory.Allallcategorydescribe(AllParams.manageCategory.allcategory) ));
//                        System.out.println("3");
                    }
                    if (newVal == "删除") {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("Managetucengdelete.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);
                        AllPages.categoryMangageStage.setTitle("删除分类体系");
                        AllPages.categoryMangageStage.setScene(scene);
                        AllPages.categoryMangageStage.show();
                        System.out.println("1");
                        //AllParams.manageCategory.Addcategory(CategoryManager.MySureAdd());
                        System.out.println("运行到断点");
                    }
                    if (newVal == "管理") {
                        System.out.println("选中管理选项1");
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("CategoryManage.fxml"));
                            System.out.println("选中管理选项2");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);
//                        AllPages.categoryMangageStage.setTitle("删除分类体系");
                        AllPages.categoryMangageStage.setScene(scene);
                        AllPages.categoryMangageStage.show();
//                        System.out.println("1");
//                        //AllParams.manageCategory.Addcategory(CategoryManager.MySureAdd());
//                        System.out.println("运行到断点");
                    }
                }
        );

    }

}