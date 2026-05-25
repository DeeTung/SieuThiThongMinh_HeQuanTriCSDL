@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM ==========================================================
REM Export schema SYSTEM để copy dữ liệu Java sang HQT_DEMO
REM Oracle: 10.0.212.105:1521/ORCLPDB
REM Source schema: SYSTEM
REM Output folder: thư mục backup hiện tại
REM ==========================================================

set ORACLE_HOST=10.0.212.105
set ORACLE_PORT=1521
set ORACLE_SERVICE=ORCLPDB

set ADMIN_USER=SYSTEM
set ADMIN_PASS=Admin123

set SOURCE_SCHEMA=APP_DEMO
set DIRECTORY_NAME=DATA_PUMP_DIR_SMART

set BACKUP_DIR=%~dp0

for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value ^| find "="') do set DTS=%%I

set YYYY=%DTS:~0,4%
set MM=%DTS:~4,2%
set DD=%DTS:~6,2%
set HH=%DTS:~8,2%
set MI=%DTS:~10,2%
set SS=%DTS:~12,2%

set DUMP_FILE=SMART_SUPERMARKET_APP_DEMO_%YYYY%%MM%%DD%_%HH%%MI%%SS%.DMP
set LOG_FILE=SMART_SUPERMARKET_APP_DEMO_%YYYY%%MM%%DD%_%HH%%MI%%SS%_EXPORT.LOG

echo ==========================================================
echo Export schema SYSTEM for HQT_DEMO
echo ==========================================================
echo Host       : %ORACLE_HOST%
echo Port       : %ORACLE_PORT%
echo Service    : %ORACLE_SERVICE%
echo Source     : %SOURCE_SCHEMA%
echo Backup dir : %BACKUP_DIR%
echo Dump file  : %DUMP_FILE%
echo Log file   : %LOG_FILE%
echo ==========================================================

echo.
echo [STEP 1] Creating Oracle DIRECTORY object...

(
echo WHENEVER SQLERROR EXIT SQL.SQLCODE;
echo CREATE OR REPLACE DIRECTORY %DIRECTORY_NAME% AS '%BACKUP_DIR%';
echo EXIT;
) > "%TEMP%\create_data_pump_dir.sql"

sqlplus -S %ADMIN_USER%/%ADMIN_PASS%@//%ORACLE_HOST%:%ORACLE_PORT%/%ORACLE_SERVICE% @"%TEMP%\create_data_pump_dir.sql"

if errorlevel 1 (
    echo [ERROR] Cannot create DIRECTORY object.
    del "%TEMP%\create_data_pump_dir.sql" >nul 2>&1
    pause
    exit /b 1
)

del "%TEMP%\create_data_pump_dir.sql" >nul 2>&1

echo.
echo [STEP 2] Exporting schema %SOURCE_SCHEMA%...

expdp %ADMIN_USER%/%ADMIN_PASS%@//%ORACLE_HOST%:%ORACLE_PORT%/%ORACLE_SERVICE% ^
    SCHEMAS=%SOURCE_SCHEMA% ^
    DIRECTORY=%DIRECTORY_NAME% ^
    DUMPFILE=%DUMP_FILE% ^
    LOGFILE=%LOG_FILE% ^
    REUSE_DUMPFILES=Y

if errorlevel 1 (
    echo.
    echo [ERROR] Export failed. Check log file: %LOG_FILE%
    pause
    exit /b 1
)

echo.
echo ==========================================================
echo [OK] Export completed successfully.
echo Dump file: %BACKUP_DIR%%DUMP_FILE%
echo Log file : %BACKUP_DIR%%LOG_FILE%
echo ==========================================================

pause