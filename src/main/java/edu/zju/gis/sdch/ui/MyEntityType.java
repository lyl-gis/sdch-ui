package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyEntityType {
    private SimpleStringProperty code;
    private SimpleStringProperty pCode;
    private SimpleStringProperty name;

    MyEntityType() {
        code = new SimpleStringProperty("");
        pCode = new SimpleStringProperty("");
        name = new SimpleStringProperty("");
    }
}
