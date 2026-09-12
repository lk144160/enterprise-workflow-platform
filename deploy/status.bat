@echo off
chcp 65001 >nul

rem ===================================================
rem  装修业务运营平台 - 后端服务状态检查
rem ===================================================

set PORT=8080

echo ============================================
echo   装修业务运营平台后端服务状态
echo ============================================
echo.

rem 检查端口
netstat -ano | findstr ":%PORT% " | findstr "LISTENING" >nul 2>&1
if %errorlevel% neq 0 (
    echo   状态: [未运行]
    echo   端口 %PORT% 无监听
    echo.
    echo   启动服务请运行 start.bat
    pause
    exit /b 0
)

echo   状态: [运行中]
for /f "tokens=2,3,4,5" %%a in ('netstat -ano ^| findstr ":%PORT% " ^| findstr "LISTENING"') do (
    echo   监听地址: %%a
    echo   PID: %%d
    goto :done
)
:done

echo.

rem 尝试健康检查
echo 正在测试接口连通性...
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:%PORT%/' -TimeoutSec 3 -UseBasicParsing; Write-Host '   接口响应:' $r.StatusCode } catch { Write-Host '   接口无响应（服务可能仍在启动中）' }"

echo.
pause
