@echo off
chcp 65001 >nul

rem ===================================================
rem  装修业务运营平台 - 后端一键停止脚本
rem  查找并终止占用 8080 端口的进程
rem ===================================================

set PORT=8080

echo ============================================
echo   装修业务运营平台后端服务停止
echo ============================================
echo   查找端口 %PORT% 占用进程...
echo.

rem 查找监听 8080 端口的进程 PID
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT% " ^| findstr "LISTENING"') do (
    set PID_VAL=%%a
    goto :found
)

echo [提示] 端口 %PORT% 无进程监听，服务可能未运行
echo.
pause
exit /b 0

:found
echo   占用 PID: %PID_VAL%

rem 获取进程名
for /f "tokens=1" %%p in ('tasklist /fi "pid eq %PID_VAL%" /nh 2^>nul') do set PROC_NAME=%%p
echo   进程名: %PROC_NAME%

rem 确认后终止
echo.
set /p CONFIRM=确认终止该进程？(Y/N): 
if /i not "%CONFIRM%"=="Y" (
    echo 已取消
    pause
    exit /b 0
)

taskkill /f /pid %PID_VAL% >nul 2>&1
if %errorlevel% equ 0 (
    echo.
    echo [成功] 进程 %PID_VAL% (%PROC_NAME%) 已终止
) else (
    echo.
    echo [失败] 终止失败，可能需要管理员权限
)

echo.
pause
