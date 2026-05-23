-- ==========================================================
-- Local Docker development login accounts
-- Password for all accounts: 123456
-- BCrypt hash generated with the same jBCrypt-compatible format used by PasswordUtils.
-- Role mapping:
--   ADMIN          -> R_ADMIN_ALL
--   STORE_MANAGER  -> R_STORE_MNG
--   SALES          -> R_STAFF_SALE
--   WAREHOUSE      -> R_STAFF_VIEW_PROD
-- ==========================================================

-- Base function required by ROLES.function_id foreign key.
MERGE INTO FUNCTIONS f
USING (SELECT 'F_LOCAL_DEV' function_id, 'Local development access' function_name FROM dual) src
ON (f.function_id = src.function_id)
WHEN MATCHED THEN UPDATE SET f.function_name = src.function_name, f.is_deleted = 0
WHEN NOT MATCHED THEN INSERT (function_id, function_name, is_deleted)
VALUES (src.function_id, src.function_name, 0);

-- Role rows used by the current login and authorization flow.
MERGE INTO ROLES r
USING (
    SELECT 'R_ADMIN_ALL' role_id, 'Admin' role_name, 'F_LOCAL_DEV' function_id,
           1 can_view, 1 can_add, 1 can_edit, 1 can_delete, 1 can_export FROM dual
    UNION ALL
    SELECT 'R_STORE_MNG', 'Store Manager', 'F_LOCAL_DEV',
           1, 1, 1, 1, 1 FROM dual
    UNION ALL
    SELECT 'R_STAFF_SALE', 'Sales Staff', 'F_LOCAL_DEV',
           1, 1, 0, 0, 1 FROM dual
    UNION ALL
    SELECT 'R_STAFF_VIEW_PROD', 'Warehouse Staff', 'F_LOCAL_DEV',
           1, 1, 1, 0, 1 FROM dual
) src
ON (r.role_id = src.role_id)
WHEN MATCHED THEN UPDATE SET
    r.role_name = src.role_name,
    r.function_id = src.function_id,
    r.can_view = src.can_view,
    r.can_add = src.can_add,
    r.can_edit = src.can_edit,
    r.can_delete = src.can_delete,
    r.can_export = src.can_export,
    r.is_deleted = 0
WHEN NOT MATCHED THEN INSERT (
    role_id, role_name, function_id, can_view, can_add, can_edit, can_delete, can_export, is_deleted
) VALUES (
    src.role_id, src.role_name, src.function_id, src.can_view, src.can_add, src.can_edit,
    src.can_delete, src.can_export, 0
);

-- USERS records backing ACCOUNTS.user_id.
MERGE INTO USERS u
USING (
    SELECT 'U_LOCAL_ADMIN' user_id, 'Local Admin' full_name, 'admin@local.dev' email, '0900000001' phone_number FROM dual
    UNION ALL
    SELECT 'U_LOCAL_MANAGER', 'Local Store Manager', 'manager@local.dev', '0900000002' FROM dual
    UNION ALL
    SELECT 'U_LOCAL_SALE', 'Local Sale Staff', 'sale@local.dev', '0900000003' FROM dual
    UNION ALL
    SELECT 'U_LOCAL_WAREHOUSE', 'Local Warehouse Staff', 'warehouse@local.dev', '0900000004' FROM dual
    UNION ALL
    SELECT 'U_LOCAL_CASHIER', 'Local Cashier Staff', 'cashier@local.dev', '0900000005' FROM dual
) src
ON (u.user_id = src.user_id)
WHEN MATCHED THEN UPDATE SET
    u.full_name = src.full_name,
    u.email = src.email,
    u.phone_number = src.phone_number,
    u.is_deleted = 0
WHEN NOT MATCHED THEN INSERT (user_id, full_name, email, phone_number, is_deleted)
VALUES (src.user_id, src.full_name, src.email, src.phone_number, 0);

