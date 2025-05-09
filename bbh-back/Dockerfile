FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 直接复制（默认使用 root 用户）
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 直接运行（无安全隔离）
ENTRYPOINT ["java", "-jar", "/app/app.jar"]