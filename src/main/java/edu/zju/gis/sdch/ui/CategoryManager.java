package edu.zju.gis.sdch.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CategoryManager {

    @FXML
    public TextField cateid;

    @FXML
    public TextField parcateid;

    @FXML
    public TextField catedes;

    @FXML
    private Button sureadd;

    @FXML
    public void SureAdd(ActionEvent event) {
        Integer id1 = Integer.parseInt(cateid.getText());
        Integer id2 = Integer.parseInt(parcateid.getText());
        String des = catedes.getText();
        MyCategory newcate = new MyCategory(id1, id2, des);
        AllParams.manageCategory.Addcategory(newcate);
        System.out.println("已添加");
        System.out.println("HHHHHHH");
        AllParams.mp.cbCategory.setItems(FXCollections.observableArrayList(
                AllParams.manageCategory.Allallcategorydescribe(AllParams.manageCategory.allcategory)));
        System.out.println(AllParams.mp.cbCategory.getItems());
    }

//    public void SureAdd() {
//
////        Integer id1=Integer.parseInt(cateid.getText());
////        Integer id2=Integer.parseInt(parcateid.getText());
////        String  des=catedes.getText();
////        MyCategory newcate= new MyCategory(id1,id2,des);
////        a1.manageCategory.Addcategory(newcate);
////        System.out.println("已添加");
//
//    }

}