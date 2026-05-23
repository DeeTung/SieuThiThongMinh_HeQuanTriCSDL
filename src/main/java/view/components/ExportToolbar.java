package view.components;

import view.ImportProductDialog;
import view.ProductView;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class ExportToolbar extends JPanel {

    private JButton btnExcel, btnPDF, btnImport, btnSearch;
    private JComboBox<String> cbSearch;

    public ExportToolbar(ProductView parent) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        setBackground(Color.WHITE);

        cbSearch = new JComboBox<>();
        cbSearch.setEditable(true);
        cbSearch.setPreferredSize(new Dimension(220, 30));
        add(new JLabel("🔍"));
        add(cbSearch);

        btnSearch = createButton("Tìm", new Color(70, 70, 70));
        btnSearch.setPreferredSize(new Dimension(70, 30));
        add(btnSearch);

        // --- FIX LỖI COMBOBOX: GÕ KHÔNG MẤT CHỮ & HIỆN GỢI Ý NGAY ---
        JTextField txtEditor = (JTextField) cbSearch.getEditor().getEditorComponent();

        // [MỚI THÊM] Nạp sẵn top 15 món ngay lúc vừa mở giao diện lên để tránh bị giật
        SwingUtilities.invokeLater(() -> {
            List<String> defaultSuggestions = business.sql.prod_inventory.ProductsSql.getInstance().getSearchSuggestions("");
            cbSearch.setModel(new DefaultComboBoxModel<>(defaultSuggestions.toArray(new String[0])));
            txtEditor.setText(""); // Xóa text mặc định đi để trả lại ô trống cho user gõ
        });

        // 1. Sự kiện khi CLICK vào dấu mũi tên (Đã Fix lỗi vừa mở đã tắt)
        cbSearch.addPopupMenuListener(new PopupMenuListener() {
            boolean isUpdating = false; // Biến cờ chống vòng lặp vô tận

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (isUpdating) {
                    return;
                }

                if (txtEditor.getText().trim().isEmpty()) {
                    isUpdating = true;
                    SwingUtilities.invokeLater(() -> {
                        List<String> suggestions = business.sql.prod_inventory.ProductsSql.getInstance().getSearchSuggestions("");
                        cbSearch.setModel(new DefaultComboBoxModel<>(suggestions.toArray(new String[0])));
                        txtEditor.setText("");

                        cbSearch.showPopup(); // Ép xổ danh sách lại vì hàm setModel vừa làm đóng nó
                        isUpdating = false;
                    });
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        // 2. Sự kiện khi GÕ CHỮ (Xử lý mượt không làm mất chữ)
        txtEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                // Bỏ qua phím điều hướng
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
                    return;
                }

                // Nhấn Enter thì bấm nút Tìm
                if (key == KeyEvent.VK_ENTER) {
                    cbSearch.hidePopup();
                    btnSearch.doClick();
                    return;
                }

                String text = txtEditor.getText();
                int caretPos = txtEditor.getCaretPosition(); // LƯU LẠI VỊ TRÍ CON TRỎ (Bí quyết chống mất chữ)

                if (text.length() >= 1) {
                    SwingUtilities.invokeLater(() -> {
                        List<String> suggestions = business.sql.prod_inventory.ProductsSql.getInstance().getSearchSuggestions(text);
                        if (!suggestions.isEmpty()) {
                            // Dùng DefaultComboBoxModel để update toàn bộ list
                            cbSearch.setModel(new DefaultComboBoxModel<>(suggestions.toArray(new String[0])));
                            cbSearch.showPopup();
                        } else {
                            cbSearch.hidePopup();
                        }
                        // PHỤC HỒI CHỮ VÀ CON TRỎ CHUỘT NGAY LẬP TỨC
                        txtEditor.setText(text);
                        txtEditor.setCaretPosition(caretPos);
                    });
                }
            }
        });

        // 3. Sự kiện khi DÙNG CHUỘT CHỌN món trong list -> Tự động tìm kiếm
        cbSearch.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                Object selected = cbSearch.getSelectedItem();
                // Phải check xem user đang gõ (popup hiển thị) hay là thực sự click chọn
                if (selected != null && !cbSearch.isPopupVisible()) {
                    btnSearch.doClick();
                }
            }
        });

        // --- CÁC NÚT KHÁC ---
        btnExcel = createButton("📊 Xuất Excel", new Color(29, 111, 66));
        add(btnExcel);

        btnPDF = createButton("📄 Xuất PDF", new Color(211, 47, 47));
        add(btnPDF);

        btnImport = createButton("⚙️ Import Data", new Color(0, 120, 215));
        btnImport.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(parent);
            if (win instanceof JFrame) {
                new ImportProductDialog((JFrame) win, parent).setVisible(true);
            }
        });
        add(btnImport);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public String getSearchText() {
        return ((JTextField) cbSearch.getEditor().getEditorComponent()).getText().trim();
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }
}
