CREATE OR REPLACE PROCEDURE PROC_DEMO_SELL_PRODUCT_LOST_UPDATE(
    p_employee_id IN VARCHAR2,
    p_store_id IN VARCHAR2,
    p_product_id IN VARCHAR2,
    p_sell_qty IN NUMBER,
    p_sleep_seconds IN NUMBER DEFAULT 8,
    p_result_code OUT NUMBER,
    p_result_message OUT VARCHAR2
)
    IS
    v_before_qty    NUMBER := 0;
    v_after_qty     NUMBER := 0;
    v_log_id        VARCHAR2(50);
    v_error_message VARCHAR2(1000);
BEGIN
    /*
     * ============================================================
     * DEMO LOST UPDATE
     * ============================================================
     *
     * BUG MODE:
     *   Comment dòng SET TRANSACTION bên dưới.
     *
     * FIX MODE:
     *   Mở dòng SET TRANSACTION bên dưới.
     *
     * Java không tự xử lý Lost Update.
     * Java chỉ gọi procedure này.
     */

    -- ============================================================
    -- BẬT/TẮT DÒNG NÀY ĐỂ DEMO
    -- ============================================================

    -- BUG MODE: comment dòng dưới.
    -- FIX MODE: mở dòng dưới.
 EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';


    /*
     * Đọc tồn kho hiện tại.
     * Cố tình KHÔNG dùng SELECT FOR UPDATE.
     */
    SELECT NVL(quantity, 0)
    INTO v_before_qty
    FROM INVENTORY
    WHERE store_id = p_store_id
      AND product_id = p_product_id
      AND NVL(is_deleted, 0) = 0;


    /*
     * Giả lập thời gian xử lý lâu để 2 nhân viên có thể cùng đọc tồn kho cũ.
     * Dùng DBMS_SESSION.SLEEP để tránh lỗi thiếu quyền DBMS_LOCK.
     */
    DBMS_SESSION.SLEEP(NVL(p_sleep_seconds, 8));


    /*
     * Kiểm tra tồn kho dựa trên giá trị đã đọc ban đầu.
     */
    IF v_before_qty < p_sell_qty THEN
        p_result_code := 0;
        p_result_message := 'Không đủ tồn kho. Tồn đọc được = '
            || v_before_qty
            || ', số lượng cần bán = '
            || p_sell_qty;

        ROLLBACK;
        RETURN;
    END IF;


    /*
     * Cố tình update theo giá trị đã đọc ban đầu.
     *
     * Đây là điểm tạo Lost Update:
     * Nếu 2 transaction cùng đọc v_before_qty = 10,
     * cả 2 đều tính after = 0.
     */
    v_after_qty := v_before_qty - p_sell_qty;

    UPDATE INVENTORY
    SET quantity     = v_after_qty,
        last_updated = SYSDATE
    WHERE store_id = p_store_id
      AND product_id = p_product_id
      AND NVL(is_deleted, 0) = 0;


    /*
     * Ghi log thành công.
     */
    v_log_id := 'LU' || TO_CHAR(SYSTIMESTAMP, 'YYYYMMDDHH24MISSFF6');

    INSERT INTO DEMO_LOST_UPDATE_LOG (LOG_ID,
                                      EMPLOYEE_ID,
                                      STORE_ID,
                                      PRODUCT_ID,
                                      SELL_QTY,
                                      BEFORE_QTY,
                                      AFTER_QTY,
                                      DEMO_MODE,
                                      STATUS,
                                      ERROR_MESSAGE)
    VALUES (v_log_id,
            p_employee_id,
            p_store_id,
            p_product_id,
            p_sell_qty,
            v_before_qty,
            v_after_qty,
            'SERIALIZABLE_OR_BUG_MODE',
            'SUCCESS',
            NULL);

    COMMIT;

    p_result_code := 1;
    p_result_message := 'Bán thành công. Before = '
        || v_before_qty
        || ', After = '
        || v_after_qty;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;

        p_result_code := -1;
        p_result_message := 'Không tìm thấy tồn kho cho store_id='
            || p_store_id
            || ', product_id='
            || p_product_id;

    WHEN OTHERS THEN
        ROLLBACK;

        v_error_message := SQLERRM;

        p_result_code := -2;
        p_result_message := 'Lỗi Lost Update demo: ' || v_error_message;

        BEGIN
            v_log_id := 'LU' || TO_CHAR(SYSTIMESTAMP, 'YYYYMMDDHH24MISSFF6');

            INSERT INTO DEMO_LOST_UPDATE_LOG (LOG_ID,
                                              EMPLOYEE_ID,
                                              STORE_ID,
                                              PRODUCT_ID,
                                              SELL_QTY,
                                              BEFORE_QTY,
                                              AFTER_QTY,
                                              DEMO_MODE,
                                              STATUS,
                                              ERROR_MESSAGE)
            VALUES (v_log_id,
                    p_employee_id,
                    p_store_id,
                    p_product_id,
                    p_sell_qty,
                    v_before_qty,
                    v_after_qty,
                    'SERIALIZABLE_OR_BUG_MODE',
                    'FAILED',
                    v_error_message);

            COMMIT;
        EXCEPTION
            WHEN OTHERS THEN
                NULL;
        END;
END PROC_DEMO_SELL_PRODUCT_LOST_UPDATE;
/


-- Session 1 --
DECLARE
    v_code NUMBER;
    v_msg  VARCHAR2(1000);
BEGIN
    PROC_DEMO_SELL_PRODUCT_LOST_UPDATE(
        p_employee_id    => 'EMP_STAFF_1',
        p_store_id       => 'ST001',
        p_product_id     => 'SP0000037',
        p_sell_qty       => 32,
        p_sleep_seconds  => 8,
        p_result_code    => v_code,
        p_result_message => v_msg
    );

    DBMS_OUTPUT.PUT_LINE('CODE=' || v_code);
    DBMS_OUTPUT.PUT_LINE('MSG=' || v_msg);
END;
/


-- Session 2 --
DECLARE
    v_code NUMBER;
    v_msg  VARCHAR2(1000);
BEGIN
    PROC_DEMO_SELL_PRODUCT_LOST_UPDATE(
        p_employee_id    => 'EMP_STAFF_1',
        p_store_id       => 'ST001',
        p_product_id     => 'SP0000037',
        p_sell_qty       => 32,
        p_sleep_seconds  => 8,
        p_result_code    => v_code,
        p_result_message => v_msg
    );

    DBMS_OUTPUT.PUT_LINE('CODE=' || v_code);
    DBMS_OUTPUT.PUT_LINE('MSG=' || v_msg);
END;
/

select count(*) from products where product_id = 'SP0000037' And store_id = 'ST001';


SELECT object_name, object_type, status
FROM user_objects
WHERE object_name = 'PROC_DEMO_SELL_PRODUCT_LOST_UPDATE';

