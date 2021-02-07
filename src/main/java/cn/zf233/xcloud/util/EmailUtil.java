package cn.zf233.xcloud.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zf233 on 2021/1/14
 */
public class EmailUtil {

    @Resource
    private JavaMailSender javaMailSender;

    private final String from;

    private final Lock lock = new ReentrantLock();

    public EmailUtil(String from) {
        this.from = from;
    }

    // 发送复杂html邮件
    @Async("taskExecutor")
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

        try {
            lock.lock();
            //发送邮件
            javaMailSender.send(message);
        } finally {
            lock.unlock();
        }
    }

    // 发送简单文本邮件
    @Async("taskExecutor")
    public void sendSimpleEmail(String to, String subject, String content) {

        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();

        //邮件发送人
        message.setFrom(from);
        //邮件接收人
        message.setTo(to);
        //邮件主题
        message.setSubject(subject);
        //邮件内容
        message.setText(content);

        try {
            lock.lock();
            //发送邮件
            javaMailSender.send(message);
        } finally {
            lock.unlock();
        }
    }
}
