<%--
  Created by IntelliJ IDEA.
  User: 
  Date: 2020/6/21
  Time: 11:09 上午
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>XCloud-注册</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="description" content="XCloud XCloud.show-简约方便的在线存储网站,支持桌面(浏览器)、移动端(APP)随存随取"/>
    <meta name="keywords" content="XCloud,xcloud,网络存储,云存储,在线存储,网盘,云盘,文件服务,后端开发"/>
    <%@ include file="../common/head.jsp" %>
    <script type="text/javascript">
        $(function () {
            $("#sub").click(function () {
                var username = $("#username").val().trim();
                var password = $("#password").val().trim();
                var inviteCode = $("#inviteCode").val().trim();
                if (username == "") {
                    alert("用户名不可为空");
                    return false;
                }
                if (password == "") {
                    alert("密码不可为空");
                    return false;
                }
                if (inviteCode == "") {
                    alert("邀请码不可为空");
                    return false;
                }
                if (username.length < 5) {
                    alert("用户名格式有误");
                    return false;
                }
                if (password.length < 5) {
                    alert("密码格式有误");
                    return false;
                }
                if (inviteCode.length < 5) {
                    alert("邀请码格式有误");
                    return false;
                }
            })
        })
    </script>
</head>
<body>
<div class="head">
    <a href="user/browse/index">
        <div class="logo">
            <img class='pic' src='/static/img/logo.png' style="height: 40px;">
        </div>
        <div class="logo02">
            <img class='pic' src='/static/img/logo2.png' style="height: 30px;">
        </div>
    </a>
</div>
<form action="user/browse/regist" class="box" method="post">
    <h1>regist</h1>
    <input type="text" name="username" placeholder="username" id="username" onblur="formValid.checkUserName()"
           onclick="formClick.checkUserName()"/>
    <span class="imsg" id="userInfo">请输入用户名</span>
    <input type="text" placeholder="password" name="password" id="password" onblur="formValid.checkPassword()"
           onclick="formClick.checkPassword()"/>
    <span class="imsg" id="passwordInfo">请输入密码</span>
    <input type="text" placeholder="nickname" name="nickname" id="nickname"/>
    <span class="imsg" id="nicknameInfo">请输入昵称</span>
    <input type="password" name="inviteCode" placeholder="inviteCode" id="inviteCode"
           onblur="formValid.checkInviteCode()" onclick="formClick.checkInviteCode()"/>
    <span class="imsg" id="inviteCodeInfo">请输入邀请码</span>
    <input type="submit" value="regist" id="sub">
</form>
<div id="foot">
    <span style="color: black">
        <p>V4.5.5&nbsp;Copyright &copy;&nbsp;2021&nbsp;XCloud.</p>
        <p>zf233.cn&nbsp;All Rights Reserved·</p>
        <p><a href="https://beian.miit.gov.cn/" style="text-decoration: none">冀ICP备20013542号</a></p>
    </span>
</div>
<script text="text/javascript">

    var username = document.getElementById('username');
    var password = document.getElementById('password');
    var inviteCode = document.getElementById('inviteCode');

    var userInfo = document.getElementById('userInfo');
    var passwordInfo = document.getElementById('passwordInfo');
    var inviteCodeInfo = document.getElementById('inviteCodeInfo');

    var formValid = {
        checkUserName: function () {
            if (username.value.length == 0) {
                userInfo.innerHTML = "用户名不能为空";
                userInfo.style.color = "red";
                return false;
            }
        },
        checkPassword: function () {
            if (password.value.length == 0) {
                passwordInfo.innerHTML = "密码不能为空";
                passwordInfo.style.color = "red";
                return false;
            }
        },
        checkInviteCode: function () {
            if (inviteCode.value.length == 0) {
                inviteCodeInfo.innerHTML = "邀请码不能为空";
                inviteCodeInfo.style.color = "red";
                return false;
            }
        }
    };
    var formClick = {
        checkUserName: function () {
            userInfo.innerHTML = "请输入用户名";
            userInfo.style.color = "";
            return false;
        },
        checkPassword: function () {
            passwordInfo.innerHTML = "请输入密码";
            passwordInfo.style.color = "";
            return false;
        },
        checkInviteCode: function () {
            inviteCodeInfo.innerHTML = "请输入邀请码";
            inviteCodeInfo.style.color = "";
            return false;
        }
    }
</script>
</body>
</html>
