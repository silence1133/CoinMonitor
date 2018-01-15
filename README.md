# CoinMonitor
监控币安数据，超过一定幅度变化发送监控邮件

main入口：
MonitorSchedule

maven打包jar：
mvn clean package

运行：
java -jar param1 param2
param1:打包的jar路径
param2:配置文件路径，配置模板按照https://github.com/silence1133/CoinMonitor/blob/master/config.properties 配置即可

功能特性：
1、支持动态实时配置加载，例如动态调整波动幅度、动态新增新的币种监控、动态增加监控邮箱等等。
2、确保每天只发送一个上限数量的邮件，防止邮件太多。
3、动态关闭监控。

