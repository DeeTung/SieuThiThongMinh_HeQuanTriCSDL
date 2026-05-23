# Contributing Guide - Hệ Thống Quản Lý Siêu Thị (Smart Supermarket)

## Prerequisites

- Java Development Kit (JDK) >= 21
- Apache Maven >= 3.8
- NetBeans IDE >= 20 (Khuyến nghị sử dụng, để quản lý tốt các file giao diện thiết kế bằng Form Builder `.form`)
- Oracle Database (cấu trúc Schema tương thích có sẵn)
- Git

---

## Git Workflow

### Branch Strategy

```text
main              ← Production (protected)
  │
develop           ← Integration branch (protected)
  │
feature/*         ← Tính năng mới
bugfix/*          ← Sửa lỗi thông thường
hotfix/*          ← Sửa lỗi nóng trên production
```

### Quy trình làm việc

```bash
# 1. Clone repo (lần đầu)
git clone <repo-url>
cd SieuThiOnline_Java

# 2. Luôn bắt đầu từ develop hoặc main mới nhất
git checkout main
git pull origin main

# 3. Tạo branch mới theo quy tắc
git checkout -b feature/ten-tinh-nang
# hoặc: git checkout -b bugfix/ten-loi

# 4. Code & Commit thường xuyên
git add .
git commit -m "feat: add user profile page"

# 5. Push branch lên remote
git push origin feature/ten-tinh-nang

# 6. Tạo Pull Request trên GitHub
#    - Base: main
#    - Compare: feature/ten-tinh-nang

# 7. Sau khi PR được merge, xóa branch ở local
git checkout main
git pull origin main
git branch -d feature/ten-tinh-nang
```

### Commit Message Convention

Format: `type: message`

| Type | Mô tả |
|------|-------|
| `feat` | Thêm tính năng khởi tạo mới |
| `fix` | Sửa các lỗi bug |
| `refactor` | Tổ chức/làm mới logic code, không thay đổi biểu hiện chức năng bên ngoài |
| `docs` | Thêm/sửa Documentation, README, README.md |
| `ui` | Cập nhật các giao diện trên NetBeans / FlatLaf |
| `chore` | Config package `pom.xml`, các file hệ thống |

Ví dụ:
```text
feat: add product management service
fix: resolve database connection leak
ui: update settings view layout
chore: update maven dependencies
```

### Code Review

- Mỗi PR cần ít nhất 1 approval.
- Code compile thành công qua Maven (`mvn compile`).
- Vui lòng resolve tất cả comment trước khi merged.

---

## Getting Started

```bash
# 1. Tải các dependencies của Maven và Build project
mvn clean install

# 2. Chạy ứng dụng (Swing Desktop)
mvn exec:java -Dexec.mainClass="business.main.SieuThiOnline"
```

---

## Project Structure

Tuân thủ kiến trúc mô phỏng 3 lớp:

```text
SieuThiOnline_Java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── business/     # Tầng Xử lý Logic & DB Actions
│   │   │   │   ├── main/       # Entry point chạy app
│   │   │   │   ├── service/    # Logic dịch vụ (PaymentService, LoginService...)
│   │   │   │   └── sql/        # Repository SQL Queries thao tác với OracleDB
│   │   │   ├── common/       # Utilities, Security, Export báo cáo (iText, POI)
│   │   │   ├── model/        # Data Models được chia theo domain (account, product,..)
│   │   │   └── view/         # Tầng giao diện UI (Swing JFrame, JPanel, Component)
│   │   │       ├── components/ # Các module UI tái sử dụng (Sidebar, MenuItem...)
│   │   └── resources/        # Ảnh, icon, file config .properties
├── pom.xml                   # Cấu hình Dependency (Maven)
├── nbactions.xml             # Thiết lập hành động tự động từ NetBeans
└── AGENTS.md                 # Chỉ dẫn nguyên tắc cho AI / Dev
```

---

## Development Workflow

### Thêm một Logic Dịch Vụ Mới (Service & Model)

1. Tạo Model/Entity trong package `src/main/java/model/`:
```java
// model/promotion/Promotion.java
package model.promotion;

public class Promotion {
    private String promoId;
    private double discountRate;
    // Getters & Setters ...
}
```

2. Viết Data Access (CRUD) thông qua JDBC vào Oracle:
Tạo class trong package `business/sql/`. Đặc biệt nhớ đóng Connection.
```java
// business/sql/promotion/PromotionSql.java
package business.sql.promotion;
import common.database.DatabaseConnection;

public class PromotionSql {
    // public List<Promotion> getAllPromotions() { ... }
}
```

3. Khởi tạo Service Wrapper điều phối (nếu cần xử lý phức tạp):
```java
// business/service/PromotionService.java
package business.service;

public class PromotionService {
    // public static boolean applyDiscount(String type) { ... }
}
```

### Thêm một Màn Hình/Tính Năng Giao Diện Mới

- **Quan Trọng**: Giao diện của dự án này đang được vận hành một phần bằng **NetBeans GUI Builder**, vì thế **Không chỉnh sửa thủ công** trong khối `//GEN-BEGIN:initComponents` của các class ở package `view`.  
- Nếu muốn tùy biến bằng code, hãy viết vào phương thức `initUI()` hoặc hàm khởi tạo (Constructor).
- Luôn ưu tiên dùng **FlatLaf** Colors Theme (Ví dụ: `UIManager.getColor("Component.accentColor")`) thay cho mã màu cứng (hardcode color) để tương thích Dark/Light Theme.

---

## Code Style & Convention

- Áp dụng Style chuẩn của **Java (CamelCase)**: tên Class `EmployeeView`, tên biến `employeeName`.
- Các Element trong View nên được định danh kèm theo Type phía trước: `txtUsername`, `btnLogin`, `lblStatus`, `tblProducts`.
- Chỉ đưa vào các thư viện (`dependencies` qua `pom.xml`) khi thực sự cần thiết, phòng tránh nặng dung lượng project.

---

## Commands Reference

| Lệnh | Chức năng (trên Termial) |
|---------|-------------|
| `mvn clean` | Dọn folder `target/` |
| `mvn compile` | Biên dịch các class Java kiểm tra lỗi Text/Cú pháp |
| `mvn package` | Đóng gói ra file `.jar` có thể thực thi |
| `mvn exec:java -Dexec.mainClass="business.main.SieuThiOnline"` | Build và Chạy ứng dụng Desktop |

---

## Môi trường Thử Nghiệm Nội Bộ (Test Accounts)

Tạm thời bạn dùng các tài khoản dưới đây để test quy trình chức năng ứng dụng. (Chế độ `Dev Mode` hiện đang bật tại `LoginService`).

| Tài khoản / UserID | Mật khẩu (Nếu yêu cầu) | Chức danh |
|-------|----------|------|
| admin | admin123 | Quản trị viên (Admin) |
| staff | staff123 | Nhân viên (Staff) |
