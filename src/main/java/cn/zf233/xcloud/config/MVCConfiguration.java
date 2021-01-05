package cn.zf233.xcloud.config;

import cn.zf233.xcloud.intercept.PermissionsCheck;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by zf233 on 2020/11/4
 */
@Configuration
public class MVCConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("*****");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPathPatterns = {
                "/file/browse/**"
        };
        String[] excludePathPatterns = {
                "/user/browse/**"
        };
        registry.addInterceptor(new PermissionsCheck()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }
}
