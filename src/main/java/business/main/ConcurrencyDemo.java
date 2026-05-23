//package business.main;
//
//import common.db.DatabaseConnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class ConcurrencyDemo {
//
//    public static void main(String[] args) {
//        System.out.println("============== BAT DAU TEST ================");
//        System.out.println("Kich ban: Non-repeatable Read (Theo timeline chuan)");
//        System.out.println("============================================");
//
//        testDeadlock();
//    }
//
////    public static void testLostUpdate() {
////        // Luong 1: Nghiep vu Ban hang
////        Thread saleThread = new Thread(() -> {
////            try (Connection conn = DatabaseConnection.getConnection()) {
////                conn.setAutoCommit(false);
////
////                // THOI GIAN: 0.0s - Luong 1 doc ton kho
////                PreparedStatement psRead = conn.prepareStatement(
////                        "SELECT quantity FROM INVENTORY WHERE product_id = 'SP0000001' AND store_id = 'ST001'"
////                );
////                ResultSet rs = psRead.executeQuery();
////
////                if (rs.next()) {
////                    int currentQty = rs.getInt("quantity");
////                    // In: "Luồng 1... tồn kho hiện tại là 50"
////                    System.out.println("[0.0s] Luong 1... ton kho hien tai la " + currentQty);
////
////                    // THOI GIAN: tu 0.0s den 3.0s - Cho khach thanh toan
////                    System.out.println("[0.0s - 3.0s] (Dang cho thanh toan...)");
////                    Thread.sleep(3000);
////
////                    // THOI GIAN: 3.0s - Luong 1 ghi de vao DB
////                    PreparedStatement psUpdate = conn.prepareStatement(
////                            "UPDATE INVENTORY SET quantity = ? WHERE product_id = 'SP0000001' AND store_id = 'ST001'"
////                    );
////                    psUpdate.setInt(1, currentQty - 2);
////                    psUpdate.executeUpdate();
////                    conn.commit();
////
////                    // In: "Luồng 1... Ghi đè tồn kho thành 48"
////                    System.out.println("[3.0s] Luong 1... Ghi de ton kho thanh " + (currentQty - 2));
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////
////        // Luong 2: Nghiep vu Nhap kho
////        Thread importThread = new Thread(() -> {
////            try (Connection conn = DatabaseConnection.getConnection()) {
////                conn.setAutoCommit(false);
////
////                // Cho 0.5s de khop moc thoi gian doc cua Luong 2
////                Thread.sleep(500);
////
////                // THOI GIAN: 0.5s - Luong 2 doc ton kho
////                PreparedStatement psRead = conn.prepareStatement(
////                        "SELECT quantity FROM INVENTORY WHERE product_id = 'SP0000001' AND store_id = 'ST001'"
////                );
////                ResultSet rs = psRead.executeQuery();
////
////                if (rs.next()) {
////                    int currentQty = rs.getInt("quantity");
////                    // In: "Luồng 2... tồn kho hiện tại là 50"
////                    System.out.println("[0.5s] Luong 2... ton kho hien tai la " + currentQty);
////
////                    // Cho them 0.1s de khop moc 0.6s
////                    Thread.sleep(100);
////
////                    // THOI GIAN: 0.6s - Luong 2 cap nhat vao DB
////                    PreparedStatement psUpdate = conn.prepareStatement(
////                            "UPDATE INVENTORY SET quantity = ? WHERE product_id = 'SP0000001' AND store_id = 'ST001'"
////                    );
////                    psUpdate.setInt(1, currentQty + 20);
////                    psUpdate.executeUpdate();
////                    conn.commit();
////
////                    // In: "Luồng 2... cập nhật tồn kho thành 70"
////                    System.out.println("[0.6s] Luong 2... cap nhat ton kho thanh " + (currentQty + 20));
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////
////        // Chay ca 2 luong cung mot luc de mo phong timeline
////        saleThread.start();
////        importThread.start();
////    }
//    // __________________________________________________________________________________________________________________________________________
////    public static void testNonRepeatableRead() {
////        // Luong 1: Ke toan kho dang kiem ke ton kho
////        Thread accountantThread = new Thread(() -> {
////            try (Connection conn = DatabaseConnection.getConnection()) {
////                conn.setAutoCommit(false);
////                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
////
////                // THOI GIAN: 0.0s - Luong 1 doc ton kho lan 1
////                PreparedStatement psRead = conn.prepareStatement(
////                        "SELECT quantity FROM INVENTORY WHERE product_id = 'SP0000002' AND store_id = 'ST001'"
////                );
////                ResultSet rs1 = psRead.executeQuery();
////                if (rs1.next()) {
////                    int qty = rs1.getInt("quantity");
////                    System.out.println("[0.0s] Luong 1... Lan 1: Ton kho SP0000002 la " + qty);
////                }
////
////                // THOI GIAN: 0.0s - 3.0s
////                System.out.println("[0.0s - 3.0s] (Dang ghi chep so lieu vao file Excel...)");
////                Thread.sleep(3000);
////
////                // THOI GIAN: 3.0s - Luong 1 doc lai lan 2 (Nhan F5)
////                ResultSet rs2 = psRead.executeQuery();
////                if (rs2.next()) {
////                    int newQty = rs2.getInt("quantity");
////                    System.out.println("[3.0s] Luong 1... Lan 2: Ton kho bi thay doi thanh " + newQty);
////                }
////
////                conn.commit();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////
////        // Luong 2: Thu ngan ban hang
////        Thread saleThread = new Thread(() -> {
////            try (Connection conn = DatabaseConnection.getConnection()) {
////                conn.setAutoCommit(false);
////                Thread.sleep(1000); // Cho 1 giay de Luong 1 doc xong
////
////                // THOI GIAN: 1.0s - Luong 2 ban 5 san pham va COMMIT
////                PreparedStatement psUpdate = conn.prepareStatement(
////                        "UPDATE INVENTORY SET quantity = quantity - 5 WHERE product_id = 'SP0000002' AND store_id = 'ST001'"
////                );
////                psUpdate.executeUpdate();
////                conn.commit();
////
////                System.out.println("[1.0s] Luong 2... Thu ngan da ban 5 san pham va COMMIT");
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////
////        accountantThread.start();
////        saleThread.start();
////    }
//    // ___________________________________________________________________________________________________________________________________________
////    public static void testPhantomRead() {
////        // Luong 1: Quan ly dem so luong san pham CAT001
////        Thread managerThread = new Thread(() -> {
////            try (Connection conn = DatabaseConnection.getConnection()) {
////                conn.setAutoCommit(false);
////                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
////
////                // THOI GIAN: 0.0s - Luong 1 dem lan 1
////                PreparedStatement ps = conn.prepareStatement(
////                        "SELECT COUNT(*) FROM PRODUCTS WHERE category_id = 'CAT001' AND is_deleted = 0"
////                );
////                ResultSet rs1 = ps.executeQuery();
////                if (rs1.next()) {
////                    System.out.println("[0.0s] Luong 1... Lan 1: Tong so san pham CAT001 la " + rs1.getInt(1));
////                }
////
////                // THOI GIAN: 0.0s - 3.0s
////                System.out.println("[0.0s - 3.0s] (Dang chuan bi xuat file bao cao...)");
////                Thread.sleep(3000);
////
////                // THOI GIAN: 3.0s - Luong 1 dem lan 2
////                ResultSet rs2 = ps.executeQuery();
////                if (rs2.next()) {
////                    System.out.println("[3.0s] Luong 1... Lan 2: Bong ma xuat hien, so luong thanh " + rs2.getInt(1));
////                }
////                
////                conn.commit();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////
////        // Luong 2: Nhan vien them San pham moi
////        Thread staffThread = new Thread(() -> {
////            try (Connection conn = DatabaseConnection.getConnection()) {
////                conn.setAutoCommit(false);
////                Thread.sleep(1000);
////
////                // THOI GIAN: 1.0s - Luong 2 INSERT va COMMIT
////                PreparedStatement ps = conn.prepareStatement(
////                        "INSERT INTO PRODUCTS (product_id, product_name, base_price, category_id, supplier_id, is_deleted) " +
////                        "VALUES ('SP_GHOST', 'Mi Hao Hao', 5000, 'CAT001', 'SUP001', 0)"
////                );
////                ps.executeUpdate();
////                conn.commit();
////
////                System.out.println("[1.0s] Luong 2... Da INSERT them 1 san pham moi");
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////
////        managerThread.start();
////        staffThread.start();
////    }
//// ____________________________________________________________________________________
//    public static void testDeadlock() {
//        // Luong 1: Cap nhat SP1 roi moi den SP2
//        Thread thread1 = new Thread(() -> {
//            try (Connection conn = DatabaseConnection.getConnection()) {
//                conn.setAutoCommit(false);
//
//                // THOI GIAN: 0.0s
//                System.out.println("[0.0s] Luong 1... Dang cap nhat va khoa SP0000001");
//                conn.createStatement().executeUpdate(
//                        "UPDATE INVENTORY SET quantity = quantity - 1 WHERE product_id = 'SP0000001' AND store_id = 'ST001'"
//                );
//
//                // Cho 2 giay de xay ra be tac
//                Thread.sleep(2000);
//
//                // THOI GIAN: 2.0s
//                System.out.println("[2.0s] Luong 1... Dang cho de xin khoa SP0000002...");
//                conn.createStatement().executeUpdate(
//                        "UPDATE INVENTORY SET quantity = quantity + 1 WHERE product_id = 'SP0000002' AND store_id = 'ST001'"
//                );
//
//                conn.commit();
//                System.out.println("Luong 1 thanh cong!");
//            } catch (Exception e) {
//                System.err.println("\n[LOI DEADLOCK] Luong 1 bi Oracle huy: " + e.getMessage());
//            }
//        });
//
//        // Luong 2: Cap nhat SP2 roi moi den SP1
//        Thread thread2 = new Thread(() -> {
//            try (Connection conn = DatabaseConnection.getConnection()) {
//                conn.setAutoCommit(false);
//
//                // Cho 0.5s de dam bao Luong 1 chay lenh dau tien truoc
//                Thread.sleep(500);
//
//                // THOI GIAN: 0.5s
//                System.out.println("[0.5s] Luong 2... Dang cap nhat va khoa SP0000002");
//                conn.createStatement().executeUpdate(
//                        "UPDATE INVENTORY SET quantity = quantity - 1 WHERE product_id = 'SP0000002' AND store_id = 'ST001'"
//                );
//
//                // Cho 2 giay
//                Thread.sleep(2000);
//
//                // THOI GIAN: 2.5s
//                System.out.println("[2.5s] Luong 2... Dang cho de xin khoa SP0000001...");
//                conn.createStatement().executeUpdate(
//                        "UPDATE INVENTORY SET quantity = quantity + 1 WHERE product_id = 'SP0000001' AND store_id = 'ST001'"
//                );
//
//                conn.commit();
//                System.out.println("Luong 2 thanh cong!");
//            } catch (Exception e) {
//                System.err.println("\n[LOI DEADLOCK] Luong 2 bi Oracle huy: " + e.getMessage());
//            }
//        });
//
//        thread1.start();
//        thread2.start();
//    }
//}
