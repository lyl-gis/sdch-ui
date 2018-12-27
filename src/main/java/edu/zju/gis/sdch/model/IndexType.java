package edu.zju.gis.sdch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class IndexType implements Serializable {
    private String id;
    private String indice;
    private String dtype;
    private String geoType;
    private String description;
    private String category;
    private Date updateTime;
}