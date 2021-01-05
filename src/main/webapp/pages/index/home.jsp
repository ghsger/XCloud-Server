<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>XCloud</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="description" content="XCloud XCloud.show-简约方便的在线存储网站,支持桌面(浏览器)、移动端(APP)随存随取"/>
    <meta name="keywords" content="XCloud,xcloud,网络存储,云存储,在线存储,网盘,云盘,文件服务,后端开发"/>
    <%@ include file="../common/head.jsp" %>
    <script type="text/javascript">
        $(function () {
            $("#delete_remark").click(function () {
                $("#remark").val("");
                $("#select_file").val("")
            })
            $("#file_submit").click(function () {
                if ($("#select_file").val().trim() == "") {
                    alert("未选择文件");
                    return false
                }
                var remarkText = $("#remark").val().trim();
                if (remarkText.length > 20) {
                    remarkText = remarkText.substr(0, 20);
                    $("#remark").val(remarkText)
                }
            })
            $(".delete_button").click(function () {
                if (!confirm("确认删除？")) {
                    return false
                }
            })
            $("#file_submit").click(function () {
                if (${sessionScope.currentUser.useCapacity} >= ${sessionScope.currentUser.level * 10})
                {
                    alert("您的存储空间已满，清理空间后继续");
                    $("#remark").val("");
                    $("#select_file").val("")
                    return false
                }
            })
            $("#createFolder").click(function () {
                var foldername = $("#folderName").val().trim();
                if (foldername.length <= 0) {
                    alert("文件夹名不可为空");
                    return false;
                }
            })
            $("#fuzzySelect").blur(function () {
                var matchCode = $("#fuzzySelect").val().trim()
                if (matchCode.length <= 0) {
                    alert("检索关键字为空");
                    return false;
                }
                $("#fuzzySelect_button").trigger("click")
            })
        })
    </script>
    <style>
        body {
            width: 100%;
            height: 100%;
            background-image: linear-gradient(to top right, rgba(224, 73, 13, 0.4), rgba(4, 149, 245, 0.5));
            background-repeat: no-repeat;
            background-size: 100% 100%;
            background-attachment: fixed;
        }

        #upload_div {
            text-align: center;
            background-color: rgb(223, 224, 225);
            background-color: rgba(0, 0, 0, 0.05);
            padding: 5px 10px 5px 10px;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            border-radius: 24px;
        }

        table tbody {
            display: block;
            height: 330px;
            overflow-y: scroll;
        }

        table thead,
        tbody tr {
            display: table;
            width: 100%;
            table-layout: fixed; /*重要  表格固定算法*/
        }

        table tbody tr:hover {
            background-color: rgb(223, 224, 225);
        }

        table thead { /*留出滚动条的位置*/
            width: calc(100% - 1em)
        }

        table thead th {
            background: #ccc;
        }
    </style>
