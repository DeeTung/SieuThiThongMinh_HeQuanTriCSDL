package presentation.view.admin;

import business.sql.dbms.DbmsFunctionDemoSql;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;

/**
 * Panel demo Function ở mức Admin.
 *
 * Mục tiêu demo: - Trước khi ứng dụng Function: Java tự tính bằng SQL rời rạc.
 * - Sau khi ứng dụng Function: Java gọi FUNC_GET_FINAL_SYSTEM_REVENUE trong
 * Oracle.
 */
public class DbmsFunctionDemoPanel extends JPanel {

    private final JTextField txtFromDate = new JTextField("2026-05-01", 12);
    private final JTextField txtToDate = new JTextField("2026-05-31", 12);

    private final JLabel lblFunctionStatus = new JLabel("Chưa kiểm tra");
    private final JLabel lblWithoutFunction = new JLabel("-");
    private final JLabel lblWithFunction = new JLabel("-");
    private final JLabel lblCompare = new JLabel("-");

    private final JTextArea txtLog = new JTextArea();

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.##");

    public DbmsFunctionDemoPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildResultPanel(), BorderLayout.CENTER);
        add(buildLogPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel root = new JPanel(new BorderLayout(8, 8));

        JLabel title = new JLabel("Demo Function - Tổng hợp doanh thu cuối cùng toàn hệ thống");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel desc = new JLabel(
                "<html>Demo 2 mức: <b>chưa ứng dụng Function</b> và <b>đã ứng dụng Function trong Oracle</b>.</html>"
        );

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.add(new JLabel("Từ ngày yyyy-mm-dd:"));
        filterPanel.add(txtFromDate);
        filterPanel.add(new JLabel("Đến ngày yyyy-mm-dd:"));
        filterPanel.add(txtToDate);

        JButton btnCheckFunction = new JButton("Kiểm tra Function");
        JButton btnWithoutFunction = new JButton("Demo chưa dùng Function");
        JButton btnWithFunction = new JButton("Demo đã dùng Function");
        JButton btnCompare = new JButton("So sánh 2 mức");

        btnCheckFunction.addActionListener(e -> checkFunctionStatus());
        btnWithoutFunction.addActionListener(e -> runWithoutFunction());
        btnWithFunction.addActionListener(e -> runWithFunction());
        btnCompare.addActionListener(e -> runCompare());

        filterPanel.add(btnCheckFunction);
        filterPanel.add(btnWithoutFunction);
        filterPanel.add(btnWithFunction);
        filterPanel.add(btnCompare);

        JPanel top = new JPanel(new GridLayout(2, 1, 4, 4));
        top.add(title);
        top.add(desc);

        root.add(top, BorderLayout.NORTH);
        root.add(filterPanel, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 12));
        panel.setBorder(BorderFactory.createTitledBorder("Kết quả demo"));

        panel.add(new JLabel("Trạng thái Function trong Oracle:"));
        panel.add(lblFunctionStatus);

        panel.add(new JLabel("Mức 1 - Java tự tính, chưa dùng Function:"));
        panel.add(lblWithoutFunction);

        panel.add(new JLabel("Mức 2 - Gọi FUNC_GET_FINAL_SYSTEM_REVENUE:"));
        panel.add(lblWithFunction);

        panel.add(new JLabel("Đối chiếu kết quả:"));
        panel.add(lblCompare);

        return panel;
    }

    private JScrollPane buildLogPanel() {
        txtLog.setRows(12);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        return new JScrollPane(txtLog);
    }

    private void checkFunctionStatus() {
        appendLog("=== Kiểm tra trạng thái Function ===");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return DbmsFunctionDemoSql.getInstance().getFunctionStatus();
            }

            @Override
            protected void done() {
                try {
                    String status = get();
                    lblFunctionStatus.setText(status);

                    if ("VALID".equalsIgnoreCase(status)) {
                        appendLog("[OK] Function FUNC_GET_FINAL_SYSTEM_REVENUE đang VALID.");
                    } else if ("NOT_FOUND".equalsIgnoreCase(status)) {
                        appendLog("[BUG] Function chưa tồn tại trong Oracle.");
                    } else {
                        appendLog("[WARN] Function tồn tại nhưng trạng thái: " + status);
                    }
                } catch (Exception ex) {
                    lblFunctionStatus.setText("ERROR");
                    appendLog("[ERROR] Không kiểm tra được function: " + ex.getMessage());
                    showError(ex);
                }
            }
        }.execute();
    }

    private void runWithoutFunction() {
        appendLog("=== Mức 1: Chưa ứng dụng Function ===");
        appendLog("Java tự chạy SQL để tính tổng bán hàng và tổng nhập hàng.");

        DateRange range = readDateRange();
        if (range == null) {
            return;
        }

        new SwingWorker<BigDecimal, Void>() {
            @Override
            protected BigDecimal doInBackground() throws Exception {
                return DbmsFunctionDemoSql.getInstance()
                        .calculateFinalRevenueWithoutFunction(range.fromDate, range.toDate);
            }

            @Override
            protected void done() {
                try {
                    BigDecimal result = get();
                    lblWithoutFunction.setText(formatMoney(result));
                    appendLog("[Mức 1] Kết quả Java tự tính = " + formatMoney(result));
                    appendLog("[Vấn đề] Logic SQL nằm ở Java, dễ bị lặp và khó bảo trì.");
                } catch (Exception ex) {
                    lblWithoutFunction.setText("ERROR");
                    appendLog("[ERROR] Demo chưa dùng Function bị lỗi: " + ex.getMessage());
                    showError(ex);
                }
            }
        }.execute();
    }

    private void runWithFunction() {
        appendLog("=== Mức 2: Đã ứng dụng Function ===");
        appendLog("Java gọi trực tiếp FUNC_GET_FINAL_SYSTEM_REVENUE trong Oracle.");

        DateRange range = readDateRange();
        if (range == null) {
            return;
        }

        new SwingWorker<BigDecimal, Void>() {
            @Override
            protected BigDecimal doInBackground() throws Exception {
                return DbmsFunctionDemoSql.getInstance()
                        .calculateFinalRevenueWithFunction(range.fromDate, range.toDate);
            }

            @Override
            protected void done() {
                try {
                    BigDecimal result = get();
                    lblWithFunction.setText(formatMoney(result));

                    if (BigDecimal.valueOf(-1).compareTo(result) == 0) {
                        appendLog("[BUG] Function trả về -1. Có thể function bị lỗi hoặc chưa tạo đúng trong Oracle.");
                    } else {
                        appendLog("[Mức 2] Kết quả từ Function Oracle = " + formatMoney(result));
                        appendLog("[OK] Logic tính doanh thu đã được tập trung trong database.");
                    }
                } catch (Exception ex) {
                    lblWithFunction.setText("ERROR");
                    appendLog("[ERROR] Demo đã dùng Function bị lỗi: " + ex.getMessage());
                    showError(ex);
                }
            }
        }.execute();
    }

    private void runCompare() {
        appendLog("=== So sánh 2 mức demo ===");

        DateRange range = readDateRange();
        if (range == null) {
            return;
        }

        new SwingWorker<CompareResult, Void>() {
            @Override
            protected CompareResult doInBackground() throws Exception {
                DbmsFunctionDemoSql dao = DbmsFunctionDemoSql.getInstance();

                BigDecimal withoutFunction = dao.calculateFinalRevenueWithoutFunction(range.fromDate, range.toDate);
                BigDecimal withFunction = dao.calculateFinalRevenueWithFunction(range.fromDate, range.toDate);

                return new CompareResult(withoutFunction, withFunction);
            }

            @Override
            protected void done() {
                try {
                    CompareResult result = get();

                    lblWithoutFunction.setText(formatMoney(result.withoutFunction));
                    lblWithFunction.setText(formatMoney(result.withFunction));

                    if (result.withoutFunction.compareTo(result.withFunction) == 0) {
                        lblCompare.setText("Khớp");
                        appendLog("[OK] Hai mức cho cùng kết quả.");
                        appendLog("     Java tự tính      = " + formatMoney(result.withoutFunction));
                        appendLog("     Function Oracle   = " + formatMoney(result.withFunction));
                    } else {
                        lblCompare.setText("Không khớp");
                        appendLog("[BUG] Hai mức đang lệch kết quả.");
                        appendLog("      Java tự tính     = " + formatMoney(result.withoutFunction));
                        appendLog("      Function Oracle  = " + formatMoney(result.withFunction));
                        appendLog("      Cần kiểm tra lại điều kiện status/ngày/xóa mềm.");
                    }
                } catch (Exception ex) {
                    lblCompare.setText("ERROR");
                    appendLog("[ERROR] So sánh thất bại: " + ex.getMessage());
                    showError(ex);
                }
            }
        }.execute();
    }

    private DateRange readDateRange() {
        try {
            String fromText = txtFromDate.getText().trim();
            String toText = txtToDate.getText().trim();

            Date fromDate = fromText.isEmpty() ? null : Date.valueOf(fromText);
            Date toDate = toText.isEmpty() ? null : Date.valueOf(toText);

            if (fromDate != null && toDate != null && fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ngày bắt đầu không được lớn hơn ngày kết thúc.",
                        "Dữ liệu không hợp lệ",
                        JOptionPane.WARNING_MESSAGE
                );
                return null;
            }

            return new DateRange(fromDate, toDate);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ngày phải nhập theo định dạng yyyy-mm-dd. Ví dụ: 2026-05-01",
                    "Sai định dạng ngày",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return moneyFormat.format(value) + " VNĐ";
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(message + System.lineSeparator());
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Lỗi demo Function",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class DateRange {

        private final Date fromDate;
        private final Date toDate;

        private DateRange(Date fromDate, Date toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }
    }

    private static class CompareResult {

        private final BigDecimal withoutFunction;
        private final BigDecimal withFunction;

        private CompareResult(BigDecimal withoutFunction, BigDecimal withFunction) {
            this.withoutFunction = withoutFunction;
            this.withFunction = withFunction;
        }
    }
}
