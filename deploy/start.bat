@echo off
chcp 65001 >nul
title 装修业务运营平台后端服务

rem ===================================================
rem  装修业务运营平台 - 后端一键启动脚本
rem  双击运行即可，关闭窗口自动停止服务
rem ===================================================

rem ---- 路径配置（java 必须已加入 PATH）----
set DEPLOY_DIR=%~dp0
set JAR_NAME=ruoyi-admin.jar
set JAR_PATH=%DEPLOY_DIR%%JAR_NAME%

rem ---- JVM 参数 ----
set JVM_OPTS=-Xms512m -Xmx2048m -Dfile.encoding=UTF-8

rem ---- Spring 参数（覆盖开发环境路径）----
rem 上传目录：部署目录下的 uploadPath
set SPRING_OPTS=^
 --spring.profiles.active=druid,prod ^
 --ruoyi.profile=%DEPLOY_DIR%uploadPath ^
 --ruoyi.addressEnabled=false ^
 --logging.file.path=%DEPLOY_DIR%logs ^
 --server.port=8080

echo ============================================
echo   装修业务运营平台后端服务启动
echo ============================================
echo   部署目录: %DEPLOY_DIR%
echo   JAR 文件: %JAR_PATH%
echo   服务端口: 8080
echo   Java 命令: java
echo   上传目录: %DEPLOY_DIR%uploadPath
echo   日志目录: %DEPLOY_DIR%logs
echo ============================================
echo.

rem 创建必要目录
if not exist "%DEPLOY_DIR%uploadPath" mkdir "%DEPLOY_DIR%uploadPath"
if not exist "%DEPLOY_DIR%logs" mkdir "%DEPLOY_DIR%logs"

rem 检查 JAR 是否存在
if not exist "%JAR_PATH%" (
    echo [错误] 未找到 JAR 文件: %JAR_PATH%
    echo 请先将打包好的 %JAR_NAME% 复制到此目录
    pause
    exit /b 1
)

rem 检查端口是否被占用
netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [警告] 端口 8080 已被占用，请先运行 stop.bat 停止旧服务
    pause
    exit /b 1
)

echo 正在启动后端服务...
echo.

rem 启动后端（前台运行，关闭窗口即停止）
java %JVM_OPTS% -jar "%JAR_PATH%" %SPRING_OPTS%
