# CoinMonitor
监控币安数据，超过一定幅度变化发送监控邮件

## 程序入口
cn.zxy.MonitorSchedule


## maven 生成 jar：
在命令行终端输入 mvn clean package

## 运行 jar
在命令行终端输入 java -jar param1 param2  
param1:打包的 jar 路径  
param2:配置文件路径，配置模板按照 https://github.com/silence1133/CoinMonitor/blob/master/config.json 配置即可

## config.json 说明

> |-- config.json  与 cn.zxy.config.SystemConfig.class 对应  
> |--|-- config  与 cn.zxy.config.PublicConfig.class 对应  
> |--|--|-- openMonitor   控制监控的开关，对应的 Java 类为 Boolean  
> |--|--|-- spiderClass   用于指定数字货币信息来源，对应 cn.zxy.spider.Spider.class 的子类；例：指定从 coinmarketcap 获取数字货币的信息，如果没有对应的 Java 类，那么新建一个全名为 cn.zxy.spider.impl.CoinmarketcapSpiderImpl 的 class 继承自 cn.zxy.spider.Spider.class，这时 spiderClass 的值就可以设置为 cn.zxy.spider.impl.CoinmarketcapSpiderImpl   
> |--|--|-- frequency  从数据源获取数据的频率，对应的 Java 类为 Integer  
> |--|--|-- maxSendEmails   每天发送邮件数量的上限，对应的 Java 类为 Integer  
> |--|--|-- emailFrom   发送邮件的邮箱(需登录所属邮件服务商的网站开启 POP3/SMTP/IMAP 服务)，对应的 Java 类为 String
> |--|--|-- emailPassword  发送邮箱的 POP3/SMTP/IMAP 服务 的密码
> |--|-- subscribers  一组订阅者信息，对应 cn.zxy.config.Subscriber.class 数组  
> |--|--|-- email   订阅者邮箱，对应 cn.zxy.config.Subscriber.class 中的 email 属性，类型为 String       
> |--|--|-- coins   数字货币组，对应 cn.zxy.config.Subscriber.class 中的 coins 字段，类型为 cn.zxy.config.Coin.class 数组  
> |--|--|--|-- coinName  数字货币名称，对应 cn.zxy.config.Coin.class 中的 coinName 字段，类型为 String  
> |--|--|--|-- monitorLevel 数字货币的价格波动幅度，对应 cn.zxy.config.Coin 中的 monitorLevel 字段，类型为 String

    
## 注意
如果出现邮件内容乱码的情况，请新建一个 变量名为 JAVA_TOOL_OPTIONS，变量值为 -Dfile.encoding=UTF-8 的环境变量；然后再重新 maven 打包 jar.

