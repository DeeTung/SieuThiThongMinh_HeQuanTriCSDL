DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tab_cols
    WHERE table_name = 'ORDERS' AND column_name = 'PAYMENT_METHOD_ID';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE ORDERS ADD (payment_method_id VARCHAR2(50))';
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM user_tab_cols
    WHERE table_name = 'ORDERS' AND column_name = 'NOTE';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE ORDERS ADD (note NVARCHAR2(255))';
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM user_constraints
    WHERE constraint_name = 'FK_ORDERS_PM';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE ORDERS ADD CONSTRAINT FK_ORDERS_PM FOREIGN KEY (payment_method_id) REFERENCES PAYMENT_METHODS (payment_method_id)';
    END IF;
END;
/

MERGE INTO PAYMENT_METHODS pm
USING (
    SELECT 'PM_CASH' payment_method_id FROM dual
    UNION ALL SELECT 'PM_TRANSFER' FROM dual
    UNION ALL SELECT 'PM_EWALLET' FROM dual
) src
ON (pm.payment_method_id = src.payment_method_id)
WHEN MATCHED THEN UPDATE SET is_deleted = 0
WHEN NOT MATCHED THEN INSERT (payment_method_id, is_deleted) VALUES (src.payment_method_id, 0);

MERGE INTO CASH_PAYMENT cp
USING (SELECT 'PM_CASH' payment_method_id FROM dual) src
ON (cp.payment_method_id = src.payment_method_id)
WHEN MATCHED THEN UPDATE SET is_deleted = 0
WHEN NOT MATCHED THEN INSERT (payment_method_id, is_deleted) VALUES (src.payment_method_id, 0);

COMMIT;
