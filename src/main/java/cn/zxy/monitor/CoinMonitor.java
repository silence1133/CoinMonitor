package cn.zxy.monitor;

import cn.zxy.config.ConfigLoader;
import cn.zxy.mail.MailUtil;
import cn.zxy.spider.CoinData;
import cn.zxy.spider.Spider;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silence
 * @Date 2017/12/11
 */
public class CoinMonitor implements Runnable {
    private static Map<String, Double> lastPriceMap = null;
    private static int todaySendTimes = 0;
    private static int todayQueryTimes = 0;
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT1 = "yyyy-MM-dd";
    public static String today = parseDateToStr(new Date(), TIME_FORMAT1);
    public static String currentDate;
    public static final String TEXT_FORMAT = "{0}:¥{1},{2}幅:{3}%\n";
    public static final String TITLE_FORMAT = "【{0}】{1}出现较大波动，当前价格：{2}";

    @Override
    public void run() {
        try {
            if (ConfigLoader.getConfig().getMonitorSwitch().equals("off")) {
                System.out.println("监控已经关闭");
                return;
            }
            Map<String, Double> priceMap = Spider.getCoinDatas();
            if (priceMap == null) {
                return;
            }
            todayQueryTimes++;
            currentDate = parseDateToStr(new Date(), TIME_FORMAT1);
            //确保波动超出了预设的范围，并且当天发送邮件没有超过次数才发送邮件
            if (!currentDate.equals(today)) {
                todaySendTimes = 0;
                today = currentDate;
            }
            boolean exceedRiseLevel = false;
            Map<String, CoinData> coinDataMap = new HashMap<>();
            if (lastPriceMap != null) {
                for (Map.Entry<String, Double> entry : priceMap.entrySet()) {
                    if (!lastPriceMap.containsKey(entry.getKey())) {
                        lastPriceMap.put(entry.getKey(), entry.getValue());
                    }
                    CoinData coinData = new CoinData(entry.getKey(), entry.getValue(), calRiseLevel(lastPriceMap.get(entry.getKey()), entry.getValue()));
                    if (Math.abs(coinData.getRiseLevel()) >= ConfigLoader.getConfig().getMonitorLevel()) {
                        exceedRiseLevel = true;
                        //价格超过了幅度的修改原来记录的价格
                        lastPriceMap.replace(entry.getKey(), entry.getValue());
                    }
                    coinDataMap.put(coinData.getKey(), coinData);
                }
            } else {
                for (Map.Entry<String, Double> entry : priceMap.entrySet()) {
                    coinDataMap.put(entry.getKey(), new CoinData(entry.getKey(), entry.getValue(), 0D));
                }
                lastPriceMap = priceMap;
            }
            String notifyText = formatText(coinDataMap);
            if (exceedRiseLevel && todaySendTimes < 100) {
                sendEmail(coinDataMap, notifyText);
                todaySendTimes++;
            }
            notifyText = notifyText.concat("今日累计通知次数：").concat(String.valueOf(todaySendTimes)).concat("\n");
            System.out.println(notifyText);
        } catch (Throwable e) {
            //确保出线异常导致定时器依然执行
            e.printStackTrace();
            return;
        }
    }

    private Double calRiseLevel(Double before, Double current) {
        return ((current - before) / current) * 100;
    }

    private void sendEmail(Map<String, CoinData> coinDataMap, String notifyText) {
        CoinData coinData = coinDataMap.entrySet().stream().filter(x -> Math.abs(x.getValue().getRiseLevel()) >= ConfigLoader.getConfig().getMonitorLevel()).findFirst().get().getValue();
        String title = MessageFormat.format(TITLE_FORMAT, coinData.getRiseLevel() >= 0 ? "涨↑↑↑↑" : "跌↓↓↓↓", coinData.getKey(), formatDouble(coinData.getRmbPrice()));
        ;
        MailUtil.send(title, notifyText, ConfigLoader.getConfig().getEmails().stream().toArray(String[]::new));
    }

    private String formatText(Map<String, CoinData> coinDataMap) {
        StringBuffer line1 = new StringBuffer(">>>>>>>查询时间：").append(parseDateToStr(new Date(), TIME_FORMAT)).append("\n");
        for (Map.Entry<String, CoinData> entry : coinDataMap.entrySet()) {
            String key = entry.getValue().getKey();
            double rmbPrice = formatDouble(entry.getValue().getRmbPrice());
            double riseLevel = formatDouble(entry.getValue().getRiseLevel());
            line1.append(MessageFormat.format(TEXT_FORMAT, key, rmbPrice, riseLevel >= 0 ? "涨" : "跌", Math.abs(riseLevel)));
        }
        line1.append("今日累计监控次数：").append(todayQueryTimes).append("\n");
        return line1.toString();
    }

    private double formatDouble(Double riseLevel) {
        return new BigDecimal(riseLevel).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private static String parseDateToStr(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }


}
