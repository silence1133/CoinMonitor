package cn.zxy.config;

import lombok.Data;

/**
 * 订阅者信息
 * Created by cnbo on 2018/2/12 13:24
 */
@Data
public class Subscriber {

    private String email;

    private Coin[] coins;

}
