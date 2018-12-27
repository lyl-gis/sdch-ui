package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.Main;
import edu.zju.gis.sdch.tool.Importer;
import edu.zju.gis.sdch.util.Contants;
import edu.zju.gis.sdch.util.GdalHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.gdal.ogr.Layer;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DataPreview implements Initializable {
    @FXML
    private Button Dataimport;
    @FXML
    private ProgressBar progressbar;
    @FXML
    private TextField indexxname;
    @FXML
    private TextField indexdescription;
    //    public static Map<String, Map<String, Object>> myrecords;
    @FXML
    private TableView<Data> datapreviewtv;

    @FXML
    private ChoiceBox<Integer> cbPreviewSize;

//
//    @FXML
//    private Button ShowData;

    @FXML
    void dataimport(ActionEvent event) throws IOException {
        //数据入库
        //测试能否正确传参
        String ccIndex = indexxname.getText();
        //   System.out.println(ccindex);
        MainPage MPindex = (MainPage) StageManager.CONTROLLER.get("dataPreviewController");
        String ccDtype = MPindex.cbCategory.getValue();
        // System.out.println(ccdtype);
//       StageManager.STAGE.remove("second");
//     StageManager.CONTROLLER.remove("indexControl");
        String ccLayer = MPindex.cbxLayers.getValue();
        //  System.out.println(cclayer);
        String ccUuidfield = MPindex.cbUuidField.getValue();
        // System.out.println(ccuuidfield);
        Map<String, String> ccFieldMapping = new HashMap<>();//选取字段名与别名的映射
        Map<String, Integer> ccFields = new HashMap<>();//选取字段名与类型的映射
        for (int i = 0; i < MPindex.tableView.getItems().size(); i++) {
            if (MPindex.tableView.getItems().get(i).getUsed().getValue()) {
                ccFields.put(MPindex.tableView.getItems().get(i).getName().getValue(), MPindex.tableView.getItems().get(i).getType().intValue());
            }
        }
        for (int i = 0; i < MPindex.tableView.getItems().size(); i++) {
            if (MPindex.tableView.getItems().get(i).getUsed().getValue()) {
                ccFieldMapping.put(MPindex.tableView.getItems().get(i).getName().getValue(), MPindex.tableView.getItems().get(i).getTargetName().getValue());
            }
        }
        Map<String, Float> ccAnalyzable = new HashMap<>();
        for (int i = 0; i < MPindex.tableView.getItems().size(); i++) {
            if (MPindex.tableView.getItems().get(i).getAnalyzable().getValue()) {
                ccAnalyzable.put(MPindex.tableView.getItems().get(i).getTargetName().getValue(), Float.parseFloat(MPindex.tableView.getItems().get(i).getBoost().getValue().toString()));
            }
        }
        Boolean ccSkipemptygeom = MPindex.rbSkipEmpty.isSelected();//检查单选框是否被选中
//        System.out.println("最后检查一次");
        Importer importer = new Importer(Main.getHelper(), Main.getSetting(), ccIndex, ccDtype, AllParams.mp.reader.getLayer(ccLayer), ccFields, ccUuidfield,
                ccFieldMapping, ccAnalyzable, ccSkipemptygeom, Contants.IndexType.FRAMEWORK, "");
        importer.exec();


    }

//    @FXML
//    void showData(ActionEvent event) {
//
//        MainPage MPindex = (MainPage) StageManager.CONTROLLER.get("dataPreviewController");
//        Layer layer = AllParams.mp.reader.getLayer(MPindex.cbxLayers.getValue());
//        Map<String, Integer> ccfields = new HashMap<>();
//        for (int i = 0; i < MPindex.tableView.getItems().size(); i++) {
//            if (MPindex.tableView.getItems().get(i).getUsed().getValue()) {
//                ccfields.put(MPindex.tableView.getItems().get(i).getName().getValue(), MPindex.tableView.getItems().get(i).getType().intValue());
//            }
//        }
//        SpatialReference sr = layer.GetSpatialRef();
//        CoordinateTransformation transformation = null;
//        if (sr.IsProjected() == 1)
//            transformation = osr.CreateCoordinateTransformation(sr, sr.CloneGeogCS());
//        Map<String, Map<String, Object>> records = GdalHelper.getNextNFeatures(layer, Integer.valueOf(dataNumber.getText()), ccfields, MPindex.cbUuidField.getValue(), MPindex.rbSkipEmpty.isSelected(), transformation);
//        System.out.println("参考参数是否返回");
//        ArrayList<TableColumn<Data, String>> AllColumn = new ArrayList<TableColumn<Data, String>>();
//        int number = 0;
//        Set keys = records.keySet();
//        if (keys != null) {
//            Iterator iterator = keys.iterator();
//            Object key = iterator.next();
//            Map<String, Object> value = records.get(key);
//            Set keys2 = value.keySet();
//            if (keys2 != null) {
//                Iterator iterator2 = keys2.iterator();
//                while (iterator2.hasNext()) {
//                    number++;//得到共选了几个字段
//                    iterator2.next();
//                }
//            }
//        }
//        System.out.println("得到number成功");
//        ArrayList<Data> mylayer = new ArrayList<Data>();
//
//        for (Map.Entry<String, Map<String, Object>> entry : records.entrySet()) {
//            ArrayList<String> mymydata = new ArrayList<String>();
//            for (Map.Entry<String, Object> entry2 : entry.getValue().entrySet()) {
//                mymydata.add(entry2.getValue().toString());
//            }
//            mylayer.add(new Data(mymydata));
//        }
//
//        ObservableList<Data> layerdata = FXCollections.observableArrayList(mylayer);
//        //显示表格
////        Set keyccfield = ccfields.keySet( );
////        Iterator iterator = keyccfield.iterator( );
//        Map<String, String> ccFieldMapping = new HashMap<>();//选取字段名与别名的映射
//        for (int i = 0; i < MPindex.tableView.getItems().size(); i++) {
//            if (MPindex.tableView.getItems().get(i).getUsed().getValue()) {
//                ccFieldMapping.put(MPindex.tableView.getItems().get(i).getName().getValue(), MPindex.tableView.getItems().get(i).getTargetName().getValue());
//            }
//        }
//        java.util.Iterator it = ccFieldMapping.entrySet().iterator();
//        AllColumn.add(new TableColumn<Data, String>("第几列"));//第一列是关于坐标信息的，名字起什么无所谓
//        for (int i = 1; i < number; i++) {
//            java.util.Map.Entry entry5 = (java.util.Map.Entry) it.next();
//            AllColumn.add(new TableColumn<Data, String>(entry5.getValue().toString()));
////            Object key = iterator.next( );
////            iterator.next();
//        }
//        for (Integer i = 0; i < number; i++) {//绑定数据
//            int gg = i;
//            // AllColumn.get(i).setCellValueFactory(cellData -> cellData.getValue().data);
//            AllColumn.get(i).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Data, String>, ObservableValue<String>>() {
//                @Override
//                public ObservableValue<String> call(TableColumn.CellDataFeatures<Data, String> param) {
//                    Integer g = gg;
//                    return new SimpleStringProperty(param.getValue().data.get(gg));
//                }
//            });
//
//        }
//        for (int j = 1; j < number; j++) {//第一列不显示
//            datapreviewtv.getColumns().add(AllColumn.get(j));
//        }
//        datapreviewtv.setItems(layerdata);
//
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbPreviewSize.setItems(FXCollections.observableArrayList(2, 4, 6, 100));
        cbPreviewSize.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
//                datapreviewtv.refresh();
                System.out.println("GGHHHHSSGH");
                MainPage MPindex = (MainPage) StageManager.CONTROLLER.get("dataPreviewController");
                Layer layer = MainPage.reader.getLayer(MPindex.cbxLayers.getValue());
                Map<String, Integer> ccfields = new HashMap<>();
                for (int i = 0; i < MPindex.tableView.getItems().size(); i++) {
                    if (MPindex.tableView.getItems().get(i).getUsed().getValue()) {
                        ccfields.put(MPindex.tableView.getItems().get(i).getName().getValue(), MPindex.tableView.getItems().get(i).getType().intValue());
                    }
                }
                SpatialReference sr = layer.GetSpatialRef();
                CoordinateTransformation transformation = null;
                if (sr.IsProjected() == 1)
                    transformation = osr.CreateCoordinateTransformation(sr, sr.CloneGeogCS());
                Map<String, Map<String, Object>> records = GdalHelper.getNextNFeatures(layer, newValue, ccfields, MPindex.cbUuidField.getValue(), MPindex.rbSkipEmpty.isSelected(), transformation);
                System.out.println("参考参数是否返回");
                ArrayList<TableColumn<Data, String>> AllColumn = new ArrayList<>();
                int number = records.values().iterator().next().size();
                System.out.println("得到number成功");
                ArrayList<Data> mylayer = new ArrayList<>();
                for (Map.Entry<String, Map<String, Object>> entry : records.entrySet()) {
                    ArrayList<String> mymydata = new ArrayList<>();
                    for (Map.Entry<String, Object> entry2 : entry.getValue().entrySet()) {
                        mymydata.add(entry2.getValue().toString());
                    }
                    mylayer.add(new Data(mymydata));
                }
                ObservableList<Data> layerdata = FXCollections.observableArrayList(mylayer);
                //显示表格
                Map<String, String> ccFieldMapping = new HashMap<>();//选取字段名与别名的映射
                for (int i = 0; i < MPindex.tableView.getItems().size(); i++) {
                    if (MPindex.tableView.getItems().get(i).getUsed().getValue()) {
                        ccFieldMapping.put(MPindex.tableView.getItems().get(i).getName().getValue(), MPindex.tableView.getItems().get(i).getTargetName().getValue());
                    }
                }
                java.util.Iterator it = ccFieldMapping.entrySet().iterator();
                AllColumn.add(new TableColumn<Data, String>("第几列"));//第一列是关于坐标信息的，名字起什么无所谓
                for (int i = 1; i < number; i++) {
                    java.util.Map.Entry entry5 = (java.util.Map.Entry) it.next();
                    AllColumn.add(new TableColumn<Data, String>(entry5.getValue().toString()));
                }
                for (int i = 0; i < number; i++) {//绑定数据
                    int gg = i;
                    // AllColumn.get(i).setCellValueFactory(cellData -> cellData.getValue().data);
                    AllColumn.get(i).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Data, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<Data, String> param) {
                            Integer g = gg;
                            return new SimpleStringProperty(param.getValue().data.get(gg));
                        }
                    });

                }
                datapreviewtv.getColumns().clear();
                for (int j = 1; j < number; j++) {//第一列不显示
                    datapreviewtv.getColumns().add(AllColumn.get(j));
                }
                datapreviewtv.setItems(layerdata);
                System.out.println("GGGG");
            }
        });
        cbPreviewSize.setValue(10);
    }

}
