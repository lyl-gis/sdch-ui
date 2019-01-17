package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyIndex {
    private SimpleStringProperty indice;
    private SimpleIntegerProperty shards;
    private SimpleIntegerProperty replicas;
    private SimpleStringProperty description;
    private SimpleStringProperty category;
    private SimpleBooleanProperty deleted;
    private SimpleBooleanProperty   modified;
    MyIndex(){
        indice = new SimpleStringProperty("");
        shards = new SimpleIntegerProperty(1);
        replicas = new SimpleIntegerProperty(1);
        description = new SimpleStringProperty("");
        category = new SimpleStringProperty("");
        deleted=new SimpleBooleanProperty(false);
        modified=new SimpleBooleanProperty(false);
    }
}
