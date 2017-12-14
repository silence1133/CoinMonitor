package cn.zxy.spider;

import cn.zxy.config.ConfigLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Bidi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Silence
 * @Date 2017/12/11
 */
public class Spider {
    public static final String URL = "https://www.binance.com/exchange/public/product";
    public static final String BTCUSDT = "BTCUSDT";
    public static final BigDecimal USD_RATE = new BigDecimal(6.6071);

    //获取数据失败重试3次
    private static final Retryer<String> RETRYER_TIME_OUT = RetryerBuilder.<String>newBuilder()
            .withWaitStrategy(WaitStrategies.noWait())
            .retryIfException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .retryIfResult(x -> (x == null || x.trim().length() == 0))
            .build();

    public static Map<String, Double> getCoinDatas() {
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

        Map<String, CoinTemp> filterCoinTemps = coinTempList.stream()
                .filter(x -> (x.getSymbol().equals(BTCUSDT) || ConfigLoader.getConfig().getCoinTypes().contains(x.getSymbol().endsWith("BTC") ? x.getSymbol().split("BTC")[0] : x.getSymbol())))
                .collect(Collectors.toMap(x -> x.getSymbol(), x -> x));

        BigDecimal btcUsdt = filterCoinTemps.get(BTCUSDT).getClose();
        Map<String, Double> resultMap = new HashMap<>();
        filterCoinTemps.values().stream().forEach(x -> {
            if (x.getSymbol().equals(BTCUSDT)) {
                resultMap.put(x.getBaseAsset(), x.getClose().multiply(USD_RATE).doubleValue());
            } else {
                resultMap.put(x.getBaseAsset(), x.getClose().multiply(btcUsdt).multiply(USD_RATE).doubleValue());
            }
        });
        return resultMap;
    }

    public static void main(String[] args) throws URISyntaxException {
        Map<String, Double> map = getCoinDatas();
        System.out.println(map);
    }

    @Data
    static class CoinTemp {
        private String symbol;
        private String baseAsset;
        private BigDecimal close;
    }
}
