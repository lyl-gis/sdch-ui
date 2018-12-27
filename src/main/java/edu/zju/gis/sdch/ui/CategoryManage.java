package edu.zju.gis.sdch.ui;

import edu.zju.gis.sdch.model.Category;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CategoryManage {
    @FXML
    private TableView<Category> CategoryTableview;
    @FXML
    private TableColumn<Category, String> Categoryid;
    @FXML
    private TableColumn<Category, String> CategorypId;
    @FXML
    private TableColumn<Category, String> Categorfunc;
    @FXML
    private TableColumn<Category, String> Categorydescription;
    @FXML
    private Button CategoryAdd;
    @FXML
    private Button CategoryDelete;

    @FXML
    private Button CategorySelect;

    @FXML
    private Button CategoryModify;

    @FXML
    void categoryAdd(ActionEvent event) {

    }

    @FXML
    void categoryDelete(ActionEvent event) {

    }

    @FXML
    void categoryModify(ActionEvent event) {

    }

    @FXML
    void categorySelect(ActionEvent event) {
    }

    public ObservableList<Category> getCategory() {
        ArrayList<Category> category = new ArrayList<Category>();
        category.add(new Category("1", "1", "3", "4"));
        category.add(new Category("5", "6", "7", "8"));
        //从数据库中得到category
        ObservableList<Category> mycategory = FXCollections.observableArrayList(category);
        return mycategory;
    }

    public void showCategorytable(ObservableList<Category> layerdata) {
        //数据绑定
        Categoryid.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> param) {
                return new SimpleStringProperty(param.getValue().getId());
            }
        });
        CategorypId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> param) {
                return new SimpleStringProperty(param.getValue().getPId());
            }
        });
        Categorfunc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> param) {
                return new SimpleStringProperty(param.getValue().getFunc());
            }
        });
        Categorydescription.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> param) {
                return new SimpleStringProperty(param.getValue().getDescription());
            }
        });
        //选择显示的列
        CategoryTableview.getColumns().addAll(Categoryid, CategorypId, Categorfunc, Categorydescription);
        //显示数据
        CategoryTableview.setItems(layerdata);
    }

    public void initialize(URL location, ResourceBundle resources) {


        this.showCategorytable(this.getCategory());

    }
}

