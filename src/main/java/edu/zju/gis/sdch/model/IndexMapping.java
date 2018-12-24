package edu.zju.gis.sdch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class IndexMapping {
    private String id;
    private String indice;
    private String fieldName;
    private String fieldType;
    private Float boost;
    private Boolean analyzable;
    private String description;
    private Date createTime;
}