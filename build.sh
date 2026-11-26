#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
STATIC_DIR="$ROOT_DIR/bbh-back/src/main/resources/static"

# 补充常见包管理器路径（macOS Homebrew 等）
for dir in /opt/homebrew/bin /usr/local/bin; do
  if [ -d "$dir" ] && [[ ":$PATH:" != *":$dir:"* ]]; then
    export PATH="$dir:$PATH"
  fi
done

# 自动探测 JDK 21+（若外部未显式指定 JAVA_HOME）
resolve_java_home() {
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    return
  fi
  if [ -x /usr/libexec/java_home ] && /usr/libexec/java_home -v 21+ >/dev/null 2>&1; then
    export JAVA_HOME="$(/usr/libexec/java_home -v 21+)"
    return
  fi
  for candidate in \
    /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home \
    /opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home \
    /usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home \
    /usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home; do
    if [ -x "$candidate/bin/javac" ]; then
      export JAVA_HOME="$candidate"
      return
    fi
  done
}

resolve_java_home
if [ -n "${JAVA_HOME:-}" ]; then
  export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "==> [1/3] 编译前端静态网页..."
cd "$ROOT_DIR/bbh-front"
if [ ! -d "node_modules" ] || [ "package.json" -nt "node_modules" ]; then
  npm install
fi
npm run build

echo "==> [2/3] 同步前端产物至后端 static 目录..."
rm -rf "$STATIC_DIR"
mkdir -p "$STATIC_DIR"
cp -R "$ROOT_DIR/bbh-front/dist/." "$STATIC_DIR/"

echo "==> [3/3] 打包后端可执行 JAR（内嵌前端静态资源）..."
cd "$ROOT_DIR/bbh-back"
chmod +x ./mvnw
./mvnw clean package -DskipTests

JAR_FILE="$ROOT_DIR/bbh-back/target/bbh-back-0.2.jar"
echo ""
echo "==> 构建成功！"
echo "    产物路径: bbh-back/target/bbh-back-0.2.jar"
echo "    启动命令: java -jar \"$JAR_FILE\""
echo "    访问地址: http://localhost:10000 （前端页面） | http://localhost:10000/api/... （后端接口）"
