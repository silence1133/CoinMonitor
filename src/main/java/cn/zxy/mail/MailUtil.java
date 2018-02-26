package cn.zxy.mail;

import cn.zxy.config.ConfigLoader;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Silence 000996
 * @data 17/12/11
 */
public class MailUtil {
    public static void main(String[] args) throws Exception {
        ConfigLoader configLoader = new ConfigLoader("E:\\IdeaProject\\CoinMonitor\\config.json");
        configLoader.load();
        send("222", "内容发到付\n测试", "hubo18163912002@163.com");
    }

    /**
     * 发件人的邮箱需开启了 POP3/SMTP/IMAP；
     * 同时密码是邮箱注册所属的邮件服务商的邮箱客户端授权码(在第一次开启 POP3/SMTP/IMAP
     *  时，会要求设置)；
     *  发件人的邮箱和邮件服务器的地址必须同属于一个邮件服务提供商；
     * @param title
     * @param content
     * @param receiveEmail
     */
    public static void send(String title, String content, String... receiveEmail) {
        // 发件人电子邮箱
        String from = ConfigLoader.getSystemConfig().getConfig().getEmailFrom();

        /* 指定发送邮件的主机为 smtp.qq.com
        QQ 邮件服务器*/
//        String host = "smtp.exmail.qq.com";
        String host = "smtp.163.com";

        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");

        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new MyAuthenricator(from, ConfigLoader.getSystemConfig().getConfig().getEmailPassword()));
        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            Address[] receiveAddress = Arrays.asList(receiveEmail).stream().map(x -> {
                try {
                    return new InternetAddress(x);
                } catch (AddressException e) {
                    e.printStackTrace();
                    return null;
                }
            }).toArray(InternetAddress[]::new);
            // Set To: 头部头字段
            message.addRecipients(Message.RecipientType.TO, receiveAddress);
            // Set Subject: 头部头字段
            message.setSubject(title);
            // 设置消息体
            message.setText(content);
            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully ");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
