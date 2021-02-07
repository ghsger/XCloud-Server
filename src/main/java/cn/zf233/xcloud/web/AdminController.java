package cn.zf233.xcloud.web;

import cn.zf233.xcloud.commom.Const;
import cn.zf233.xcloud.commom.ServerResponse;
import cn.zf233.xcloud.entity.User;
import cn.zf233.xcloud.exception.LoginException;
import cn.zf233.xcloud.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Created by zf233 on 2021/1/17
 */
@Controller
public class AdminController {

    @Resource
    private AdminService adminService;

    // 后台主页
    @RequestMapping("/admin")
    public String home(HttpSession session) {
        Object currentAdmin = session.getAttribute(Const.CURRENT_ADMIN);
        if (currentAdmin != null) {
            ServerResponse allUserInfo = adminService.getAllUserInfo();
            session.setAttribute("adminVos", allUserInfo.getData());
            return "admin";
        }

        return "admin_login";
    }

    // 移除用户
    @RequestMapping("/admin/remove")
    public String remove(User user) {
        adminService.removeUser(user);
        return "redirect:/admin";
    }

    // 设置用户权限（锁定/解锁）
    @RequestMapping("/admin/role")
    public String setRole(User user) {
        adminService.updateUserRole(user);
        return "redirect:/admin";
    }

    // 管理员登陆
    @RequestMapping("/admin/login")
    public String login(User user, HttpSession session) {
        ServerResponse response = adminService.adminLogin(user);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_ADMIN, response.getData());
            return "redirect:/admin";
        }

        throw new LoginException(response.getMsg());
    }

    // 管理员登出
    @RequestMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }
}
