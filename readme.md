# 文件传输系统

## 依赖环境

os: Windows10
语言: Java
IDE： Eclipse
数据库：MySql 8.0.19

## 部署步骤

启动命令：

 - 创建数据库

 `mysql -h localhost -u root -p password < CREATE.sql`

 - 启动服务端

 `java -jar FileServerSSL.jar root password`

 *传入的参数为数据库的用户名和密码。*

 - 启动客户端

 `java -jar ClientMainWindow.jar`

## 目录结构说明
```
│  CREATE.sql
│  readme.md
│  安全文件传输实验报告-王明慧.pdf
│  运行视频.mp4
│
└─FileTransfer
    │  cookie.txt
    │  FileClientSSL.jar  // 可执行文件
    │  FileServerSSL.jar  // 可执行文件
    │
    ├─.settings
    │      org.eclipse.core.resources.prefs
    │
    ├─bin
    │
    ├─certs // 私钥、证书和信任库
    │
    ├─ClientStorage  // 客户端的虚拟目录，默认打开文件夹
    │
    ├─lib  // 引入jar包
    │
    ├─log
    │      log.txt  // 文件操作的日志记录
    │
    ├─ServerStorage  // 服务器虚拟目录
    │
    └─src
        ├─base  // 数据格式的规定
        │      Command.java
        │      FileInfo.java
        │      LogInfo.java
        │      Request.java
        │
        ├─client
        │      ClientMainWindow.java  // 界面程序
        │      ProtocolClient.java  // 与服务器的交互
        │      ProtocolClientSSL.java  // // 与服务器的加密交互
        │
        ├─server
        │      FileServer.java  // 分析传输协议时的主程序
        │      FileServerSSL.java  // 主程序
        │      ProtocolServer.java  // 处理客户端的请求
        │      ProtocolServerSSL.java
        │      TaskThread.java  // 接收到客户端的通信请求后新建线程处理该通信，继承Runnable，调用ProtocolServer
        │      TaskThreadSSL.java  //
        │
        └─Utils  // 实现各种功能的工具类
                Base64Utils.java
                LogUtils.java  // 定时器，将队列中的文件日志记录写入文件中
                TimeUtils.java
                UUIDUtils.java
```