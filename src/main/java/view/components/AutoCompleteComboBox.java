package view.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * ============================================================
 *  AutoCompleteComboBox<T>
 *  Class dùng chung cho toàn bộ dự án Siêu Thị Thông Minh.
 *
 *  Sử dụng:
 *    // Sản phẩm
 *    AutoCompleteComboBox<SanPham> cbSanPham =
 *        new AutoCompleteComboBox<>(danhSachSanPham, SanPham::getTenSanPham);
 *
 *    // Khách hàng
 *    AutoCompleteComboBox<KhachHang> cbKhachHang =
 *        new AutoCompleteComboBox<>(danhSachKhachHang, KhachHang::getTenKhachHang);
 *
 *    // Nhân viên
 *    AutoCompleteComboBox<NhanVien> cbNhanVien =
 *        new AutoCompleteComboBox<>(danhSachNhanVien, NhanVien::getTenNhanVien);
 *
 *  Lấy giá trị được chọn:
 *    T selectedItem = cbSanPham.getSelectedItem();
 *
 *  Lắng nghe sự kiện chọn:
 *    cbSanPham.addSelectionListener(item -> System.out.println(item.getTenSanPham()));
 * ============================================================
 */
public class AutoCompleteComboBox<T> extends JPanel {

    // ── Core fields ──────────────────────────────────────────
    private final List<T> allItems;                    // Toàn bộ dữ liệu gốc
    private final Function<T, String> displayMapper;   // Hàm lấy text hiển thị
    private List<T> filteredItems;                     // Kết quả sau khi filter

    // ── UI Components ─────────────────────────────────────────
    private final JTextField textField;
    private final JPopupMenu popupMenu;
    private final JList<String> suggestionList;
    private final DefaultListModel<String> listModel;
    private final JScrollPane scrollPane;

    // ── State ─────────────────────────────────────────────────
    private T selectedItem = null;
    private boolean isSelecting = false;   // Tránh vòng lặp khi set text
    private boolean isUpdating = false;

    // ── Listeners ─────────────────────────────────────────────
    private final List<SelectionListener<T>> selectionListeners = new ArrayList<>();

    // ── Style constants ───────────────────────────────────────
    private static final Color COLOR_BG          = new Color(255, 255, 255);
    private static final Color COLOR_BORDER      = new Color(200, 210, 220);
    private static final Color COLOR_BORDER_FOCUS = new Color(66, 133, 244);
    private static final Color COLOR_POPUP_BG    = new Color(255, 255, 255);
    private static final Color COLOR_HOVER       = new Color(232, 240, 254);
    private static final Color COLOR_SELECT      = new Color(210, 227, 252);
    private static final Color COLOR_TEXT        = new Color(30,  40,  50);
    private static final Color COLOR_PLACEHOLDER = new Color(160, 170, 180);
    private static final Font  FONT_INPUT        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_LIST         = new Font("Segoe UI", Font.PLAIN, 13);
    private static final int   POPUP_MAX_ROWS    = 8;
    private static final int   ROW_HEIGHT        = 30;

    // ── Placeholder ───────────────────────────────────────────
    private String placeholder = "Tìm kiếm...";

    // ──────────────────────────────────────────────────────────
    //  CONSTRUCTORS
    // ──────────────────────────────────────────────────────────

    /**
     * Constructor cơ bản.
     * @param items         Danh sách đối tượng (SanPham, KhachHang, NhanVien, ...)
     * @param displayMapper Hàm lấy chuỗi hiển thị từ đối tượng, vd: SanPham::getTenSanPham
     */
    public AutoCompleteComboBox(List<T> items, Function<T, String> displayMapper) {
        this.allItems      = new ArrayList<>(items);
        this.displayMapper = displayMapper;
        this.filteredItems = new ArrayList<>(items);

        // ── Layout ────────────────────────────────────────────
        setLayout(new BorderLayout());
        setOpaque(false);

        // ── TextField ─────────────────────────────────────────
        textField = buildTextField();
        add(textField, BorderLayout.CENTER);

        // ── Popup & List ──────────────────────────────────────
        listModel      = new DefaultListModel<>();
        suggestionList = buildSuggestionList();
        scrollPane     = buildScrollPane();
        popupMenu      = buildPopupMenu();

        // ── Wire events ───────────────────────────────────────
        attachDocumentListener();
        attachKeyboardNavigation();
        attachFocusHandlers();
        attachMouseHandler();
    }

    /**
     * Constructor với placeholder tuỳ chỉnh.
     */
    public AutoCompleteComboBox(List<T> items, Function<T, String> displayMapper, String placeholder) {
        this(items, displayMapper);
        this.placeholder = placeholder;
        textField.putClientProperty("placeholder", placeholder);
        textField.repaint();
    }

    // ──────────────────────────────────────────────────────────
    //  BUILD UI HELPERS
    // ──────────────────────────────────────────────────────────

