package edu.zju.gis.sdch.ui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Test implements Initializable {

    @FXML
    private TableView<FieldInformation> TestTableview;

    @FXML
    public TableColumn<FieldInformation, String> filedName;
    @FXML
    public TableColumn<FieldInformation, String> filedType;
    @FXML
    public TableColumn<FieldInformation, Boolean> ifUsed;
    @FXML
    public TableColumn<FieldInformation, Boolean> ifAnalyzable;
    @FXML
    public TableColumn<FieldInformation, Integer> boost;
    @FXML
    public TableColumn<FieldInformation, Integer> description;
    @FXML
    public TableColumn<FieldInformation, Integer> nameOther;

    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<FieldInformation> mylayer = new ArrayList<FieldInformation>();
//        mylayer.add(new FieldInformation("NAME1", 1));
//        mylayer.add(new FieldInformation("ID", 2));
        ObservableList<FieldInformation> layerdata = FXCollections.observableArrayList(mylayer);

        ifUsed.setCellFactory(CheckBoxTableCell.forTableColumn(ifUsed));

//        used.setCellFactory(new Callback<TableColumn<FieldInformation, Boolean>, TableCell<FieldInformation, Boolean>>() {
//            @Override
//            public TableCell<FieldInformation, Boolean> call(TableColumn<FieldInformation, Boolean> param) {
//                public Table
//                return null;
//            }
//        });
        ifAnalyzable.setCellFactory(CheckBoxTableCell.forTableColumn(ifAnalyzable));
        boost.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        description.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        nameOther.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        TestTableview.getColumns().addAll();
        TestTableview.setItems(layerdata);//显示数据
        layerdata.addListener(new ListChangeListener<FieldInformation>() {
            @Override
            public void onChanged(Change<? extends FieldInformation> c) {
                while (c.next()) {
                    if (c.wasUpdated()) {
                        System.out.println("选择发生改变");
                    }
                }
            }
        });


    }


}

