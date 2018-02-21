package cn.zxy.monitor;

import cn.zxy.config.Coin;
import cn.zxy.config.ConfigLoader;
import cn.zxy.config.Subscriber;
import cn.zxy.config.SystemConfig;
import cn.zxy.mail.MailUtil;
import cn.zxy.spider.CoinData;
import cn.zxy.spider.Spider;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silence
 * @Date 2017/12/11
 */
public class CoinMonitor implements Runnable {
    private static Map<String, Map<String, Double>> lastPriceMap = new HashMap<>();
    private static int todaySendTimes = 0;
    private static int todayQueryTimes = 0;
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT1 = "yyyy-MM-dd";
    public static String today = parseDateToStr(new Date(), TIME_FORMAT1);
    public static String currentDate;
    public static final String TEXT_FORMAT = "{0}:¥{1},{2}幅:{3}%\n";
    public static final String EMAIL_TITLE = "亲，您的数字货币带来了神秘信息，请查收！";

    @Override
    public void run() {
        try {

            if (!ConfigLoader.getSystemConfig().getConfig().getOpenMonitor()) {
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

            Map<String, Map<String, CoinData>> feedbackDatas = new HashMap<>(16);
            if (lastPriceMap != null) {
                Subscriber[] subscribers = ConfigLoader.getSystemConfig().getSubscribers();
                for (Subscriber subscriber : subscribers) {
                    if (!lastPriceMap.containsKey(subscriber.getEmail())) {
                        lastPriceMap.put(subscriber.getEmail(), new HashMap<>(16));
                    }
                    Map<String, CoinData> coinDataMap = new HashMap<>(16);
                    Map<String, Double> coinPriceMap = lastPriceMap.get(subscriber.getEmail());
                    for (Coin coin : subscriber.getCoins()) {
                        Double currentPrice = priceMap.get(coin.getCoinName());
                        if (!coinPriceMap.containsKey(coin.getCoinName())) {
                            coinPriceMap.put(coin.getCoinName(), currentPrice);
                        }
                        Double lastPrice = coinPriceMap.get(coin.getCoinName());
                        Double currentRiseLevel = calRiseLevel(lastPrice, currentPrice);
                        if (Math.abs(currentRiseLevel) >= coin.getMonitorLevel()) {
                            CoinData coinData = new CoinData(coin.getCoinName(), currentPrice, currentRiseLevel);
                            coinDataMap.put(coinData.getKey(), coinData);

                            // 更新历史价格
                            coinPriceMap.replace(coin.getCoinName(), currentPrice);
                        }
                    }
                    if (coinDataMap.size() > 0) {
                        feedbackDatas.put(subscriber.getEmail(), coinDataMap);
                    }
                }

            }

            if (todaySendTimes >= ConfigLoader.getSystemConfig().getConfig().getMaxSendEmails()) {
                return;
            }

            Set<String> emails = feedbackDatas.keySet();
            for(String email : emails) {
                Map<String, CoinData> coinDataMap = feedbackDatas.get(email);
                String notifyText = formatText(coinDataMap);
                MailUtil.send(EMAIL_TITLE, notifyText, email);
                todaySendTimes++;
                notifyText = notifyText.concat("今日累计通知次数：").concat(String.valueOf(todaySendTimes)).concat("\n");
                System.out.println(notifyText);
            }


        } catch (Throwable e) {
            //确保出线异常导致定时器依然执行
            e.printStackTrace();
            return;
        }
    }

    private Double calRiseLevel(Double before, Double current) {
        return ((current - before) / current) * 100;
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
