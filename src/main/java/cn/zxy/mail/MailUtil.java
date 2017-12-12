package cn.zxy.mail;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Silence 000996
 * @data 17/12/11
 */
public class MailUtil {
    public static void main(String[] args) {
        send("标题", "内容发到付\n测试", "531610808@qq.com");
    }

    public static void send(String title, String content, String... receiveEmail) {
        // 发件人电子邮箱
        String from = "zhangxiaoyong@fcbox.com";

        // 指定发送邮件的主机为 smtp.qq.com
        String host = "smtp.exmail.qq.com";  //QQ 邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new MyAuthenricator("zhangxiaoyong@fcbox.com", "Fc123456!"));
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
