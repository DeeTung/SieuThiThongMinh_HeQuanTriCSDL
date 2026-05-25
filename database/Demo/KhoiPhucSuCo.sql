-- ============================================================
-- DEMO KHOI PHUC SU CO - SMART SUPERMARKET
-- Demo transaction COMMIT / ROLLBACK trong chuc nang thanh toan
-- Data demo:
-- Store      : ST001
-- Product    : SP0000001
-- Stock goc  : 2120
-- ============================================================

-- ============================================================
-- 1. RESET TON KHO TRUOC KHI DEMO
-- ============================================================

UPDATE INVENTORY
SET quantity = 2120,
    last_updated = SYSDATE
WHERE store_id = 'ST001'
  AND product_id = 'SP0000001'
  AND NVL(is_deleted, 0) = 0;

COMMIT;

-- ============================================================
-- 2. KIEM TRA TON KHO TRUOC KHI THANH TOAN
-- ============================================================

SELECT store_id,
       product_id,
       quantity
FROM INVENTORY
WHERE store_id = 'ST001'
  AND product_id = 'SP0000001'
  AND NVL(is_deleted, 0) = 0;

-- ============================================================
-- 3. GHI NHAN SO LUONG DON HANG TRUOC KHI DEMO
-- ============================================================

SELECT COUNT(*) AS total_orders -- 187
FROM ORDERS;

SELECT COUNT(*) AS total_order_details -- 414
FROM ORDER_DETAILS;

-- ============================================================
-- 4. KIEM TRA SAN PHAM DEMO CO HIEN TREN APP KHONG
-- ============================================================

SELECT p.product_id,
       p.product_name,
       p.Base_Price,
       i.store_id,
       i.quantity
FROM PRODUCTS p
JOIN INVENTORY i
    ON i.product_id = p.product_id
WHERE p.product_id = 'SP0000001'
  AND i.store_id = 'ST001'
  AND NVL(p.is_deleted, 0) = 0
  AND NVL(i.is_deleted, 0) = 0;

-- ============================================================
-- 5. SAU KHI BAM THANH TOAN O CHE DO LOI GIA LAP
-- CHAY LAI CAC CAU SAU DE KIEM TRA ROLLBACK
-- ============================================================

SELECT COUNT(*) AS total_orders_after_rollback
FROM ORDERS;

SELECT COUNT(*) AS total_order_details_after_rollback
FROM ORDER_DETAILS;

SELECT store_id,
       product_id,
       quantity
FROM INVENTORY
WHERE store_id = 'ST001'
  AND product_id = 'SP0000001'
  AND NVL(is_deleted, 0) = 0;

-- Ket qua mong muon khi rollback thanh cong:
-- total_orders khong tang
-- total_order_details khong tang
-- quantity van la 2120

-- ============================================================
-- 6. SAU KHI TAT LOI GIA LAP VA THANH TOAN THANH CONG
-- CHAY LAI CAC CAU SAU DE KIEM TRA COMMIT
-- ============================================================

SELECT COUNT(*) AS total_orders_after_commit
FROM ORDERS;

SELECT COUNT(*) AS total_order_details_after_commit
FROM ORDER_DETAILS;

SELECT store_id,
       product_id,
       quantity
FROM INVENTORY
WHERE store_id = 'ST001'
  AND product_id = 'SP0000001'
  AND NVL(is_deleted, 0) = 0;

-- Ket qua mong muon khi commit thanh cong:
-- total_orders tang
-- total_order_details tang
-- quantity = 2120 - so_luong_ban



SELECT COUNT(*) AS total_orders_after_rollback
FROM ORDERS;

SELECT COUNT(*) AS total_order_details_after_rollback
FROM ORDER_DETAILS;

SELECT store_id,
       product_id,
       quantity
FROM INVENTORY
WHERE store_id = 'ST001'
  AND product_id = 'SP0000001'
  AND NVL(is_deleted, 0) = 0;

