package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
//这个类用于得到要添加文档的各项值
public class DocAdded {
    SimpleStringProperty keys;
    SimpleStringProperty values;

    DocAdded() {
        keys = new SimpleStringProperty();
        values = new SimpleStringProperty();
    }
}
