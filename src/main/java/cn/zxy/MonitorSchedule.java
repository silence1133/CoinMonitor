package cn.zxy;

import cn.zxy.config.ConfigLoader;
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
    public static final ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        String configPath;
        if (args != null && args.length > 0) {
            configPath = args[0];
        } else {
            System.out.println("请指定配置文件的路径");
            return;
        }
        ConfigLoader configLoader = new ConfigLoader(configPath);
        try {
            configLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        service.scheduleAtFixedRate(new CoinMonitor(), 0, ConfigLoader.getConfig().getFrequency(), TimeUnit.MINUTES);
        service.scheduleAtFixedRate(configLoader, 0, 10, TimeUnit.SECONDS);
    }
}
