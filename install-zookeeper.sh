#!/usr/bin/env bash

# 测试环境：CentOS 7.4

# 工作目录
WORKING_DIR=/tmp

# 软件名称
SOFTWARE_NAME=zookeeper

# 软件版本
SOFTWARE_VERSION=3.4.5

# 源码包名称
ARCHIVE_NAME="${SOFTWARE_NAME}-${SOFTWARE_VERSION}.tar.gz"

# 源码包下载地址
ARCHIVE_DOWNLOAD_URL="https://archive.apache.org/dist/zookeeper/${SOFTWARE_NAME}-${SOFTWARE_VERSION}/${ARCHIVE_NAME}"

# 源码包解压后目录名称
SOURCE_DIR_NAME="${SOFTWARE_NAME}-${SOFTWARE_VERSION}"

# 源码包保存路径
ARCHIVE_SAVE_PATH="${WORKING_DIR}/${ARCHIVE_NAME}"

# 源码所在目录
SOURCE_DIR="${WORKING_DIR}/${SOURCE_DIR_NAME}"

# 安装目录的根目录
INSTALL_ROOT=/usr/local/${SOFTWARE_NAME}

# 安装目录
INSTALL_DIR="${INSTALL_ROOT}/${SOFTWARE_NAME}-${SOFTWARE_VERSION}"

# 当前使用版本的符号链接
CURRENT_VERSION="${INSTALL_ROOT}/current"

# 二进制文件路径的配置文件
SOFTWARE_PROFILE="/etc/profile.d/${SOFTWARE_NAME}.sh"

# 配置二进制文件路径
function config_binary_path() {
    echo "export PATH=\${PATH}:${CURRENT_VERSION}/bin" > $SOFTWARE_PROFILE
}

# 进入工作目录
cd $WORKING_DIR

# 下载源码包
if [ ! -e "$ARCHIVE_SAVE_PATH" ]; then
    wget -O $ARCHIVE_SAVE_PATH $ARCHIVE_DOWNLOAD_URL
fi

# 下载失败，不再继续。
if [ ! -e "$ARCHIVE_SAVE_PATH" ]; then
    echo "[ERROR] Download ${ARCHIVE_NAME} failed."
    exit 1
fi

# 备份旧的源码目录
if [ -d "$SOURCE_DIR" ]; then
    mv $SOURCE_DIR "${SOURCE_DIR}-$(date +%Y%m%d%H%M%S)"
fi

# 备份旧的安装目录
if [ -d "$INSTALL_DIR" ]; then
    mv $INSTALL_DIR "${INSTALL_DIR}-$(date +%Y%m%d%H%M%S)"
fi

# 解压源码包
tar zxvf $ARCHIVE_SAVE_PATH

# 创建安装根目录
mkdir -p $INSTALL_ROOT

# 拷贝源码目录到安装根目录
cd $SOURCE_DIR $INSTALL_DIR

# 创建符号链接
if [ -L "$CURRENT_VERSION" ]; then
    rm -f $CURRENT_VERSION
fi

ln -s $INSTALL_DIR $CURRENT_VERSION

# 配置二进制文件路径
config_binary_path

echo "################################################################################"
echo "# Open a new terminal or enter: source ${SOFTWARE_PROFILE}"
echo "################################################################################"
