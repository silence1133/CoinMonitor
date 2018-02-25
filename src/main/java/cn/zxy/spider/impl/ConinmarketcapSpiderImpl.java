package cn.zxy.spider.impl;

import cn.zxy.spider.HttpUtil;
import cn.zxy.spider.Spider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.rholder.retry.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 从 coinmarketcap 获取数字货币的价格
 * @author cnbo
 * @date 2018/2/24 18:49
 */
public class ConinmarketcapSpiderImpl extends Spider {
    public static final String COINMARKETCAP_URL = "https://api.coinmarketcap.com/v1/ticker/?limit=0";


    /**
     *
     * @return 返回键为数字货币名称，值为数字货币对应的人民币价格的 map 集合
     */
    @Override
    public  Map<String, Double> getCoinDatas() {
        Map<String, Double> resultMap = new HashMap<>(16);
        String content = null;
        try {
            content = RETRYER_TIME_OUT.call(() -> HttpUtil.doGet(COINMARKETCAP_URL, null));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            e.printStackTrace();
        }
        if (content == null) {
            System.out.println("本次获取数据失败");
            return resultMap;
        }

        JSONArray jsonArray = JSON.parseArray(content);
        List<ConinmarketcapSpiderImpl.CoinTemp> coinTempList = jsonArray.toJavaList(ConinmarketcapSpiderImpl.CoinTemp.class);

        // 获取美元兑人民币的汇率
        BigDecimal usdRate = getUsdRate();

        coinTempList.stream().forEach(x -> {
            if (x.getPrice_usd() != null) {
                resultMap.put(x.getSymbol(), x.getPrice_usd().multiply(usdRate).doubleValue());
            }
        });

        return resultMap;
    }

    @Data
    static class CoinTemp {
        // 数字货币的英文简称，如 VeChain 的 symbol 是 VEN
        private String symbol;
        // 数字货币对美元的价格
        private BigDecimal price_usd;
    }
}
