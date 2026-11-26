#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

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

BACK_PID=""
cleanup() {
  echo ""
  echo "==> 正在停止本地联调服务..."
  if [ -n "$BACK_PID" ] && kill -0 "$BACK_PID" 2>/dev/null; then
    kill "$BACK_PID" 2>/dev/null || true
    wait "$BACK_PID" 2>/dev/null || true
  fi
}
trap cleanup INT TERM EXIT

echo "==> [1/2] 启动后端开发服务 (http://localhost:10000/api) ..."
(
  cd "$ROOT_DIR/bbh-back"
  chmod +x ./mvnw
  exec ./mvnw spring-boot:run -Dspring-boot.run.fork=false
) &
BACK_PID=$!

echo "==> [2/2] 启动前端 Vite 开发服务器 (http://localhost:5173 -> 代理 /api 至 :10000) ..."
cd "$ROOT_DIR/bbh-front"
if [ ! -d "node_modules" ] || [ "package.json" -nt "node_modules" ]; then
  npm install
fi
npm run dev
