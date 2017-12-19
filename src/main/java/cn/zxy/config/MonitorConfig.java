package cn.zxy.config;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Silence 000996
 * @data 17/12/14
 */
@Data
public class MonitorConfig {
    private String monitorSwitch;
    private List<String> coinTypes;
    private Integer monitorLevel;
    private Integer frequency;
    private List<String> emails;
    private String emailFrom;
    private String emailPassword;
}
