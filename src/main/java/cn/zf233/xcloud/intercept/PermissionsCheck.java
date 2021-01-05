package cn.zf233.xcloud.intercept;

import cn.zf233.xcloud.commom.Const;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zf233 on 2020/11/4
 */
public class PermissionsCheck implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return true;
        }
        request.getRequestDispatcher("/pages/user/login.jsp").forward(request, response);
        return false;
    }
}
