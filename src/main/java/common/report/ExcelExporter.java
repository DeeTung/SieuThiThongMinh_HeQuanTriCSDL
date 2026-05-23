package common.report;

import model.product.Inventory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    public static void exportProducts(List<Map<String, Object>> products, String filePath) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sản Phẩm");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Mã SP", "Tên Sản Phẩm", "Giá", "Số Lượng"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Map<String, Object> product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(product.get("productId")));
                row.createCell(1).setCellValue(String.valueOf(product.get("productName")));
                row.createCell(2).setCellValue(parseDouble(product.get("price")));
                row.createCell(3).setCellValue(parseInt(product.get("quantity")));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    // Dùng cho ProductView (list map)
    public static void exportInventoryFromMap(List<Map<String, Object>> productList, String filePath) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventory");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Mã sản phẩm");
            header.createCell(1).setCellValue("Tên sản phẩm");
            header.createCell(2).setCellValue("Giá");
            header.createCell(3).setCellValue("Số lượng");

            int rowNum = 1;
            for (Map<String, Object> p : productList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(p.get("productId")));
                row.createCell(1).setCellValue(String.valueOf(p.get("productName")));
                row.createCell(2).setCellValue(parseDouble(p.get("price")));
                row.createCell(3).setCellValue(parseInt(p.get("quantity")));
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    // Dùng cho MAIN (List<Inventory>) ✅
    public static void exportInventory(List<Inventory> dsTonKho, String filePath) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tồn Kho");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Mã sản phẩm");
            header.createCell(1).setCellValue("Mã cửa hàng");
            header.createCell(2).setCellValue("Số lượng");
            header.createCell(3).setCellValue("Đơn vị");
            header.createCell(4).setCellValue("Cập nhật lần cuối");

            int rowNum = 1;
            for (Inventory inv : dsTonKho) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(nullToEmpty(inv.getProductId()));
                row.createCell(1).setCellValue(nullToEmpty(inv.getStoreId()));
                row.createCell(2).setCellValue(inv.getQuantity());
                row.createCell(3).setCellValue(nullToEmpty(inv.getUnit()));

                if (inv.getLastUpdated() != null) {
                    row.createCell(4).setCellValue(inv.getLastUpdated().toString());
                } else {
                    row.createCell(4).setCellValue("");
                }
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private static String nullToEmpty(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    private static double parseDouble(Object value) {
        if (value == null) return 0;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private static int parseInt(Object value) {
        if (value == null) return 0;
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }
}