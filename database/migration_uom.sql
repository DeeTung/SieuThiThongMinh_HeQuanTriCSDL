-- Migration: Unit of Measure (UoM)
-- Chay file nay tren database da ton tai truoc khi dung tinh nang quy doi don vi.

CREATE TABLE UNITS
(
    unit_id    VARCHAR2(50) PRIMARY KEY,
    unit_name  NVARCHAR2(50) NOT NULL,
    is_deleted NUMBER(1) DEFAULT 0
);

CREATE TABLE PRODUCT_UNITS
(
    product_id              VARCHAR2(50),
    unit_id                 VARCHAR2(50),
    conversion_rate_to_base NUMBER(15, 4) NOT NULL,
    is_base_unit            NUMBER(1) DEFAULT 0,
    is_deleted              NUMBER(1) DEFAULT 0,
    PRIMARY KEY (product_id, unit_id),
    CONSTRAINT FK_PU_PRODUCTS FOREIGN KEY (product_id) REFERENCES PRODUCTS (product_id),
    CONSTRAINT FK_PU_UNITS FOREIGN KEY (unit_id) REFERENCES UNITS (unit_id),
    CONSTRAINT CK_PU_CONVERSION_POSITIVE CHECK (conversion_rate_to_base > 0)
);

ALTER TABLE PRODUCTS ADD (base_unit_id VARCHAR2(50));
ALTER TABLE PRODUCTS ADD CONSTRAINT FK_PRODUCTS_BASE_UNIT FOREIGN KEY (base_unit_id) REFERENCES UNITS (unit_id);

ALTER TABLE ORDER_DETAILS ADD (unit_id VARCHAR2(50));
ALTER TABLE ORDER_DETAILS ADD (quantity_base NUMBER(10));
ALTER TABLE ORDER_DETAILS ADD CONSTRAINT FK_OD_UNITS FOREIGN KEY (unit_id) REFERENCES UNITS (unit_id);

INSERT INTO UNITS (unit_id, unit_name) VALUES ('U_CAI', N'Cai');
INSERT INTO UNITS (unit_id, unit_name) VALUES ('U_HOP', N'Hop');
INSERT INTO UNITS (unit_id, unit_name) VALUES ('U_THUNG', N'Thung');

UPDATE PRODUCTS
SET base_unit_id = 'U_CAI'
WHERE base_unit_id IS NULL;

INSERT INTO PRODUCT_UNITS (product_id, unit_id, conversion_rate_to_base, is_base_unit, is_deleted)
SELECT product_id, 'U_CAI', 1, 1, 0
FROM PRODUCTS
WHERE is_deleted = 0;

UPDATE ORDER_DETAILS
SET unit_id = 'U_CAI',
    quantity_base = quantity
WHERE unit_id IS NULL;

COMMIT;
