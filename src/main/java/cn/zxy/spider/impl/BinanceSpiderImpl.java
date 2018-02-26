package cn.zxy.spider.impl;

import cn.zxy.spider.HttpUtil;
import cn.zxy.spider.Spider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.RetryException;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 从币安获取获取数字名称和对应的价格
 * @author cnbo
 * @date 2018/2/24 19:07
 */
public class BinanceSpiderImpl extends Spider {
    public static final String URL = "https://www.binance.com/exchange/public/product";
    public static final String BTCUSDT = "BTCUSDT";

    /**
     *
     * @return 返回键为数字货币名称，值为数字货币对应的人民币价格的 map 集合
     */
    @Override
    public Map<String, Double> getCoinDatas() {
        String content = null;
        try {
            content = RETRYER_TIME_OUT.call(() -> HttpUtil.doGet(URL, null));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            e.printStackTrace();
        }
        if (content == null) {
            System.out.println("本次获取数据失败");
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<CoinTemp> coinTempList = jsonArray.toJavaList(CoinTemp.class);

        // 筛选以 BTC 作为交易的数字货币
        Map<String, CoinTemp> filterCoinTemps = coinTempList.stream()
                .filter(x -> ((x.getSymbol().equals(BTCUSDT) || x.getSymbol().endsWith("BTC"))))
                .collect(Collectors.toMap(x -> x.getSymbol(), x -> x));

        BigDecimal btcUsdt = filterCoinTemps.get(BTCUSDT).getClose();
        // 获取美元对人民币的汇率
        BigDecimal usdRate = getUsdRate();
        Map<String, Double> resultMap = new HashMap<>(16);

        // 将数字货币的价格换算成人民币
        filterCoinTemps.values().stream().forEach(x -> {
            if (x.getSymbol().equals(BTCUSDT)) {
                resultMap.put(x.getBaseAsset(), x.getClose().multiply(usdRate).doubleValue());
            } else {
                resultMap.put(x.getBaseAsset(), x.getClose().multiply(btcUsdt).multiply(usdRate).doubleValue());
            }
        });
        return resultMap;
    }

    @Data
    static class CoinTemp {
        // 数字货币的英文简称，如 VeChain 的 symbol 是 VEN
        private String symbol;
        // 基于某种基础数字货币进行交易，如 VEN 与 BTC 进行交易，那么 VEN 的 baseAsset 是 VENBTC
        private String baseAsset;
        // 数字货币的价格，如果 VEN 的 baseAsset 是 VENBTC，那么 VEN 的价格就是 XXX BTC
        private BigDecimal close;
    }
}
