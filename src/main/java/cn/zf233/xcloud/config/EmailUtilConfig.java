package cn.zf233.xcloud.config;

import cn.zf233.xcloud.util.EmailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zf233 on 2021/1/14
 */
@Configuration
public class EmailUtilConfig {

    @Value("${spring.mail.username}")
    private String fromMail;

    @Bean
    public EmailUtil emailUtil() {
        EmailUtil emailUtil = new EmailUtil();
        emailUtil.setFrom(fromMail);
        return emailUtil;
    }
}
