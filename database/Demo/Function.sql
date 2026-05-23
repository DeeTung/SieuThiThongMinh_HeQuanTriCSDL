CREATE OR REPLACE FUNCTION FUNC_GET_FINAL_SYSTEM_REVENUE (
    p_from_date IN DATE DEFAULT NULL,
    p_to_date   IN DATE DEFAULT NULL
) RETURN NUMBER
IS
    v_total_sales   NUMBER := 0;
    v_total_import  NUMBER := 0;
    v_final_revenue NUMBER := 0;
BEGIN
    SELECT NVL(SUM(o.total_amount), 0)
    INTO v_total_sales
    FROM ORDERS o
    WHERE NVL(o.is_deleted, 0) = 0
      AND (
            UPPER(NVL(o.status, '')) = 'COMPLETED'
            OR UPPER(NVL(o.status, '')) = 'SUCCESS'
            OR UPPER(NVL(o.status, '')) = 'PAID'
            OR UPPER(NVL(o.status, '')) LIKE '%HOÀN THÀNH%'
            OR UPPER(NVL(o.status, '')) LIKE '%HOAN THANH%'
            OR UPPER(NVL(o.status, '')) LIKE '%ĐÃ THANH TOÁN%'
            OR UPPER(NVL(o.status, '')) LIKE '%DA THANH TOAN%'
          )
      AND (p_from_date IS NULL OR o.order_date >= p_from_date)
      AND (p_to_date IS NULL OR o.order_date < p_to_date + 1);

    SELECT NVL(SUM(pr.total_after_tax), 0)
    INTO v_total_import
    FROM PURCHASE_RECEIPTS pr
    WHERE NVL(pr.is_deleted, 0) = 0
      AND (p_from_date IS NULL OR pr.created_at >= CAST(p_from_date AS TIMESTAMP))
      AND (p_to_date IS NULL OR pr.created_at < CAST(p_to_date + 1 AS TIMESTAMP));

    v_final_revenue := v_total_sales - v_total_import;

    RETURN ROUND(v_final_revenue, 2);

EXCEPTION
    WHEN OTHERS THEN
        RETURN -1;
END FUNC_GET_FINAL_SYSTEM_REVENUE;
/