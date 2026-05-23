@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM Smart Supermarket - Import SYSTEM dump to HQT_DEMO
REM ==========================================================

set DB_HOST=10.0.249.155
set DB_PORT=1521
set DB_SERVICE=ORCLPDB

set DB_ADMIN_USER=system
set DB_ADMIN_PASSWORD=Admin123

set SOURCE_SCHEMA=APP_DEMO
set TARGET_SCHEMA=HQT_DEMO

set BACKUP_DIR=%~dp0
set ORACLE_DIR_NAME=DATA_PUMP_DIR_SMART

echo.
echo ==========================================================
echo Smart Supermarket - Import Java schema to HQT_DEMO
echo ==========================================================
echo Host          : %DB_HOST%
echo Port          : %DB_PORT%
echo Service       : %DB_SERVICE%
echo Source schema : %SOURCE_SCHEMA%
echo Target schema : %TARGET_SCHEMA%
echo Backup dir    : %BACKUP_DIR%
echo ==========================================================
echo.

echo Available .DMP files:
echo ----------------------------------------------------------
dir /b "%BACKUP_DIR%*.DMP"
echo ----------------------------------------------------------
echo.

set /p DUMP_FILE=Enter dump file name: 

if "%DUMP_FILE%"=="" (
    echo [ERROR] Dump file name is empty.
    pause
    exit /b 1
)

if not exist "%BACKUP_DIR%%DUMP_FILE%" (
    echo [ERROR] Dump file not found: %BACKUP_DIR%%DUMP_FILE%
    pause
    exit /b 1
)

set LOG_FILE=%DUMP_FILE%
set LOG_FILE=%LOG_FILE:.DMP=%
set LOG_FILE=%LOG_FILE:.dmp=%
set LOG_FILE=%LOG_FILE%_IMPORT_TO_%TARGET_SCHEMA%.log

echo.
echo [STEP 1] Creating Oracle DIRECTORY object...

(
echo CREATE OR REPLACE DIRECTORY %ORACLE_DIR_NAME% AS '%BACKUP_DIR%';
echo GRANT READ, WRITE ON DIRECTORY %ORACLE_DIR_NAME% TO %TARGET_SCHEMA%;
echo EXIT;
) > "%TEMP%\create_hqt_dir.sql"

sqlplus -L %DB_ADMIN_USER%/%DB_ADMIN_PASSWORD%@//%DB_HOST%:%DB_PORT%/%DB_SERVICE% @"%TEMP%\create_hqt_dir.sql"

if errorlevel 1 (
    echo.
    echo [ERROR] Cannot create DIRECTORY object.
    echo Check temp file: %TEMP%\create_hqt_dir.sql
    pause
    exit /b 1
)

echo.
echo [WARN] Import will copy schema %SOURCE_SCHEMA% from dump into %TARGET_SCHEMA%.
echo [WARN] Existing tables in %TARGET_SCHEMA% may be replaced.
echo.
set /p CONFIRM=Type YES to continue import: 

if /I not "%CONFIRM%"=="YES" (
    echo [CANCELLED] Import cancelled.
    pause
    exit /b 0
)

echo.
echo [STEP 2] Importing dump file %DUMP_FILE% into schema %TARGET_SCHEMA%...

impdp %DB_ADMIN_USER%/%DB_ADMIN_PASSWORD%@//%DB_HOST%:%DB_PORT%/%DB_SERVICE% ^
    DIRECTORY=%ORACLE_DIR_NAME% ^
    DUMPFILE=%DUMP_FILE% ^
    LOGFILE=%LOG_FILE% ^
    SCHEMAS=%SOURCE_SCHEMA% ^
    REMAP_SCHEMA=%SOURCE_SCHEMA%:%TARGET_SCHEMA% ^
    TABLE_EXISTS_ACTION=REPLACE ^
    TRANSFORM=OID:N ^
    TRANSFORM=SEGMENT_ATTRIBUTES:N ^
    EXCLUDE=STATISTICS ^
    EXCLUDE=TRIGGER

if errorlevel 1 (
    echo.
    echo [ERROR] Import completed with errors.
    echo Check log file: %BACKUP_DIR%%LOG_FILE%
    pause
    exit /b 1
)

echo.
echo ==========================================================
echo [DONE] Import completed successfully.
echo Target schema: %TARGET_SCHEMA%
echo Log file     : %BACKUP_DIR%%LOG_FILE%
echo ==========================================================
echo.

pause
exit /b 0