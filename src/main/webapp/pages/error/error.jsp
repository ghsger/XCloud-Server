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
    <title>XCloud-${sessionScope.errorMsg}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="description" content="XCloud XCloud.show-简约方便的在线存储网站,支持桌面(浏览器)、移动端(APP)随存随取"/>
    <meta name="keywords" content="XCloud,xcloud,网络存储,云存储,在线存储,网盘,云盘,文件服务,后端开发" />
    <%@ include file="../common/head.jsp" %>
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
<center>
    <br><br>
    <h1>${sessionScope.errorMsg}</h1>
    <br>
    <h2>
        <a href="${sessionScope.errorBack}">点此返回</a>
    </h2>
</center>
<div class="foot">
    <span style="color: black">
        <p>V4.5.5&nbsp;Copyright &copy;&nbsp;2021&nbsp;XCloud.</p>
        <p>zf233.cn&nbsp;All Rights Reserved·</p>
        <p><a href="https://beian.miit.gov.cn/" style="text-decoration: none">冀ICP备20013542号</a></p>
    </span>
</div>
</body>
</html>


