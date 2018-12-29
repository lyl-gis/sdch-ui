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

}