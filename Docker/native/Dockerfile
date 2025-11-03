# Dockerfile
FROM python:3.9.22-bookworm

# 工作目录
WORKDIR /app

# 下载 Linux 二进制（根据目标架构选择版本）
ARG BINARY_URL="https://github.com/Jackxwb/My-JMComic-WebUI/releases/latest/download/app_linux_amd64"
RUN wget -O /app/app $BINARY_URL \
    && chmod +x /app/app

# 安装 Python 依赖
RUN pip install --no-cache-dir -i https://mirrors.aliyun.com/pypi/simple \
    jmcomic flask

# 创建非 root 用户（安全方式，不会重复报错）
ARG USER_ID=33
ARG GROUP_ID=100
ARG USERNAME=www-data

RUN set -eux; \
    # 创建组，如果已经存在则忽略
    if ! getent group $GROUP_ID >/dev/null 2>&1; then \
        groupadd -g $GROUP_ID users; \
    fi; \
    # 创建用户，如果已经存在则忽略
    if ! id -u $USER_ID >/dev/null 2>&1; then \
        useradd -u $USER_ID -g $GROUP_ID -m -s /bin/sh $USERNAME; \
    fi
 
# 容器启动命令
CMD ["/app/app"]
