package edu.zju.gis;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MyCategory {
    public SimpleIntegerProperty Categoryid;
    public SimpleIntegerProperty Categoryparentid;
    public SimpleStringProperty Categorydescribtion;

    public MyCategory(Integer categoryid, Integer categoryparentid, String categorydescribtion) {
        this.Categoryid = new SimpleIntegerProperty(categoryid);
        this.Categoryparentid = new SimpleIntegerProperty(categoryparentid);
        this.Categorydescribtion = new SimpleStringProperty(categorydescribtion);
    }

    public Integer GetCategoryid() {
        return Categoryid.get();
    }

    public Integer GetCategoryparentid() {
        return Categoryparentid.get();
    }

    public String GetCategorydescribtion() {
        return Categorydescribtion.get();
    }
}
