@echo off
title NinjaMS Launcher: inactive
color 1b
echo NinjaMS Launcher
echo.    
echo.
echo Ready? If so, press any key...
echo.
echo.
echo.
pause >nul
cls
color 4c
title NinjaMS Launcher: activation stared.
start /b world.bat
title NinjaMS Launcher: World Launched
@ping 127.0.0.1 -n 2 -w 1000 > nul
@ping 127.0.0.1 -n %1% -w 1000> nul
start login.bat >nul
title NinjaMS Launcher: Login Interface Launched
@ping 127.0.0.1 -n 2 -w 2500 > nul
@ping 127.0.0.1 -n %1% -w 2500> nul
start channel.bat >nul
title NinjaMS Launcher: Channel Interface Launched 
COLOR 2a
title NinjaMS Launcher: Server Running.
