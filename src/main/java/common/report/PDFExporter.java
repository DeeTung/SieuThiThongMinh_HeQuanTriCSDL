package common.report;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;

import java.util.List;
import java.util.Map;

/**
 * Lớp hỗ trợ xuất báo cáo định dạng PDF
 * Hỗ trợ hiển thị Tiếng Việt và định dạng bảng chuyên nghiệp.
 */
public class PDFExporter {

    public static void exportProducts(List<Map<String, Object>> products, String filePath) throws Exception {
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // 1. Cấu hình Font tiếng Việt
            String fontPath = "C:\\Windows\\Fonts\\Arial.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            document.setFont(font);

            // 2. Thêm tiêu đề báo cáo
            Paragraph title = new Paragraph("DANH SÁCH SẢN PHẨM")
                    .setFont(font)
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(title);

            // 3. Khởi tạo bảng dữ liệu (4 cột)
            float[] columnWidths = {100, 150, 80, 80};
            Table table = new Table(UnitValue.createPointArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // 4. Thêm hàng header
            String[] headers = {"Mã SP", "Tên Sản Phẩm", "Giá", "Số Lượng"};
            for (String header : headers) {
                Cell cell = new Cell();
                cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                cell.add(new Paragraph(header).setFont(font).setBold());
                cell.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cell);
            }

            // 5. Đổ dữ liệu từ danh sách
            if (products == null || products.isEmpty()) {
                document.add(new Paragraph("Không có dữ liệu sản phẩm để hiển thị.")
                        .setFont(font));
            } else {
                for (Map<String, Object> product : products) {
                    table.addCell(new Cell().add(new Paragraph(
                            product.get("productId").toString()).setFont(font)));
                    
                    table.addCell(new Cell().add(new Paragraph(
                            product.get("productName").toString()).setFont(font)));
                    
                    table.addCell(new Cell().add(new Paragraph(
                            product.get("price").toString()).setFont(font))
                            .setTextAlignment(TextAlignment.RIGHT));
                    
                    table.addCell(new Cell().add(new Paragraph(
                            product.get("quantity").toString()).setFont(font))
                            .setTextAlignment(TextAlignment.CENTER));
                }
            }

            // 6. Thêm bảng vào tài liệu
            document.add(table);

            // 7. Thêm ngày xuất báo cáo
            document.add(new Paragraph("\nNgày xuất báo cáo: " + new java.util.Date().toString())
                    .setFont(font)
                    .setItalic()
                    .setFontSize(10));

            System.out.println("✅ Đã xuất PDF thành công tại: " + filePath);

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xuất PDF: " + e.getMessage());
            throw e;
        }
    }
    
}