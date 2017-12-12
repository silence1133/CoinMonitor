package cn.zxy;

import cn.zxy.monitor.CoinMonitor;
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
    public static void main(String[] args) {
        service.scheduleAtFixedRate(new CoinMonitor(),0,2, TimeUnit.MINUTES);
    }
}
