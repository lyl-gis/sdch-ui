package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MyLayer {
    public SimpleStringProperty layernumber;
    public SimpleStringProperty layername;
    public SimpleStringProperty layerdescription;
    public Boolean layerifanalyzable;
    public SimpleIntegerProperty layerboost;

    // public SimpleStringProperty  fuzzy;
//    public Integer layernumber;
//    public String  layername;
    public MyLayer(String number, String name, String description
//            ,Boolean ifanalyzable,Integer boost
    ) {
        this.layername = new SimpleStringProperty(name);
        this.layernumber = new SimpleStringProperty(number);
        this.layerdescription = new SimpleStringProperty(description);
//    this.layerifanalyzable=ifanalyzable;
//    this.layerboost=new SimpleIntegerProperty(boost);
//        layernumber=number;
//        layername=name;
    }


    //成员函数默认值构造法方法
//    public MyLayer(String number,String description)
//    {
//        this.layername= new SimpleStringProperty("1");
//        this.layernumber= new SimpleStringProperty(number);
//        this.layerdescription=new SimpleStringProperty(description);
////    this.layerifanalyzable=ifanalyzable;
////    this.layerboost=new SimpleIntegerProperty(boost);
////        layernumber=number;
////        layername=name;
//    }


    public String getName() {
        return layername.get();
    }

    public String getNumber() {
        return layernumber.get();
    }
}
