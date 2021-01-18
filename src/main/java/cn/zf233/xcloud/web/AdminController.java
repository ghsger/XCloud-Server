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
    @RequestMapping("/admin/home")
    public String home(HttpSession session) {
        ServerResponse allUserInfo = adminService.getAllUserInfo();
        session.setAttribute("adminVos", allUserInfo.getData());
        return "admin";
    }

    // 移除用户
    @RequestMapping("/admin/remove")
    public String remove(User user) {
        adminService.removeUser(user);
        return "redirect:/admin/home";
    }

    // 设置用户权限（锁定/解锁）
    @RequestMapping("/admin/role")
    public String setRole(User user) {
        adminService.updateUserRole(user);
        return "redirect:/admin/home";
    }

    // 管理员登陆
    @RequestMapping("/admin/login")
    public String login(HttpSession session, User user) {
        ServerResponse response = adminService.adminLogin(user);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_ADMIN_USER, response.getData());
            return "redirect:/admin/home";
        }
        throw new LoginException(response.getMsg());
    }

    // 管理员登陆
    @RequestMapping("/admin")
    public String login() {
        return "admin_login";
    }

    // 管理员登出
    @RequestMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "admin_login";
    }
}
