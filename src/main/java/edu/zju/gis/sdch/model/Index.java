package edu.zju.gis.sdch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Index {
    private String indice;
    private Integer shards;
    private Integer replicas;
    private String dtype;
    private String geoType;
    private String description;
    private String category;
    private Date createTime;
    private Long size;
}