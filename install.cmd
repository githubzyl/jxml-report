@echo off
set "CURRENT_DIR=%~dp0%"
%CURRENT_DIR:~0,2%
cd "%CURRENT_DIR%"
call mvn install -DskipTests
pause
