package cn.zxy.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.stream.Collectors;

/**
 * @author Silence
 * @Date 2017/12/11
 */
public class Spider {
    public static final String URL = "https://www.binance.com/exchange/public/product";
    public static final String VENBTC = "VENBTC";
    public static final String EOSBTC = "EOSBTC";
    public static final String ETHBTC = "ETHBTC";
    public static final String BTCUSDT = "BTCUSDT";
    public static final BigDecimal USD_RATE = new BigDecimal(6.6071);

    public static Map<String, Double> getCoinDatas() {
        String content = HttpUtil.doGet(URL, null);
        if (content == null || content.trim().length() == 0) {
            System.out.println("本次获取数据失败");
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<CoinTemp> coinTempList = jsonArray.toJavaList(CoinTemp.class);

        Map<String, CoinTemp> filterCoinTemps = coinTempList.stream().filter(x -> {
            return x.getSymbol().equals(VENBTC) || x.getSymbol().equals(BTCUSDT) || x.getSymbol().equals(EOSBTC) || x.getSymbol().equals(ETHBTC);
        }).collect(Collectors.toMap(x -> x.getSymbol(), x -> x));

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

    /**
     * 发送 get请求
     */
    public static String get(String url) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            System.out.println("executing request " + httpget.getURI());
            // 执行get请求.
            CloseableHttpResponse response = httpClient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    @Data
    static class CoinTemp {
        private String symbol;
        private String baseAsset;
        private BigDecimal close;
    }
}