-- EMPLOYEES records for modules that expect staff identities with role_id.
MERGE INTO EMPLOYEES e
USING (
    SELECT 'EMP_LOCAL_ADMIN' employee_id, 'Local Admin' employee_name, '0900000001' phone, 'admin@local.dev' email, 'R_ADMIN_ALL' role_id FROM dual
    UNION ALL
    SELECT 'EMP_LOCAL_MANAGER', 'Local Store Manager', '0900000002', 'manager@local.dev', 'R_STORE_MNG' FROM dual
    UNION ALL
    SELECT 'EMP_LOCAL_SALE', 'Local Sale Staff', '0900000003', 'sale@local.dev', 'R_STAFF_SALE' FROM dual
    UNION ALL
    SELECT 'EMP_LOCAL_CASHIER', 'Local Cashier Staff', '0900000005', 'cashier@local.dev', 'R_STAFF_SALE' FROM dual
    UNION ALL
    SELECT 'EMP_LOCAL_SALE_2', 'Nguyen Thi Lan', '0900000006', 'lan.sale@local.dev', 'R_STAFF_SALE' FROM dual
    UNION ALL
    SELECT 'EMP_LOCAL_WAREHOUSE', 'Local Warehouse Staff', '0900000004', 'warehouse@local.dev', 'R_STAFF_VIEW_PROD' FROM dual
    UNION ALL
    SELECT 'EMP_LOCAL_STOCK_2', 'Tran Van Kho', '0900000007', 'kho@local.dev', 'R_STAFF_VIEW_PROD' FROM dual
) src
ON (e.employee_id = src.employee_id)
WHEN MATCHED THEN UPDATE SET
    e.employee_name = src.employee_name,
    e.phone = src.phone,
    e.email = src.email,
    e.role_id = src.role_id,
    e.salary_coefficient = 1.0,
    e.is_deleted = 0
WHEN NOT MATCHED THEN INSERT (
    employee_id, employee_name, hire_date, phone, email, salary_coefficient, role_id, is_deleted
) VALUES (
    src.employee_id, src.employee_name, DATE '2026-01-01', src.phone, src.email, 1.0, src.role_id, 0
);

-- ACCOUNTS. Same BCrypt hash verifies plain password "123456".
MERGE INTO ACCOUNTS a
USING (
    SELECT 'ACC_LOCAL_ADMIN' account_id, 'U_LOCAL_ADMIN' user_id, 'admin' username, 'R_ADMIN_ALL' role_code FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_MANAGER', 'U_LOCAL_MANAGER', 'manager', 'R_STORE_MNG' FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_SALE', 'U_LOCAL_SALE', 'sale', 'R_STAFF_SALE' FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_WAREHOUSE', 'U_LOCAL_WAREHOUSE', 'warehouse', 'R_STAFF_VIEW_PROD' FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_CASHIER', 'U_LOCAL_CASHIER', 'cashier', 'R_STAFF_SALE' FROM dual
) src
ON (a.account_id = src.account_id)
WHEN MATCHED THEN UPDATE SET
    a.user_id = src.user_id,
    a.username = src.username,
    a.password = '$2a$10$Oc0XJHuKZF1sPDhGSpeum.G3pXkPpl46Cq6EgXGHSnTGc/.odACy.',
    a.status = 'ACTIVE',
    a.role = src.role_code,
    a.is_deleted = 0
WHEN NOT MATCHED THEN INSERT (account_id, user_id, username, password, status, role, is_deleted)
VALUES (
    src.account_id,
    src.user_id,
    src.username,
    '$2a$10$Oc0XJHuKZF1sPDhGSpeum.G3pXkPpl46Cq6EgXGHSnTGc/.odACy.',
    'ACTIVE',
    src.role_code,
    0
);

-- Direct role assignment used by AccountSql.selectByUsername and LoginService.
MERGE INTO ACCOUNT_ASSIGN_ROLE aar
USING (
    SELECT 'ACC_LOCAL_ADMIN' account_id, 'R_ADMIN_ALL' role_id FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_MANAGER', 'R_STORE_MNG' FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_SALE', 'R_STAFF_SALE' FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_WAREHOUSE', 'R_STAFF_VIEW_PROD' FROM dual
    UNION ALL
    SELECT 'ACC_LOCAL_CASHIER', 'R_STAFF_SALE' FROM dual
) src
ON (aar.account_id = src.account_id AND aar.role_id = src.role_id)
WHEN MATCHED THEN UPDATE SET aar.is_deleted = 0
WHEN NOT MATCHED THEN INSERT (account_id, role_id, is_deleted)
VALUES (src.account_id, src.role_id, 0);

