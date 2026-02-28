@echo off
cd /d "%~dp0"
call gradlew.bat compileJava compileClientJava --no-daemon
pause
