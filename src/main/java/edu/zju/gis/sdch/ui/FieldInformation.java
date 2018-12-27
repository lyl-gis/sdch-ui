package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableNumberValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FieldInformation {
    private SimpleStringProperty name;
    private ObservableNumberValue type;
    private SimpleBooleanProperty analyzable;
    private SimpleBooleanProperty used;
    private ObservableNumberValue boost;
    private SimpleStringProperty desc;
    private SimpleStringProperty targetName;

    public FieldInformation() {
        name = new SimpleStringProperty();
        type = new SimpleIntegerProperty();
        analyzable = new SimpleBooleanProperty();
        used = new SimpleBooleanProperty();
        boost = new SimpleFloatProperty();
        desc = new SimpleStringProperty();
        targetName = new SimpleStringProperty();
    }
}


