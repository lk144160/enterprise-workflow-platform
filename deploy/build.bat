@echo off
chcp 65001 >nul

rem ===================================================
rem  装修业务运营平台 - 一键打包部署脚本
rem  在开发机上运行：打包前后端产物并复制到 deploy 目录
rem ===================================================

set PROJECT_DIR=%~dp0..
set DEPLOY_DIR=%~dp0
set MVN_CMD=mvn
set NPM_DIR=%PROJECT_DIR%\web-ui

echo ============================================
echo   装修业务运营平台 - 打包部署
echo ============================================
echo   项目目录: %PROJECT_DIR%
echo   部署目录: %DEPLOY_DIR%
echo ============================================
echo.

rem ---- 1. 打包后端 ----
echo [1/3] 正在打包后端 JAR...
call %MVN_CMD% -q clean package -DskipTests -f "%PROJECT_DIR%\pom.xml"
if %errorlevel% neq 0 (
    echo [错误] 后端打包失败
    pause
    exit /b 1
)
copy /Y "%PROJECT_DIR%\ruoyi-admin\target\ruoyi-admin.jar" "%DEPLOY_DIR%ruoyi-admin.jar" >nul
echo   √ 后端 JAR 已复制到 deploy 目录
echo.

rem ---- 2. 打包前端 ----
echo [2/3] 正在打包前端...
cd /d "%NPM_DIR%"
call npm run build:prod
if %errorlevel% neq 0 (
    echo [错误] 前端打包失败
    pause
    exit /b 1
)
echo   √ 前端构建完成
echo.

rem ---- 3. 复制前端产物 ----
echo [3/3] 复制前端产物到 deploy 目录...
if exist "%DEPLOY_DIR%dist" rmdir /s /q "%DEPLOY_DIR%dist"
xcopy /e /i /q "%NPM_DIR%\dist" "%DEPLOY_DIR%dist" >nul
echo   √ 前端文件已复制到 deploy\dist
echo.

echo ============================================
echo   打包完成！
echo ============================================
echo.
echo   后端 JAR:   %DEPLOY_DIR%ruoyi-admin.jar
echo   前端文件:   %DEPLOY_DIR%dist\
echo.
echo   部署步骤：
echo   1. 将整个 deploy 目录复制到目标服务器/电脑
echo   2. 确保目标机器已安装 Java 17 和 MySQL
echo   3. 修改 start.bat 中的 JAVA_HOME 和数据库连接
echo   4. 运行 start.bat 启动后端
echo   5. 配置 Nginx 指向 dist 目录
echo.
pause
