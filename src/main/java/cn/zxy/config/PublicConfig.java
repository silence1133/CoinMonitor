package cn.zxy.config;

import lombok.Data;

/**
 * Created by cnbo on 2018/2/12 13:28
 */
@Data
public class PublicConfig {

    private Boolean openMonitor;

    private String spiderClass;

    private Integer frequency;

    private Integer maxSendEmails;

    private String emailFrom;

    private String emailPassword;

}
