@echo off
chcp 65001 >nul
title frp 服务端 - 装修业务运营平台

rem ===================================================
rem  frp 服务端启动脚本示例
rem ===================================================

set FRP_DIR=%~dp0

echo ============================================
echo   frp 服务端启动
echo ============================================
echo   配置文件: %FRP_DIR%frps.toml
echo   通信端口: 7000
echo   公网端口: 80
echo   仪表盘:   http://127.0.0.1:7500
echo ============================================
echo.

rem 检查 frps.exe 是否存在
if not exist "%FRP_DIR%frps.exe" (
    echo [错误] 未找到 frps.exe
    echo.
    echo 请下载 frp Windows 版本:
    echo   https://github.com/fatedier/frp/releases
    echo   下载 frp_x.xx.x_windows_amd64.zip
    echo   将 frps.exe 复制到此目录
    pause
    exit /b 1
)

echo 正在启动 frp 服务端...
echo 关闭此窗口将停止服务
echo.

frps.exe -c frps.toml

pause
