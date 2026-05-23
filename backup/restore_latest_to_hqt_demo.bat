@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM Smart Supermarket - Restore Latest Backup to HQT_DEMO
REM Purpose: Import latest .DMP file vao schema HQT_DEMO
REM ==========================================================

set DB_HOST=10.0.249.155
set DB_PORT=1521
set DB_SERVICE=ORCLPDB

set DB_ADMIN_USER=system
set DB_ADMIN_PASSWORD=Admin123

set SOURCE_SCHEMA=SYSTEM
set TARGET_SCHEMA=HQT_DEMO
set TARGET_PASSWORD=HQT123

set BACKUP_DIR=%~dp0
set ORACLE_DIR_NAME=DATA_PUMP_DIR_SMART

echo.
echo ==========================================================
echo Smart Supermarket - Restore Latest Backup to HQT_DEMO
echo ==========================================================
echo Host          : %DB_HOST%
echo Port          : %DB_PORT%
echo Service       : %DB_SERVICE%
echo Source schema : %SOURCE_SCHEMA%
echo Target schema : %TARGET_SCHEMA%
echo Backup dir    : %BACKUP_DIR%
echo ==========================================================
echo.

set LATEST_DMP=

for /f "delims=" %%F in ('dir /b /o-d "%BACKUP_DIR%*.DMP" 2^>nul') do (
    set LATEST_DMP=%%F
    goto FOUND_DMP
)

:FOUND_DMP

if "%LATEST_DMP%"=="" (
    echo [ERROR] No .DMP file found in backup folder.
    pause
    exit /b 1
)

echo Latest dump file:
echo %LATEST_DMP%
echo.

set LOG_FILE=%LATEST_DMP:.DMP=_RESTORE_TO_%TARGET_SCHEMA%.log%
set LOG_FILE=%LOG_FILE:.dmp=_RESTORE_TO_%TARGET_SCHEMA%.log%

set /p CONFIRM=Type YES to restore latest backup to %TARGET_SCHEMA%: 

if /I not "%CONFIRM%"=="YES" (
    echo [CANCELLED] Restore cancelled.
    pause
    exit /b 0
)

where sqlplus >nul 2>&1
if errorlevel 1 (
    echo [ERROR] sqlplus not found. Please check Oracle Client / PATH.
    pause
    exit /b 1
)

where impdp >nul 2>&1
if errorlevel 1 (
    echo [ERROR] impdp not found. Please check Oracle Client / PATH.
    pause
    exit /b 1
)

(
echo DECLARE
echo   v_count NUMBER;
echo BEGIN
echo   SELECT COUNT(*) INTO v_count FROM dba_users WHERE username = UPPER('%TARGET_SCHEMA%');
echo   IF v_count = 0 THEN
echo     EXECUTE IMMEDIATE 'CREATE USER %TARGET_SCHEMA% IDENTIFIED BY %TARGET_PASSWORD%';
echo   ELSE
echo     EXECUTE IMMEDIATE 'ALTER USER %TARGET_SCHEMA% IDENTIFIED BY %TARGET_PASSWORD% ACCOUNT UNLOCK';
echo   END IF;
echo END;
echo /
echo GRANT CONNECT, RESOURCE TO %TARGET_SCHEMA%;
echo GRANT CREATE SESSION TO %TARGET_SCHEMA%;
echo GRANT CREATE TABLE TO %TARGET_SCHEMA%;
echo GRANT CREATE VIEW TO %TARGET_SCHEMA%;
echo GRANT CREATE SEQUENCE TO %TARGET_SCHEMA%;
echo GRANT CREATE PROCEDURE TO %TARGET_SCHEMA%;
echo GRANT CREATE TRIGGER TO %TARGET_SCHEMA%;
echo GRANT CREATE TYPE TO %TARGET_SCHEMA%;
echo ALTER USER %TARGET_SCHEMA% QUOTA UNLIMITED ON USERS;
echo CREATE OR REPLACE DIRECTORY %ORACLE_DIR_NAME% AS '%BACKUP_DIR:\=/%';
echo GRANT READ, WRITE ON DIRECTORY %ORACLE_DIR_NAME% TO %DB_ADMIN_USER%;
echo GRANT READ, WRITE ON DIRECTORY %ORACLE_DIR_NAME% TO %TARGET_SCHEMA%;
echo EXIT;
) > "%TEMP%\smart_prepare_hqt_restore.sql"

sqlplus -L %DB_ADMIN_USER%/%DB_ADMIN_PASSWORD%@//%DB_HOST%:%DB_PORT%/%DB_SERVICE% @"%TEMP%\smart_prepare_hqt_restore.sql"

if errorlevel 1 (
    echo [ERROR] Cannot prepare target schema or Oracle DIRECTORY.
    pause
    exit /b 1
)

echo.
echo [INFO] Restoring %LATEST_DMP% into %TARGET_SCHEMA%...

impdp %DB_ADMIN_USER%/%DB_ADMIN_PASSWORD%@//%DB_HOST%:%DB_PORT%/%DB_SERVICE% ^
    DIRECTORY=%ORACLE_DIR_NAME% ^
    DUMPFILE=%LATEST_DMP% ^
    LOGFILE=%LOG_FILE% ^
    SCHEMAS=%SOURCE_SCHEMA% ^
    REMAP_SCHEMA=%SOURCE_SCHEMA%:%TARGET_SCHEMA% ^
    TABLE_EXISTS_ACTION=REPLACE ^
    TRANSFORM=OID:N ^
    EXCLUDE=STATISTICS

if errorlevel 1 (
    echo.
    echo [ERROR] Restore failed.
    echo Check log file: %BACKUP_DIR%%LOG_FILE%
    pause
    exit /b 1
)

echo.
echo ==========================================================
echo [DONE] Restore completed successfully.
echo Target schema: %TARGET_SCHEMA%
echo Dump file    : %LATEST_DMP%
echo Log file     : %BACKUP_DIR%%LOG_FILE%
echo ==========================================================
echo.

pause
exit /b 0