</head>
<body>
<div class="con">
    <div class="head">
        <a href="user/browse/index">
            <div class="logo">
                <img class='pic' src='/static/img/logo.png' style="height: 40px;">
            </div>
            <div class="logo02">
                <img class='pic' src='/static/img/logo2.png' style="height: 30px;">
            </div>
        </a>
        <c:if test="${sessionScope.currentUser == null}">
            <div class="nav02">
                <ul>
                    <div class="login">
                        <li><a href="pages/user/login.jsp">登陆</a></li>
                    </div>
                    <div class="regist">
                        <li><a href="pages/user/regist.jsp">注册</a></li>
                    </div>
                </ul>
            </div>
        </c:if>
        <c:if test="${sessionScope.currentUser != null}">
            <div class="nav02">
                <ul>
                    <div class="user">
                        <li>
                            欢迎&nbsp;
                            <a href="pages/user/userDetails.jsp"
                               style="font-size: 15px">${sessionScope.currentUser.username}</a>
                            &nbsp;Lv<font style="color: #ff7202">${sessionScope.currentUser.level}</font>
                        </li>
                    </div>
                    <div class="regist">
                        <li><a href="user/browse/logout">注销</a></li>
                    </div>
                </ul>
            </div>
        </c:if>
    </div>
    <c:if test="${sessionScope.currentUser != null}">
        <div id="path">
            <span id="path01">
                <a href="user/browse/index?parentid=-1">/</a>
                <c:forEach items="${sessionScope.absolutePath}" var="absolute">
                    <a href="user/browse/index?parentid=${absolute.parentid}">${absolute.folderName}</a>/
                </c:forEach>
            </span>
            <span id="path02">
                <form action="user/browse/index" method="get" style="height: 15px">
                    <input type="text" name="matchCode" id="fuzzySelect" placeholder="检索关键字"
                           style="background: transparent;border:1px solid rgba(0, 5, 20, 0.2)">
                    <input type="submit" id="fuzzySelect_button" style="display: none">
                </form>
            </span>
        </div>
    </c:if>
    <div class="main01">
        <c:if test="${sessionScope.currentUser != null}">
            <hr>
        </c:if>
        <br>
        <c:if test="${sessionScope.currentUser != null}">
            <table align="center" style="min-width:900px">
                <thead style="font-size: 18px; font-style: italic">
                <c:if test="${sessionScope.fileVos.size() > 0}">
                    <tr>
                        <td align="center">
                            序号
                        </td>
                        <td colspan="2" align="center">
                            名称<a href="user/browse/index?sortFlag=0&sortType=0">▲</a><a
                                href="user/browse/index?sortFlag=0&sortType=1">▼</a>
                        </td>
                        <td align="center">
                            类型<a href="user/browse/index?sortFlag=1&sortType=0">▲</a><a
                                href="user/browse/index?sortFlag=1&sortType=1">▼</a>
                        </td>
                        <td align="center">
                            大小<a href="user/browse/index?sortFlag=2&sortType=0">▲</a><a
                                href="user/browse/index?sortFlag=2&sortType=1">▼</a>
                        </td>
                        <td colspan="2" align="center">
                            上传时间<a href="user/browse/index?sortFlag=3&sortType=0">▲</a><a
                                href="user/browse/index?sortFlag=3&sortType=1">▼</a>
                        </td>
                        <td align="center">
                            下载<a href="user/browse/index?sortFlag=4&sortType=0">▲</a><a
                                href="user/browse/index?sortFlag=4&sortType=1">▼</a>
                        </td>
                        <td colspan="2" align="center">
                            备注
                        </td>
                        <td align="center">
                            操作
                        </td>
                    </tr>
                </c:if>
                </thead>
                <tbody style="font-size: 15px">
                <c:if test="${sessionScope.fileVos.size() == 0 && sessionScope.fileNullType == null}">
                    <h2>空空如也，快去上传吧！</h2>
                </c:if>
                <c:if test="${'fileNullType'.equals(sessionScope.fileNullType)}">
                    <h2>未检索到指定文件！</h2>
                </c:if>
                <c:forEach items="${sessionScope.fileVos}" var="fileVo" varStatus="index">
                    <c:if test="${fileVo.folder == 0}">
                        <tr>
                            <td style="text-align: center;">
                                    ${index.index + 1}
                            </td>
                            <td colspan="2"
                                style="text-align: left; white-space: nowrap;text-overflow: ellipsis;overflow: hidden;">
                                    ${fileVo.fileName}
                            </td>
                            <td style="text-align: center;">
                                    ${fileVo.fileType}
                            </td>
                            <td style="text-align: center;">
                                    ${fileVo.fileSize}
                            </td>
                            <td colspan="2" style="text-align: center;">
                                    ${fileVo.uploadTime}
                            </td>
                            <td style="text-align: center;">
                                    ${fileVo.downloadCount}
                            </td>
                            <td colspan="2" style="text-align: center;">
                                    ${fileVo.remark}
                            </td>
                            <td align="center">
                                <a class="delete_button" href="file/browse/delete?fileid=${fileVo.id}">
                                    <input type="button" value="删除"
                                           style="color: red;background: transparent; border: none;"/>
                                </a>
                                &nbsp;
                                <a href="file/browse/download?fileid=${fileVo.id}">
                                    <input type="button" value="下载" class="download_buttons"
                                           style="background: transparent; border: none;"/>
                                </a>
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${fileVo.folder == 1}">
                        <tr>
                            <td style="text-align: center;">
                                    ${index.index + 1}
                            </td>
                            <td colspan="2"
                                style="text-align: left; white-space: nowrap;text-overflow: ellipsis;overflow: hidden;">
                                    ${fileVo.fileName}
                            </td>
                            <td style="text-align: center;">
                                folder
                            </td>
                            <td style="text-align: center;">
                            </td>
                            <td colspan="2" style="text-align: center;">
                                    ${fileVo.uploadTime}
                            </td>
                            <td style="text-align: center;">
                            </td>
                            <td colspan="2" style="text-align: center;">
                                    ${fileVo.remark}
                            </td>
                            <td align="center">
                                <a class="delete_button" href="file/browse/delete?fileid=${fileVo.id}">
                                    <input type="button" value="删除"
                                           style="color: red; background: transparent; border: none;"/>
                                </a>
                                &nbsp;
                                <a href="user/browse/index?parentid=${fileVo.id}">
                                    <input type="button" value="打开" class="download_buttons"
                                           style="background: transparent; border: none;"/>
                                </a>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${sessionScope.currentUser == null}">
            <h2><a href="pages/user/regist.jsp">无账号？点此注册</a></h2>
        </c:if>
    </div>
    <c:if test="${sessionScope.currentUser != null}">
        <hr>
    </c:if>
    <div class="main02">
        <center>
            <c:if test="${sessionScope.currentUser != null}">
                <div id="upload_div">
                    <form action="file/browse/upload" method="post" enctype="multipart/form-data">
                        <font style="font-size: 12px">限制上传文件大小(15MB)</font>
                        <input name="myFile" id="select_file" type="file"
                               style="width: 250px; background: transparent; border: none;"/>
                        <input type="text" name="remark" id="remark"
                               style="width: 200px; background: transparent; border:1px solid rgba(0, 5, 20, 0.2)"
                               placeholder="备注(长度20,超出截断)"/>
                        <input type="button" id="delete_remark" value="清空"
                               style="color: red; background: transparent; border: none;"/>
                        <input type="hidden" name="parentid" value="${sessionScope.parentid}">
                        <input type="submit" value="确认上传" id="file_submit"
                               style="background: transparent; border: none;">
                    </form>
                    <form action="file/browse/createfolder" method="get">
                        <font style="font-size: 12px">新建文件夹</font>
                        <input type="hidden" name="parentid" value="${sessionScope.parentid}">
                        <input id="folderName" type="text" placeholder="文件夹名" name="foldername"
                               style="background: transparent;border:1px solid rgba(0, 5, 20, 0.2)"/>
                        <input id="createFolder" type="submit" value="创建"
                               style="background: transparent; border: none;">
                    </form>
                    <font style="font-size: 15px;">
                        <strong>用户: ${sessionScope.currentUser.username}</strong>&nbsp;
                        <strong>容量</strong>
                        <progress value="${sessionScope.currentUser.useCapacity}"
                                  max="${sessionScope.currentUser.level * 10}"></progress>
                        <font style="font-size: 10px">
                                ${sessionScope.currentUser.useCapacity}/${sessionScope.currentUser.level * 10}
                        </font>
                    </font>
                </div>
            </c:if>
        </center>
    </div>
    <br>
    <div class="foot">
        <span style="color: black">
			<p>V4.5.5&nbsp;Copyright &copy;&nbsp;2021&nbsp;XCloud.</p>
            <p>zf233.cn&nbsp;All Rights Reserved·</p>
            <p><a href="https://beian.miit.gov.cn/" style="text-decoration: none">冀ICP备20013542号</a></p>
        </span>
    </div>
</div>
</body>
</html>