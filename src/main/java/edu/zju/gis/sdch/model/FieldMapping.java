package edu.zju.gis.sdch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/12/20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldMapping {
    private String srcName;
    private String destName;
    private Integer oftType;

    /**
     * @return the destName if it is non-null and non-empty
     */
    public String getDestName() {
        return destName == null || destName.isEmpty() ? srcName : destName;
    }
}
