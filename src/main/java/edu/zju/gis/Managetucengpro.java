package edu.zju.gis;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Managetucengpro implements AllParams {

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
        a1.manageCategory.Addcategory(newcate);
        System.out.println("已添加");
        System.out.println("HHHHHHH");
        a1.mp.cb.setItems(FXCollections.observableArrayList(
                a1.manageCategory.Allallcategorydescribe(a1.manageCategory.allcategory)));
        System.out.println(a1.mp.cb.getItems());
    }

    public void SureAdd() {

//        Integer id1=Integer.parseInt(cateid.getText());
//        Integer id2=Integer.parseInt(parcateid.getText());
//        String  des=catedes.getText();
//        MyCategory newcate= new MyCategory(id1,id2,des);
//        a1.manageCategory.Addcategory(newcate);
//        System.out.println("已添加");

    }

}