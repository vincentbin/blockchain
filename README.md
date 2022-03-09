# blockchain
### 注册中心部署
#### Docker Zookeeper部署（例 同样支持nacos）

1. 拉取zk镜像&emsp;&emsp;&emsp;指令：docker pull zookeeper:3.4.14
2. 查看镜像id&emsp;&emsp;&emsp;指令：docker images
3. 拉起容器&emsp;&emsp;&emsp;&emsp;指令：docker run -d -p 2181:2181 --name b-zookeeper --restart always {imageId}
