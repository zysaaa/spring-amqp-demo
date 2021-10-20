
确保启动了 RabbitMQ 服务且module 下的 application.yml 配置了 RabbitMQ 服务的正确的 ip 以及用户名/密码。<br>

1. 使用maven构建项目
```
$ git clone < this project >
$ cd RabbitMQ-SpringBoot-Sample
$ mvn clean install
```
2. 一共有5个模块，分别展示了 dead-letter,delay-queue,publish-confirms&max-length%overflow-reject,rpc(同步/异步-自己实现/异步-使用AsyncTemplate),以及一般简单用法（Topic Exchange）。构建完成之后每个module下的target文件夹下会生成对应的jar包。

- dead-letter：
    ```
    java -jar dead-letter-consumer/target/dead-consumer-1.0-SNAPSHOT.jar
    java -jar dead-letter-producer/target/dead-producer-1.0-SNAPSHOT.jar
    ```
- rpc,delay-queue...
    
   
3. 查看运行时的控制台输出。

**或者：直接运行对应module（每个module都是一个SpringBoot项目），便于调试。**
