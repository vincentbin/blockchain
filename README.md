# blockchain
## 简介
- 实现了比特币block，区块链，钱包，交易的基本业务功能。
- 通过netty搭配注册中心为namespace搭建全连接的p2p网络。
- 通过http restful接口接收客户请求，并将有需要的请求发布到p2p网络进行计算。
- 在netty read接收端针对不同消息类型完成不同的业务功能。

## 部署 & 启动
#### Docker Zookeeper部署
1. 拉取zk镜像&emsp;&emsp;&emsp;指令：docker pull zookeeper:3.4.14
2. 查看镜像id&emsp;&emsp;&emsp;指令：docker images
3. 拉起容器&emsp;&emsp;&emsp;&emsp;指令：docker run -d -p 2181:2181 --name b-zookeeper --restart always {imageId}

#### 服务启动
- BlockChainApplication.java为启动类，properties文件可修改启动端口
- netty服务通过spring ApplicationContextAware的setApplicationContext带起，在此类可以设置netty启动地址以及注册中心地址。
#### 配置
- application.properties
```properties
# springboot 启动端口
server.port=8080
# 提供服务的机器ip & port
server.netty.local.address=127.0.0.1:8000
# 注册中心地址
server.netty.registry.address=127.0.0.1:2181
```
