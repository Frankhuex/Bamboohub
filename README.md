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

#### 4. 用导出的镜像部署
##### (1) 在本地导出镜像
用正确的服务名、volume名以及TLS证书打包出镜像后，用export_images.sh导出镜像。
```bash
sh export_images.sh
```

##### (2) 上传镜像至服务器
注意要把镜像文件、docker-compose-image.yml、以及数据库初始脚本init.sql都上传到服务器。

##### (3) 在服务器上导入镜像
将镜像上传至服务器后，用import_images.sh导入镜像。
```bash
sh import_images.sh
```

##### (4) 启动服务
把init.sql放在docker-compose-image.yml相同目录下，然后启动服务。