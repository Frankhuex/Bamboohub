# bbh-back/Dockerfile
FROM openjdk:17-jdk
COPY target/bbh-back-0.2.jar app.jar
# 在 Dockerfile 中确保有权限
RUN mkdir -p /sys/fs/cgroup/
VOLUME /sys/fs/cgroup
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