    private JTextField buildTextField() {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2.setColor(COLOR_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                // Placeholder
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setColor(COLOR_PLACEHOLDER);
                    g2.setFont(FONT_INPUT.deriveFont(Font.ITALIC));
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, getInsets().left + 4, y);
                }
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? COLOR_BORDER_FOCUS : COLOR_BORDER);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.8f : 1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
            }
        };
        tf.setFont(FONT_INPUT);
        tf.setForeground(COLOR_TEXT);
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 28)); // 28px room for clear btn
        tf.setPreferredSize(new Dimension(200, 34));
        return tf;
    }

    private JList<String> buildSuggestionList() {
        JList<String> list = new JList<>(listModel);
        list.setFont(FONT_LIST);
        list.setForeground(COLOR_TEXT);
        list.setBackground(COLOR_POPUP_BG);
        list.setSelectionBackground(COLOR_SELECT);
        list.setSelectionForeground(COLOR_TEXT);
        list.setFixedCellHeight(ROW_HEIGHT);
        list.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        list.setFocusable(false);

        // Custom cell renderer: highlight matching text
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object val,
                    int idx, boolean isSel, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        l, val, idx, isSel, cellHasFocus);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                lbl.setBackground(isSel ? COLOR_SELECT : COLOR_POPUP_BG);
                lbl.setForeground(COLOR_TEXT);

                // Bold matching part
                String keyword = textField.getText().trim().toLowerCase();
                String text = val.toString();
                if (!keyword.isEmpty()) {
                    int start = text.toLowerCase().indexOf(keyword);
                    if (start >= 0) {
                        String before = escapeHtml(text.substring(0, start));
                        String match  = escapeHtml(text.substring(start, start + keyword.length()));
                        String after  = escapeHtml(text.substring(start + keyword.length()));
                        lbl.setText("<html>" + before
                                + "<b style='color:#1a73e8'>" + match + "</b>"
                                + after + "</html>");
                    }
                }
                return lbl;
            }
        });

        // Hover effect
        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int idx = list.locationToIndex(e.getPoint());
                if (idx >= 0) list.setSelectedIndex(idx);
            }
        });

        return list;
    }

    private JScrollPane buildScrollPane() {
        JScrollPane sp = new JScrollPane(suggestionList);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(ROW_HEIGHT);
        return sp;
    }

    private JPopupMenu buildPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new BorderLayout());
        popup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(2, 0, 2, 0)));
        popup.setBackground(COLOR_POPUP_BG);
        popup.add(scrollPane, BorderLayout.CENTER);
        popup.setFocusable(false);
        return popup;
    }

    // ──────────────────────────────────────────────────────────
    //  EVENT WIRING
    // ──────────────────────────────────────────────────────────

    /** Lắng nghe thay đổi text → filter gợi ý */
    private void attachDocumentListener() {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void removeUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onTextChanged(); }
        });
    }

    /** Điều hướng bằng bàn phím: ↑ ↓ Enter Escape */
    private void attachKeyboardNavigation() {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if (!popupMenu.isVisible()) {
                            showPopup();
                        } else {
                            int next = Math.min(suggestionList.getSelectedIndex() + 1,
                                                listModel.getSize() - 1);
                            suggestionList.setSelectedIndex(next);
                            suggestionList.ensureIndexIsVisible(next);
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_UP:
                        if (popupMenu.isVisible()) {
                            int prev = Math.max(suggestionList.getSelectedIndex() - 1, 0);
                            suggestionList.setSelectedIndex(prev);
                            suggestionList.ensureIndexIsVisible(prev);
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_ENTER:
                        if (popupMenu.isVisible()) {
                            int idx = suggestionList.getSelectedIndex();
                            if (idx >= 0) selectItemAt(idx);
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_ESCAPE:
                        popupMenu.setVisible(false);
                        e.consume();
                        break;

                    case KeyEvent.VK_TAB:
                        if (popupMenu.isVisible() && suggestionList.getSelectedIndex() >= 0) {
                            selectItemAt(suggestionList.getSelectedIndex());
                        }
                        popupMenu.setVisible(false);
                        break;
                }
            }
        });

        // Click vào item trong danh sách
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = suggestionList.locationToIndex(e.getPoint());
                if (idx >= 0) selectItemAt(idx);
            }
        });
    }

    private void attachFocusHandlers() {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.repaint(); // re-draw border
                // Nếu có text thì show popup
                if (!textField.getText().isEmpty()) showPopup();
            }
            @Override
            public void focusLost(FocusEvent e) {
                textField.repaint();
                // Delay để click popup kịp xử lý
                SwingUtilities.invokeLater(() -> {
                    if (!suggestionList.isShowing()) {
                        popupMenu.setVisible(false);
                    }
                });
            }
        });
    }

    /** Thêm nút X xoá nhanh */
    private void attachMouseHandler() {
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Click vào vùng icon X (bên phải)
                int x = e.getX();
                int w = textField.getWidth();
                if (x > w - 26 && !textField.getText().isEmpty()) {
                    clearSelection();
                }
            }
        });

        // Vẽ nút X
        textField.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { textField.repaint(); }
        });

        // Override paintComponent để vẽ icon X/▼
        JTextField tf = textField;
        tf.addPropertyChangeListener(e -> tf.repaint());
    }

    // ──────────────────────────────────────────────────────────
    //  CORE LOGIC
    // ──────────────────────────────────────────────────────────

    private void onTextChanged() {
        if (isSelecting) return;
        selectedItem = null;  // Xoá lựa chọn khi gõ lại
        filterAndShowPopup();
    }
 
    private void filterAndShowPopup() {
        String keyword = textField.getText().trim().toLowerCase();
        filteredItems.clear();
        listModel.clear(); 

        for (T item : allItems) {
            String display = displayMapper.apply(item).toLowerCase();
            if (keyword.isEmpty() || display.contains(keyword)) {
                filteredItems.add(item);
                listModel.addElement(displayMapper.apply(item));
            }
        }

        if (listModel.isEmpty()) {
            popupMenu.setVisible(false);
        } else {
            showPopup();
            if (!keyword.isEmpty()) {
                // Auto-select first match
                suggestionList.setSelectedIndex(0);
            } else {
                suggestionList.clearSelection();
            }
        }
    }

    private void showPopup() {
        if (listModel.isEmpty()) return;

        // Tính chiều cao popup: tối đa POPUP_MAX_ROWS hàng
        int rows = Math.min(listModel.getSize(), POPUP_MAX_ROWS);
        int popupHeight = rows * ROW_HEIGHT + 12;
        scrollPane.setPreferredSize(new Dimension(textField.getWidth(), popupHeight));
        popupMenu.setPreferredSize(new Dimension(textField.getWidth(), popupHeight + 4));

        if (!popupMenu.isVisible()) {
            popupMenu.show(textField, 0, textField.getHeight() + 2);
            textField.requestFocusInWindow();
        } else {
            popupMenu.pack();
        }
    }

    private void selectItemAt(int index) {
        if (index < 0 || index >= filteredItems.size()) return;
        isSelecting = true;
        selectedItem = filteredItems.get(index);
        textField.setText(displayMapper.apply(selectedItem));
        textField.setCaretPosition(textField.getText().length());
        popupMenu.setVisible(false);
        isSelecting = false;

        // Thông báo listeners
        for (SelectionListener<T> listener : selectionListeners) {
            listener.onSelected(selectedItem);
        }
    }

    // ──────────────────────────────────────────────────────────
    //  PUBLIC API
    // ──────────────────────────────────────────────────────────

    /**
     * Trả về đối tượng T đang được chọn, hoặc null nếu chưa chọn.
     */
    public T getSelectedItem() {
        return selectedItem;
    }

    /**
     * Set giá trị được chọn theo chương trình.
     */
    public void setSelectedItem(T item) {
        isSelecting = true;
        selectedItem = item;
        textField.setText(item != null ? displayMapper.apply(item) : "");
        isSelecting = false;
    }

    /**
     * Cập nhật toàn bộ danh sách dữ liệu (vd: sau khi load từ DB).
     */
    public void setItems(List<T> items) {
        allItems.clear();
        allItems.addAll(items);
        filteredItems = new ArrayList<>(items);
        listModel.clear();
    }

    /**
     * Thêm một đối tượng mới vào danh sách.
     */
    public void addItem(T item) {
        allItems.add(item);
    }

    /**
     * Xoá lựa chọn hiện tại và xoá trắng ô nhập.
     */
    public void clearSelection() {
        isSelecting = true;
        selectedItem = null;
        textField.setText("");
        popupMenu.setVisible(false);
        isSelecting = false;
    }

    /**
     * Set placeholder text.
     */
    public void setPlaceholder(String text) {
        this.placeholder = text;
        textField.repaint();
    }

    /**
     * Set enabled/disabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        if (!enabled) popupMenu.setVisible(false);
    }

    /**
     * Đăng ký lắng nghe sự kiện khi người dùng chọn một item.
     * Ví dụ: cb.addSelectionListener(sp -> lblGia.setText(sp.getGia()+""));
     */
    public void addSelectionListener(SelectionListener<T> listener) {
        selectionListeners.add(listener);
    }

    /**
     * Xoá tất cả listeners.
     */
    public void removeAllSelectionListeners() {
        selectionListeners.clear();
    }

    /**
     * Trả về JTextField bên trong (để tuỳ chỉnh thêm nếu cần).
     */
    public JTextField getTextField() {
        return textField;
    }

    // ──────────────────────────────────────────────────────────
    //  FUNCTIONAL INTERFACE
    // ──────────────────────────────────────────────────────────

    /**
     * Interface lắng nghe sự kiện chọn item.
     * Dùng lambda hoặc anonymous class.
     */
    @FunctionalInterface
    public interface SelectionListener<T> {
        void onSelected(T item);
    }

    // ──────────────────────────────────────────────────────────
    //  UTILITY
    // ──────────────────────────────────────────────────────────

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}