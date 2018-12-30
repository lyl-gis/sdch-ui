package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
class FieldInformation {
    static final List<String> fixedFields = Arrays.asList("lsid", "name", "name1", "name2", "name3", "name4", "name5"
            , "abbreviation", "address", "telephone", "priority", "addcode", "kind", "clasid", "entiid", "entiid1"
            , "entiid2", "entiid3", "entiid4", "entiid5");
    private SimpleStringProperty name;
    private SimpleIntegerProperty type;
    private SimpleBooleanProperty analyzable;
    private SimpleBooleanProperty used;
    private SimpleFloatProperty boost;
    private SimpleStringProperty desc;
    private SimpleStringProperty targetName;

    FieldInformation() {
        name = new SimpleStringProperty();
        type = new SimpleIntegerProperty();
        analyzable = new SimpleBooleanProperty();
        used = new SimpleBooleanProperty();
        boost = new SimpleFloatProperty();
        desc = new SimpleStringProperty();
        targetName = new SimpleStringProperty();
    }
}


