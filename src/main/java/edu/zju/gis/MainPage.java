package edu.zju.gis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPage implements Initializable, Allpages, AllParams {

    @FXML
    private Button choosefile;

    @FXML
    public ChoiceBox<String> cb = new ChoiceBox<String>();

    @FXML
    void choose(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(p1.mainstage);
        String path = file.getPath();//选择的文件夹路径
    }

    @FXML
    private TableView<MyLayer> tableview;
    @FXML
    private TableColumn<MyLayer, String> onecolumn;
    @FXML
    private TableColumn<MyLayer, String> twocolumn;
    @FXML
    private TableColumn<MyLayer, Boolean> ifused;
    @FXML
    private TableColumn<MyLayer, Boolean> ifanalyzable;
    @FXML
    private TableColumn<MyLayer, Integer> boost;
    @FXML
    private TableColumn<MyLayer, String> description;
    @FXML
    private TableColumn<MyLayer, String> nameother;
    //    @FXML
//    private TableColumn<MyLayer,Boolean> iffuzzy;
    @FXML
    private ChoiceBox<String> Tuceng;
    //    @FXML
//    private Button dataimport;
    @FXML
    private Button Datapreview;
//    @FXML
//    private ChoiceBox<String> cbsy;

    public void initialize(URL location, ResourceBundle resources) {
        MyCategory c1 = new MyCategory(1, 1, "POI数据");
        MyCategory c2 = new MyCategory(1, 1, "框架数据");
        MyCategory c3 = new MyCategory(1, 1, "添加");
        MyCategory c4 = new MyCategory(1, 1, "删除");
        MyCategory c5 = new MyCategory(1, 1, "管理");
        a1.manageCategory.Addcategory(c1);
        a1.manageCategory.Addcategory(c2);
        a1.manageCategory.Addcategory(c3);
        a1.manageCategory.Addcategory(c4);
        a1.manageCategory.Addcategory(c5);
        // a1.manageCategory.Allallcategorydescribe(a1.manageCategory.allcategory);
        cb.setItems(FXCollections.observableArrayList(
                a1.manageCategory.Allallcategorydescribe(a1.manageCategory.allcategory)));
        Tuceng.setItems(FXCollections.observableArrayList(
                "图层1", "图层2", "图层3"));
//        cbsy.setItems(FXCollections.observableArrayList(
//                "sdmap", "newmap", "mymap"));
//        onecolumn.setCellValueFactory(cellData -> cellData.getValue().layernumber);
//        twocolumn.setCellValueFactory(cellData -> cellData.getValue().layername);
        this.showlayerTable(this.getlayerdata());
        this.ManageTuceng();
    }

    @FXML
    void datapreview(ActionEvent event) throws IOException {
        //跳转到数据预览页面
        Parent root = FXMLLoader.load(getClass().getResource("Datapreview.fxml"));
        Scene scene = new Scene(root);
        p1.datapreviewstage.setTitle("数据预览");
        p1.datapreviewstage.setScene(scene);
        p1.datapreviewstage.show();
    }


    //关于tableview的操作
    //传入数组
    public ObservableList<MyLayer> getlayerdata() {
        ArrayList<MyLayer> mylayer = new ArrayList<MyLayer>();
        mylayer.add(new MyLayer("NAME1", "名称", "描述一"));
        mylayer.add(new MyLayer("ID", "编号", "描述二"));
        ObservableList<MyLayer> layerdata = FXCollections.observableArrayList(mylayer);
        return layerdata;
    }

    public void showlayerTable(ObservableList<MyLayer> layerdata) {
//        onecolumn.setCellValueFactory(new PropertyValueFactory<>("layernumber"));
//        twocolumn.setCellValueFactory(new PropertyValueFactory<>("layername"));
        onecolumn.setCellValueFactory(cellData -> cellData.getValue().layernumber);//每一列是一个cell,将列与数据关联
        twocolumn.setCellValueFactory(cellData -> cellData.getValue().layername);
        description.setCellValueFactory(cellData -> cellData.getValue().layerdescription);
        nameother.setCellValueFactory(cellData -> cellData.getValue().layername);
        ifused.setCellFactory(new Callback<TableColumn<MyLayer, Boolean>, TableCell<MyLayer, Boolean>>() {
            @Override
            public TableCell<MyLayer, Boolean> call(TableColumn<MyLayer, Boolean> param) {
                TableCell<MyLayer, Boolean> cell = new TableCell<MyLayer, Boolean>() {
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(null);
                        this.setGraphic(null);
                        if (!empty) {
                            CheckBox checkBox = new CheckBox();
                            this.setGraphic(checkBox);
                            //checkbox被选中的监听事件
                            checkBox.selectedProperty().addListener((obVal, oldVal, newVal) -> {
                                if (newVal) {
                                    // 添加选中时执行的代码
                                    System.out.println("第" + this.getIndex() + "行被选中！");
                                    // 获取当前单元格的对象
                                    // this.getItem();
                                }
                            });
                        }
                    }
                };
                return cell;
            }
        });
        ifanalyzable.setCellFactory(new Callback<TableColumn<MyLayer, Boolean>, TableCell<MyLayer, Boolean>>() {
            @Override
            public TableCell<MyLayer, Boolean> call(TableColumn<MyLayer, Boolean> param) {
                TableCell<MyLayer, Boolean> cell = new TableCell<MyLayer, Boolean>() {
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(null);
                        this.setGraphic(null);
                        if (!empty) {
                            CheckBox checkBox = new CheckBox();
                            this.setGraphic(checkBox);
                            //checkbox被选中的监听事件
                            checkBox.selectedProperty().addListener((obVal, oldVal, newVal) -> {
                                if (newVal) {
                                    // 添加选中时执行的代码
                                    System.out.println("第" + this.getIndex() + "行被选中！");
                                    // 获取当前单元格的对象
                                    // this.getItem();
                                }
                            });
                        }
                    }
                };
                return cell;
            }
        });
        boost.setCellFactory(new Callback<TableColumn<MyLayer, Integer>, TableCell<MyLayer, Integer>>() {
            @Override
            public TableCell<MyLayer, Integer> call(TableColumn<MyLayer, Integer> param) {
                TableCell<MyLayer, Integer> cell = new TableCell<MyLayer, Integer>() {
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(null);
                        this.setGraphic(null);
                        if (!empty) {
                            TextField textField = new TextField();
                            this.setGraphic(textField);
                            textField.setText("yyy");

//                             CheckBox checkBox=new CheckBox();
//                             this.setGraphic(checkBox);
//                             //checkbox被选中的监听事件
//                             checkBox.selectedProperty().addListener((obVal, oldVal, newVal) -> {
//                                 if(newVal) {
//                                     // 添加选中时执行的代码
//                                     System.out.println("第" + this.getIndex() + "行被选中！");
//                                     // 获取当前单元格的对象
//                                     // this.getItem();
//                                 }
//                             });
                        }
                    }
                };
                return cell;
            }
        });
        tableview.setItems(layerdata);//显示数据
    }


    //关于数据类型管理
    public void ManageTuceng() {
        System.out.println("进入函数略略略略");
        cb.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldVal, newVal) -> {
                    // System.out.println(oldVal);oldVal的值为“选择数据类型”
                    System.out.println(newVal);
                    if (newVal == "添加") {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("Managetucengpro.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);
                        p1.managetucengpro.setTitle("增加分类体系");
                        p1.managetucengpro.setScene(scene);
                        p1.managetucengpro.show();
                        System.out.println("1");
                        //a1.manageCategory.Addcategory(Managetucengpro.MySureAdd());
                        System.out.println("运行到断点");
//                        a1.mtg.SureAdd();
//                        a1.mtg.cateid.setText("1");
//                        a1.mtg.parcateid.setText("2");
//                        a1.mtg.catedes.setText("3");
//                        Integer id1=Integer.parseInt(a1.mtg.cateid.getText());
//                        Integer id2=Integer.parseInt(a1.mtg.parcateid.getText());
//                        String  des=a1.mtg.catedes.getText();
//                        MyCategory newcate= new MyCategory(id1,id2,des);
//                        MyCategory newcate= new MyCategory(1,2,"自己的数据");
//                        a1.manageCategory.Addcategory(newcate);
//                        System.out.println("已添加");
//                        System.out.println("添加成功");
//                        cb.setItems(FXCollections.observableArrayList(
//                                a1.manageCategory.Allallcategorydescribe(a1.manageCategory.allcategory) ));
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
                        p1.managetucengpro.setTitle("删除分类体系");
                        p1.managetucengpro.setScene(scene);
                        p1.managetucengpro.show();
                        System.out.println("1");
                        //a1.manageCategory.Addcategory(Managetucengpro.MySureAdd());
                        System.out.println("运行到断点");
                    }
                    if (newVal == "管理") {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("Justtry.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);
                        p1.managetucengpro.setTitle("删除分类体系");
                        p1.managetucengpro.setScene(scene);
                        p1.managetucengpro.show();
                        System.out.println("1");
                        //a1.manageCategory.Addcategory(Managetucengpro.MySureAdd());
                        System.out.println("运行到断点");
                    }
                }
        );

    }

}