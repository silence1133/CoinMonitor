package cn.zxy.config;

import lombok.Data;

/**
 * Created by cnbo on 2018/2/12 13:27
 */
@Data
public class SystemConfig {

    private PublicConfig config;

    private Subscriber[] subscribers;

}
