package edu.zju.gis.sdch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class AdminArea {
    private String code;
    private String pCode;
    private String name;
    private String abbreviation;
    private String fullName;
    private BigDecimal lon;
    private BigDecimal lat;
    private String wkt;
}