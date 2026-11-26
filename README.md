# Bamboohub

轻量化单端口部署版 Bamboohub（Spring Boot 3.4 + Java 21 + MySQL + React 19 / Vite）。
后端直接内嵌托管前端静态网页，同端口提供前端 SPA 访问（`/`）与后端接口服务（`/api/**`）。

---

## 1. 环境要求

- **运行环境**：JDK 21+、MySQL 8.0+
- **编译环境**：Node.js 18+（含 npm）、JDK 21+（Maven Wrapper 已内置于 `bbh-back/mvnw`）

---

## 2. 数据库初始化（新机器首次部署）

项目不再使用自动数据库迁移模块，表结构由独立脚本 [sql/schema.sql](sql/schema.sql) 维护。
拿到新机器后，先执行一次建库建表脚本即可：

```bash
# Ubuntu / Debian（默认 root 使用 auth_socket 认证）：
sudo mysql < sql/schema.sql

# macOS 或使用密码认证的 root：
mysql -u root -p < sql/schema.sql
```

> 默认数据库名：`bamboohub`，默认账号/密码：`bamboohub` / `7777`（定义在 [bbh-back/src/main/resources/application.properties](bbh-back/src/main/resources/application.properties)，也可在启动时通过命令行参数覆盖）。

---

## 3. 一键编译与生产运行

在项目根目录执行编译脚本：

```bash
sh build.sh
```

`build.sh` 会依次完成：
1. 编译前端项目（`bbh-front/dist`）；
2. 将前端静态资源同步至 `bbh-back/src/main/resources/static/`；
3. 调用 Maven 打包生成包含前端网页的单一可执行 JAR：`bbh-back/target/bbh-back-0.2.jar`。

启动服务（仅需拷走该 `.jar` 文件即可运行）：

```bash
java -jar bbh-back/target/bbh-back-0.2.jar
```

如需自定义端口或数据库连接参数：

```bash
java -jar bbh-back/target/bbh-back-0.2.jar \
  --server.port=10000 \
  --spring.datasource.url="jdbc:mysql://localhost:3306/bamboohub?characterEncoding=utf-8&serverTimezone=UTC" \
  --spring.datasource.username=bamboohub \
  --spring.datasource.password=7777
```

启动后访问说明：
- **前端网页**：浏览器直接打开 `http://localhost:10000`
- **后端 API**：前端及同机器其他服务均可直接调用 `http://localhost:10000/api/...`

---

## 4. 本地前后端联调（开发模式）

本地开发时**无需**运行 `build.sh`，前端通过 [bbh-front/vite.config.ts](bbh-front/vite.config.ts) 中的 `server.proxy` 自动将 `/api` 请求转发至 `http://localhost:10000`。

### 方式一：一键同时启动前后端

```bash
sh dev.sh
```

按 `Ctrl + C` 可同时停止前后端进程。

### 方式二：分终端 / IDE 独立启动（便于后端打断点调试）

1. **启动后端**（监听 `:10000`）：在 IDE 中直接运行/调试 `BamboohubApplication`，或执行：
   ```bash
   cd bbh-back && ./mvnw spring-boot:run
   ```
2. **启动前端**（监听 `:5173`，支持热更新 HMR）：
   ```bash
   cd bbh-front && npm run dev
   ```
3. 浏览器访问 `http://localhost:5173` 进行联调。

> 若本地后端临时修改了端口，可在 `bbh-front/.env.local`（已被 Git 忽略）中配置 `VITE_API_BASE_URL=http://localhost:<新端口>/api` 覆盖默认代理地址。
