package edu.zju.gis.sdch.ui;

import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//这个类用于判断一个文档是否要删除和修改
public class Docs {
    public SimpleBooleanProperty ifDeleted;
    public SimpleBooleanProperty ifModified;

    Docs() {
        ifDeleted = new SimpleBooleanProperty();
        ifModified = new SimpleBooleanProperty();
    }
}
