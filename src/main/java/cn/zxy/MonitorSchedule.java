package cn.zxy;

import cn.zxy.spider.Spider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Silence
 * @Date 2017/12/12
 */
public class MonitorSchedule {
    public static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private static Map<String, Double> lastPriceMap = null;
    public static void main(String[] args) {
        service.scheduleAtFixedRate(()->{
            Map<String, Double> priceMap = Spider.getCoinDatas();
            String text = formatText(priceMap);
            priceMap.remove("BTC");
            System.out.println(text);
            if(lastPriceMap != null){
                for (Map.Entry entry : lastPriceMap.entrySet()){
/*                    if(entry.getValue()){

                    }*/
                }
            }
            lastPriceMap = priceMap;
        },0,2, TimeUnit.MINUTES);
    }

    private static String formatText(Map<String, Double> priceMap) {
        return null;
    }
}
