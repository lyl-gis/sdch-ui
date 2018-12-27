package edu.zju.gis.sdch.ui;

import java.util.ArrayList;

public class Data {
    public ArrayList<String> data = new ArrayList<String>();

    //    public SimpleStringProperty string;
    public Data(ArrayList<String> mydata) {
        data = mydata;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public String getData(int i) {
        return data.get(i);
    }
}
