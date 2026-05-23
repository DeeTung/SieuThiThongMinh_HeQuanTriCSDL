package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataGenerator {

    public static void main(String[] args) {
        String folderPath = "data";
        String fileName = "products1_1m.csv";

        // Đảm bảo thư mục data tồn tại
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }

        File file = new File(folder, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Viết Header (Khớp với logic Split trong ImportService)
            writer.write("product_id,product_name,base_price,category_id,supplier_id");
            writer.newLine();

            // Danh sách mã giả lập để khớp Foreign Key
            String[] categories = {"CAT001", "CAT002"};
            String[] suppliers = {"SUP001", "SUP002"};

            for (int i = 1; i <= 1000000; i++) {
                String productId = String.format("SP%07d", i); // SP0000001 -> SP1000000
                String productName = "Sản phẩm mẫu số " + i;
                double price = 10000 + (Math.random() * 500000); // Giá từ 10k đến 510k
                String catId = categories[i % 2];
                String supId = suppliers[i % 2];

                // Ghi dòng dữ liệu
                writer.write(String.format("%s,%s,%.2f,%s,%s",
                        productId, productName, price, catId, supId));
                writer.newLine();

                // In tiến độ ra console cho đỡ sốt ruột
                if (i % 100000 == 0) {
                    System.out.println("Đã tạo được: " + i + " dòng...");
                }
            }

            System.out.println("Thành công! File đã được tạo tại: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Lỗi khi tạo file: " + e.getMessage());
        }
    }
}
