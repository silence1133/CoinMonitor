package cn.zxy.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author Silence 000996
 * @data 17/12/14
 */
public class ConfigLoader implements Runnable {
    private String configPath;
    private static Long lastModifyTime;
    private static SystemConfig systemConfig;

    public ConfigLoader(String configPath) {
        this.configPath = configPath;
    }

    public static void main(String[] args) throws Exception {
        // 读取配置文件
        BufferedReader reader = new BufferedReader(new FileReader("E:\\IdeaProject\\CoinMonitor\\config.json"));
        StringBuilder configContent = new StringBuilder();
        while (reader.ready()) {
            configContent.append(reader.readLine());
        }

        JSONObject jsonObject = JSON.parseObject(configContent.toString());
        System.out.println(JSON.parseObject(configContent.toString(), SystemConfig.class));

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
        File file = new File(configPath);
        if (lastModifyTime == null || lastModifyTime != file.lastModified()) {
            // 读取配置文件
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder configContent = new StringBuilder();
            while (reader.ready()) {
                configContent.append(reader.readLine());
            }

            JSONObject jsonObject = JSON.parseObject(configContent.toString());
            systemConfig = JSON.parseObject(configContent.toString(), SystemConfig.class);

            lastModifyTime = file.lastModified();
            System.out.println("reload system config:" + systemConfig);
        }
    }

    public static SystemConfig getSystemConfig() {
        return systemConfig;
    }
}
