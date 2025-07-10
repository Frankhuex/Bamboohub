# 用Docker部署

#### 1. 若已有mysql卷
##### (1) 在docker-compose.yml中调整卷名为已存在的卷
##### (2) 在bbh-back/src/main/resources/application-prod.properties调整基线版本：
```java
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=2
```

#### 2. 全部编译打包
为了方便，我们先在本机编译前端的html静态文件、后端的jar文件，再用docker-compose部署。
方法：在项目根目录运行build.sh
Docker服务名称可在build.sh最后一句修改。
```bash
sh build.sh
```

#### 3. 部分重新编译打包
按需运行（可修改Docker服务名）:
rebuild_back.sh, rebuild_front.sh, rebuild_mysql.sh