package cn.zf233.xcloud.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by zf233 on 2021/1/14
 */
@EnableAsync
public class EmailUtil {

    @Resource
    private JavaMailSender javaMailSender;

    private String from;

    public void setFrom(String from) {
        this.from = from;
    }

    @Async(value = "taskScheduler")
    public void send(String to, String subject, String content) throws MessagingException {
        //创建message
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        //发件人
        String nickname = "XCloud";
        helper.setFrom(nickname + '<' + from + '>');
        //收件人
        helper.setTo(to);
        //邮件标题
        helper.setSubject(subject);
        //true指的是html邮件
        helper.setText(content, true);
        //发送邮件
        javaMailSender.send(message);
    }
}
