-- Local/demo safety patch for app heartbeat and DB polling.
-- Run as APPUSER on FREEPDB1. This script is idempotent.

DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'ACCOUNT_SESSIONS';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE ACCOUNT_SESSIONS (
                session_id        VARCHAR2(100) PRIMARY KEY,
                account_id        VARCHAR2(50) NOT NULL,
                login_at          TIMESTAMP DEFAULT SYSTIMESTAMP,
                last_heartbeat_at TIMESTAMP DEFAULT SYSTIMESTAMP,
                logout_at         TIMESTAMP NULL,
                status            VARCHAR2(20) DEFAULT ''ACTIVE'',
                device_info       VARCHAR2(500),
                ip_address        VARCHAR2(100),
                is_deleted        NUMBER(1) DEFAULT 0,
                CONSTRAINT fk_account_sessions_account
                    FOREIGN KEY (account_id)
                    REFERENCES ACCOUNTS(account_id)
            )
        ';
    END IF;
END;
/

DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_indexes
    WHERE index_name = 'IDX_ACCOUNT_SESSIONS_ACC';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE INDEX IDX_ACCOUNT_SESSIONS_ACC
            ON ACCOUNT_SESSIONS(account_id, status, last_heartbeat_at)
        ';
    END IF;
END;
/

DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tables
    WHERE table_name = 'APP_SYNC';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE APP_SYNC (
                sync_key       VARCHAR2(50) PRIMARY KEY,
                version_number NUMBER DEFAULT 0 NOT NULL,
                updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
            )
        ';
    END IF;
END;
/

MERGE INTO APP_SYNC s
USING (
    SELECT 'PRODUCTS' sync_key FROM dual UNION ALL
    SELECT 'INVENTORY' FROM dual UNION ALL
    SELECT 'CUSTOMERS' FROM dual UNION ALL
    SELECT 'EMPLOYEES' FROM dual UNION ALL
    SELECT 'ORDERS' FROM dual UNION ALL
    SELECT 'STATISTICS' FROM dual UNION ALL
    SELECT 'DASHBOARD' FROM dual UNION ALL
    SELECT 'STORE_INFO' FROM dual UNION ALL
    SELECT 'SYSTEM_CONFIG' FROM dual UNION ALL
    SELECT 'ACCOUNT_SECURITY' FROM dual
) src
ON (s.sync_key = src.sync_key)
WHEN NOT MATCHED THEN
    INSERT (sync_key, version_number, updated_at)
    VALUES (src.sync_key, 0, CURRENT_TIMESTAMP);

COMMIT;
