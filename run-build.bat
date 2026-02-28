@echo off
REM Run from any directory. Uses the directory where this batch file lives.
cd /d "%~dp0"
echo Building Alex's Mobs 1.21.1 (Citadel from includeBuild)...
call gradlew.bat compileJava compileClientJava --no-daemon
echo.
pause
