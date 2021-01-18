# XCloud

XCloud : 适合初学者，喜欢的小伙伴可以点上边的Star支持一下哦
***
[XCloud体验地址](https://zf233.cn)
#### 作者不太会前端，只简单写了一下满足测试需要，比较丑陋，但是基本功能都有。

### 项目主要功能
  * 用户邮箱注册，邮箱链接验证以及登陆
    * 首次注册发送验证链接，180秒有效时间，可再次获取
    * 用户当天未验证链接，系统自动移除此用户信息并发送邮件提示
  * 引入QQ第三方登陆，给用户提供便捷的登陆通道
  * 文件的批量上传、下载、删除
  * 文件夹的新建、删除
  * 支持二维码文件分享
  * 管理员后台，用户的锁定与解锁，移除违规用户
    * 使用邮箱注册的用户被移除后，系统会发送邮件提示

### 项目亮点
  * 提供了安卓App访问接口，并且已经写出一版***安卓App***，可在本人另一个仓库获取
  * 上传文件至部署后台的服务器会通过阿里内网转存至OSS
  * 下载文件不会通过后台服务器，直接访问OSS下载链接
  * 后台服务器只负责文件、文件夹和用户的统一管理
  * 数据库文件表同时负责文件、文件夹存储
  * 复杂邮件发送（HTML）模板
  * 区别其他作者的网盘项目，没有任何花里胡哨的功能，页面简陋，但快速，欢迎来战
    * 速度测试（300兆宽带）
      * 上传：104MB压缩包，用时***1分35秒***
      * 下载：104MB压缩包，用时***5秒***
      * 仅限测试，带宽的不同使得上传、下载速度的不同

### 所用技术

#### 前端

* HTML、CSS、JavaScript、JQuery

#### 后台

* SpringBoot
* MyBatis
* MySQL
* Redis:缓存
* Druid:阿里高性能数据库链接池
* 阿里OSS:文件服务器 
* ThymeLeaf:模板引擎
* Nginx:反向代理
* Tomcat
* Maven
* jackson
* 二维码工具类

### 页面展示
#### 主要界面
![](https://www.zf233.cn/static/img/git/xcloud/browse/share.png)
![](https://www.zf233.cn/static/img/git/xcloud/browse/login_01.png)
#### 邮箱界面
![](https://www.zf233.cn/static/img/git/xcloud/browse/email02.png)



### 本地开发运行部署

#### Xcloud 项目

* JDK版本: 1.8
* 下载zip直接解压或安装git后执行克隆命令 https://github.com/zhang-sexy/XCloud-Server.git
* 安装中间件 Redis
* 更改OSS配置信息
* 更改邮箱配置信息
* 运行MySql脚本文件 xcloud.sql
* 修改各配置文件相应依赖IP配置(默认本地127.0.0.1)

#### Xcloud Android App 项目

* Xcloud Android App项目请移步到本人XCloud仓库 </br>https://github.com/zhang-sexy/XCloud.git

### 写在最后
由于技术有限，难免会有一些设计不合理之处。
* 有想引入QQ登陆和邮箱功能的，等我写一篇详细的博文
* Logo已申请版权，请勿商用
