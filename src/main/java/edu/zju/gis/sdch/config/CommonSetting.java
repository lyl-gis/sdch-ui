package edu.zju.gis.sdch.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author yanlong_lee@qq.com
 * @version 1.0 2018/11/13
 */
@Getter
@Setter
@ToString
public class CommonSetting {
    private String esName;
    private List<String> esHosts;
    private int esPort;
    private int esShards;
    private int esReplicas;
    private Float esFieldBoostDefault;
}
