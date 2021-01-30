package cn.zf233.xcloud.config;

import cn.zf233.xcloud.intercept.PermissionCheckAdmin;
import cn.zf233.xcloud.intercept.PermissionCheck;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by zf233 on 2020/11/4
 */
@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:https://www.xcloud.show/xcloud/user/browse/home");
//        registry.addViewController("/").setViewName("redirect:/user/browse/home");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 用户拦截
        String[] addPathPatterns = {
                "/file/browse/**",
                "/user/browse/update",
                "/user/browse/logout",
                "/user/browse/home"
        };
        String[] excludePathPatterns = {
                "/user/browse/login",
                "/user/browse/regist",
                "/user/browse/check",
                "/user/browse/again",
                "/account/**"
        };
        registry.addInterceptor(new PermissionCheck()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);

        // 管理员拦截
        String[] addPathPatternsAdmin = {
                "/admin/remove",
                "/admin/role",
                "/admin/logout"
        };
        String[] excludePathPatternsAdmin = {
                "/admin",
                "/admin/login"
        };

        registry.addInterceptor(new PermissionCheckAdmin()).addPathPatterns(addPathPatternsAdmin).excludePathPatterns(excludePathPatternsAdmin);
    }
}