package cn.zxy.spider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Silence
 * @Date 2017/12/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoinData {
    private String key;
    private Double rmbPrice;
    private Double riseLevel;
}
