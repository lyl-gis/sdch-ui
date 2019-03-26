package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyAdminArea {
    private SimpleStringProperty code;
    private SimpleStringProperty pCode;
    private SimpleStringProperty name;
    private SimpleStringProperty abbreviation;
    private SimpleStringProperty fullName;
    private SimpleFloatProperty lon;
    private SimpleFloatProperty lat;
    private SimpleStringProperty wkt;

    MyAdminArea() {
        code = new SimpleStringProperty("");
        pCode = new SimpleStringProperty("");
        name = new SimpleStringProperty("");
        abbreviation = new SimpleStringProperty("");
        fullName = new SimpleStringProperty("");
        lon = new SimpleFloatProperty(0);
        lat = new SimpleFloatProperty(0);
        wkt = new SimpleStringProperty("");

    }
}
