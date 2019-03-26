package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyPoiType {
    private SimpleStringProperty code;
    private SimpleStringProperty pCode;
    private SimpleStringProperty name;
    private SimpleStringProperty code4;

    MyPoiType() {
        code = new SimpleStringProperty("");
        pCode = new SimpleStringProperty("");
        name = new SimpleStringProperty("");
        code4 = new SimpleStringProperty("");
    }
}