-- Shift catalog used by the Employee Management > Phan ca tab.
MERGE INTO SHIFTS s
USING (
    SELECT 'SHIFT_MORNING' shift_id, N'Ca sáng' shift_name, '07:00' start_text, '15:00' end_text FROM dual
    UNION ALL
    SELECT 'SHIFT_AFTERNOON', N'Ca chiều', '15:00', '23:00' FROM dual
    UNION ALL
    SELECT 'SHIFT_NIGHT', N'Ca tối', '23:00', '07:00' FROM dual
) src
ON (s.shift_id = src.shift_id)
WHEN MATCHED THEN UPDATE SET
    s.shift_name = src.shift_name,
    s.start_time = TO_DATE(src.start_text, 'HH24:MI'),
    s.end_time = TO_DATE(src.end_text, 'HH24:MI'),
    s.is_deleted = 0
WHEN NOT MATCHED THEN INSERT (shift_id, shift_name, start_time, end_time, is_deleted)
VALUES (src.shift_id, src.shift_name, TO_DATE(src.start_text, 'HH24:MI'), TO_DATE(src.end_text, 'HH24:MI'), 0);

-- Sample shift assignments for sale/cashier and warehouse staff.
MERGE INTO EMPLOYEE_SHIFTS es
USING (
    SELECT 'PC_LOCAL_001' assignment_id, 'EMP_LOCAL_SALE' employee_id, 'SHIFT_MORNING' shift_id,
           TRUNC(SYSDATE) work_date, 'ASSIGNED' status, N'Bán hàng khu thực phẩm tươi' note FROM dual
    UNION ALL
    SELECT 'PC_LOCAL_002', 'EMP_LOCAL_CASHIER', 'SHIFT_AFTERNOON',
           TRUNC(SYSDATE), 'ASSIGNED', N'Thu ngân quầy 2' FROM dual
    UNION ALL
    SELECT 'PC_LOCAL_003', 'EMP_LOCAL_SALE_2', 'SHIFT_NIGHT',
           TRUNC(SYSDATE), 'CANCELED', N'Nghỉ phép' FROM dual
    UNION ALL
    SELECT 'PC_LOCAL_004', 'EMP_LOCAL_WAREHOUSE', 'SHIFT_MORNING',
           TRUNC(SYSDATE), 'COMPLETED', N'Kiểm kê kho đầu ngày' FROM dual
    UNION ALL
    SELECT 'PC_LOCAL_005', 'EMP_LOCAL_STOCK_2', 'SHIFT_AFTERNOON',
           TRUNC(SYSDATE), 'ASSIGNED', N'Nhập hàng và sắp xếp kệ' FROM dual
    UNION ALL
    SELECT 'PC_LOCAL_006', 'EMP_LOCAL_SALE', 'SHIFT_AFTERNOON',
           TRUNC(SYSDATE) + 1, 'ASSIGNED', N'Thu ngân quầy 1' FROM dual
    UNION ALL
    SELECT 'PC_LOCAL_007', 'EMP_LOCAL_WAREHOUSE', 'SHIFT_NIGHT',
           TRUNC(SYSDATE) + 1, 'ASSIGNED', N'Trực kho ca tối' FROM dual
    UNION ALL
    SELECT 'PC_LOCAL_008', 'EMP_LOCAL_CASHIER', 'SHIFT_MORNING',
           TRUNC(SYSDATE) - 1, 'COMPLETED', N'Ca sáng đã hoàn tất' FROM dual
) src
ON (es.assignment_id = src.assignment_id)
WHEN MATCHED THEN UPDATE SET
    es.employee_id = src.employee_id,
    es.shift_id = src.shift_id,
    es.work_date = src.work_date,
    es.status = src.status,
    es.note = src.note,
    es.is_deleted = 0,
    es.updated_at = SYSTIMESTAMP
WHEN NOT MATCHED THEN INSERT (
    assignment_id, employee_id, shift_id, work_date, status, note, is_deleted
) VALUES (
    src.assignment_id, src.employee_id, src.shift_id, src.work_date, src.status, src.note, 0
);

COMMIT;
