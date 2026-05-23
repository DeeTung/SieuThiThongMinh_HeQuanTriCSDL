# Account, Role, and Operations Roadmap

## Account and role workflow

The application should not grant access based on words inside a username such as `admin` or `manager`. Roles must come from the RBAC tables and be managed by an admin account.

Current workflow:

1. When the database has no account yet, the first registered account is assigned `R_ADMIN_ALL`.
2. Later self-registered accounts are assigned `R_STAFF_SALE` by default.
3. Admin users open the admin portal and use Account Assign Role to change member roles.
4. Store manager users use the main store dashboard with management features.
5. Staff users only see features allowed by their role.

Role routing:

| Role | Destination | Expected access |
| --- | --- | --- |
| `R_ADMIN_ALL` | Admin portal | Account/role administration and central admin screens |
| `R_STORE_MNG` | Store dashboard | Products, employees, customers, invoices, statistics |
| `R_STAFF_SALE` | Store dashboard | POS/customer/invoice-oriented work, no employee management or statistics |
| `R_STAFF_STOCK` | Store dashboard | Product and inventory-oriented work, no employee management or statistics |

Implementation notes:

- `AccountSql.register(...)` already assigns the first account to `R_ADMIN_ALL` and later accounts to `R_STAFF_SALE`.
- `LoginView` routes admin accounts to `AdminDashboardView`; other accounts go to `DashboardView`.
- `AuthorizationService` centralizes role checks.
- `Sidebar` hides employee management and statistics unless `AuthorizationService.canAccessStatisticsAndEmployees()` is true.

## Current task status

| Task | Status | Evidence | Next work |
| --- | --- | --- | --- |
| First account becomes admin; later accounts default to staff | Done | `AccountSql.register(...)` uses account count and assigns `R_ADMIN_ALL` or `R_STAFF_SALE` | Add an admin-only account creation form so admins can create users without using public registration |
| Admin can assign roles to member accounts | Mostly done | `AccountRoleAssignmentPanel` calls role update logic through `AccountSql.updateAccountRole(...)` | Improve UX and validation; add audit messaging for role changes if needed |
| Staff cannot see statistics and employee management | Done | `AuthorizationService.canAccessStatisticsAndEmployees()` is used by `Sidebar` and guarded again in `DashboardView` | Manually test with `R_STAFF_SALE` and `R_STAFF_STOCK` accounts |
| Unit of measure setup and conversion | Mostly done | `UnitOfMeasureService`, `ProductUnitsSql`, `ProductView`, `SellPanel`, and `PaymentService` support unit configuration, selection, and conversion to base quantity; statistics aggregate `quantity_base` when available | Polish the product-unit dialog and add manual test cases for common conversions |
| Immutable warehouse / soft delete for products | Mostly done | `ProductsSql.delete(...)` soft-deletes product and inventory; `ProductView` checks `ProductsSql.isUsedInOrders(...)` and shows an "hidden" message when product has invoices | Tighten delete behavior wording: products used in invoices should always be treated as hidden, never physically deleted; add tests/manual checklist |
| Invoice/order execution | Mostly done | `PaymentService` saves orders/details and stock changes transactionally; `OrderView` supports detail viewing, status update, cancel rollback, and PDF invoice export | Improve invoice layout and add manual test cases for payment/cancel/export |
| Statistics execution | Mostly done | `StatisticSql` has totals, monthly revenue, best sellers, and recent orders; best-seller quantity now uses base-unit quantity when available; `StatisticView` renders dashboard/table | Add date filters and verify values against seed data |

## Follow-up plan

## Manual verification

Manual DB verification on 2026-04-28 passed against local Oracle `localhost:1521:orcl`.

Verified flow:

- Created an isolated test product with base stock `100`.
- Configured product unit `Thung = 24` base units.
- Converted `1 Thung` to base quantity `24`.
- Confirmed stock subtraction changes inventory from `100` to `76`.
- Created a paid order through `PaymentService`; order details stored resolved unit id and `quantity_base`.
- Confirmed best-selling statistics counted `24` base units, not `1` display unit.
- Cancelled the order and confirmed inventory returned to `100`.
- Soft-deleted the product after invoice history existed.
- Confirmed product and inventory were hidden with `is_deleted = 1`.
- Confirmed historical order detail still existed after product soft delete.
- Confirmed staff role is recognized as cashier/sales staff and not as manager/admin.

### Phase 1 - Stabilize account administration

- Add an admin-only "create account" flow in the admin portal.
- Let admin choose role during account creation.
- Keep public registration as bootstrap/staff-only, or hide public registration after the first admin exists.
- Add manual test cases for admin, manager, sales staff, and warehouse staff logins.

### Phase 2 - Complete unit of measure behavior

- Confirm schema support for `UNITS`, `PRODUCT_UNITS`, and base unit fields.
- Polish unit controls in product management for non-base units.
- Apply conversion consistently when importing stock.
- Add validation so conversion rates must be positive and one base unit exists per product.

### Phase 3 - Harden warehouse soft delete

- Keep product and inventory rows with `is_deleted = 1`.
- Hide deleted products from sales/search/product lists.
- Preserve deleted product references in historical order details.
- Add clear UI copy: "Hide product" when the product has invoice history.

### Phase 4 - Finish invoices and order lifecycle

- Verify order creation writes `ORDERS`, `ORDER_DETAILS`, payment method, and stock changes in one transaction.
- Verify cancel order logic restores stock exactly once.
- Improve invoice print/export layout if more polish is needed.
- Handle edge cases: insufficient stock, deleted product, missing customer, failed payment method.

### Phase 5 - Finish statistics

- Add date range filters for revenue and best-selling products.
- Exclude cancelled/deleted orders consistently.
- Verify dashboard cards against direct SQL totals.
- Keep statistics hidden from staff roles and available to store managers/admins only.
