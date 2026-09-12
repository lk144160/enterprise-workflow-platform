@echo off
chcp 65001 >nul
title frp 客户端 - 装修业务运营平台

rem ===================================================
rem  frp 客户端启动脚本示例
rem ===================================================

set FRP_DIR=%~dp0

echo ============================================
echo   frp 客户端启动
echo ============================================
echo   配置文件: %FRP_DIR%frpc.toml
echo   本地端口: 80 (Nginx)
echo ============================================
echo.

rem 检查 frpc.exe 是否存在
if not exist "%FRP_DIR%frpc.exe" (
    echo [错误] 未找到 frpc.exe
    echo.
    echo 请下载 frp Windows 版本:
    echo   https://github.com/fatedier/frp/releases
    echo   下载 frp_x.xx.x_windows_amd64.zip
    echo   将 frpc.exe 复制到此目录
    pause
    exit /b 1
)

echo 正在连接 frp 服务器...
echo 关闭此窗口将断开隧道
echo.

frpc.exe -c frpc.toml

pause
