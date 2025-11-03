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

# 创建用户（可选）
ARG USER_ID=33
ARG GROUP_ID=100
ARG USERNAME=www-data
RUN addgroup -g $GROUP_ID users \
    && adduser -u $USER_ID -G users -D -h /home/$USERNAME -s /bin/sh $USERNAME

# 切换到非 root 用户（可选）
USER $USERNAME

# 容器启动
CMD ["/app/app"]
