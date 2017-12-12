package cn.zxy.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Silence 000996
 * @data 17/12/11
 */
public class MyAuthenricator extends Authenticator {
    private String user ;
    private String pass ;
    public MyAuthenricator(String user, String pass){
        this.user=user;
        this.pass=pass;
    }
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user,pass);
    }
}
