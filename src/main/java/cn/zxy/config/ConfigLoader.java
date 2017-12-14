package cn.zxy.config;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Silence 000996
 * @data 17/12/14
 */
public class ConfigLoader implements Runnable {
    private String configPath;
    private static MonitorConfig config;

    public ConfigLoader(String configPath) {
        this.configPath = configPath;
    }

    public static void main(String[] args) throws Exception {
    }

    @Override
    public void run() {
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() throws Exception {
        Properties pro = new Properties();
        pro.load(new FileInputStream(configPath));

        MonitorConfig monitorConfig = new MonitorConfig();
        Class monitorConfigClass = monitorConfig.getClass();
        for (String name : pro.stringPropertyNames()) {
            Field field = monitorConfigClass.getDeclaredField(name);
            field.setAccessible(true);
            if (field.getType().equals(List.class)) {
                field.set(monitorConfig, Arrays.asList(pro.getProperty(name).trim().split(",")));
            } else if (field.getType().equals(Integer.class)) {
                field.set(monitorConfig, Integer.valueOf(pro.getProperty(name).trim()));
            } else {
                field.set(monitorConfig, pro.getProperty(name).trim());
            }
        }
        config = monitorConfig;
    }

    public static MonitorConfig getConfig() {
        return config;
    }
}
