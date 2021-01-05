# XCloud

### 作者想说

* 我是高校在校生，疫情期间（2020年初-2020年中），自学Java后端课程（白嫖培训机构网课） ，当然掌握精通肯定不现实，有兴趣的可以加入哦！
* 现在说一下XCloud这个项目的开发初衷，因为***疫情网课***的缘故， 大学的老师习惯了用网课软件，导致我上课需要带一个U盘， 有时候机房的电脑还不读U盘，所以用邮件把作业发送到自己的手机上，
  比较繁琐。某度之类的网盘使用门槛又比较高。
* 基于以上这些原因，还有我私人想要再做一个项目练手的原因， 先写了一个***XCloud浏览器***版本，因为不太会前端框架之类的技术，所以比较简陋，但是基本的网盘功能全部实现。 作者水平有限，不足之处还有很多，多包涵！
* 下面开始介绍***XCloud***。

***

## 项目架构

### 前端

* JSP
* JSTL
* EL
* JQuery

### 后台

* SpringBoot
* MyBatis
* MySQL
* Redis:缓存
* Druid:阿里高性能数据库链接池
* FastDFS:文件服务器
* Nginx
* Tomcat
* Maven
* jackson

## 页面展示

<img src="https://www.zf233.cn/static/img/git/xcloud/browse/xcloud_index.png">
<img src="https://www.zf233.cn/static/img/git/xcloud/browse/xcloud_home.png">
<img src="https://www.zf233.cn/static/img/git/xcloud/browse/xcloud_folder.png">
<img src="https://www.zf233.cn/static/img/git/xcloud/browse/xcloud_login.png">
<img src="https://www.zf233.cn/static/img/git/xcloud/browse/xcloud_regist.png">
<img src="https://www.zf233.cn/static/img/git/xcloud/browse/xcloud_error.png">

### 本地开发运行部署

#### Xcloud 项目

* JDK版本: 1.8
* 下载zip直接解压或安装git后执行克隆命令 https://github.com/zhang-sexy/XCloud-Server.git
* 安装中间件 Redis
* 安装文件服务器 FastDFS
* 运行MySql脚本文件 xcloud.sql
* 修改各配置文件相应依赖IP配置(默认本地127.0.0.1)

#### Xcloud Android App 项目

* Xcloud android app项目请移步到本人XCloud仓库 </br>https://github.com/zhang-sexy/XCloud.git

### 写在最后

* 个人学习使用遵循GPL开源协议
* Logo已申请版权，请勿商用
