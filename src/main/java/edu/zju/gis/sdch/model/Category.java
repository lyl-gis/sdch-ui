package edu.zju.gis.sdch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Category {
    private String id;
    private String pId;
    private String func;
    private String description;

    //wmx加了一个构造函数
    public Category(String id, String pId, String func, String description) {
        id = id;
        pId = pId;
        func = func;
        description = description;
    }

    public Category() {
    }
}