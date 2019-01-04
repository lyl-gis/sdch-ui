package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Index {
    private SimpleStringProperty indice;
    private SimpleIntegerProperty shards;
    private SimpleIntegerProperty replicas;
    private SimpleStringProperty description;
    private SimpleStringProperty category;
    Index(){
        indice=new SimpleStringProperty();
        shards=new SimpleIntegerProperty();
        replicas=new SimpleIntegerProperty();
        description=new SimpleStringProperty();
        category=new SimpleStringProperty();
    }
}
