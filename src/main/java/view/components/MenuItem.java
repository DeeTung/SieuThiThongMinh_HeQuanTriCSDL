package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuItem extends JPanel {
    private final JLabel lblText;
    private final JLabel lblIcon;
    private boolean active = false;
    private boolean isFramed = false;
    
    private final Color normalBackground = new Color(255, 255, 255);
    private final Color hoverBackground = new Color(245, 245, 247);
    private final Color activeBackground = new Color(236, 242, 255);
    private final Color activeBorderColor = new Color(41, 98, 255);
    private final Color textNormalColor = new Color(80, 80, 80);
    private final Color textActiveColor = new Color(20, 20, 20);

    public MenuItem(final String text, final Runnable onClick) {
        setLayout(new BorderLayout());
        setBackground(normalBackground);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(240, 45));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblText.setForeground(textNormalColor);
        
        // Icon space placeholder (since we don't have actual icons yet, we'll use a small empty border or a colored dot)
        lblIcon = new JLabel(" \u2022 "); // simple dot icon for placeholder
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblIcon.setForeground(Color.LIGHT_GRAY);
        lblIcon.setPreferredSize(new Dimension(30, 45));
        
        add(lblIcon, BorderLayout.WEST);
        add(lblText, BorderLayout.CENTER);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    setBackground(hoverBackground);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    setBackground(normalBackground);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            setBackground(activeBackground);
            lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblText.setForeground(textActiveColor);
        } else {
            setBackground(normalBackground);
            lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblText.setForeground(textNormalColor);
        }
        repaint();
    }
    
    public void setFramed(boolean framed) {
        this.isFramed = framed;
        setOpaque(false); // background is drawn manually
        if (framed) {
            lblIcon.setVisible(false); // Xóa dấu chấm
            lblText.setForeground(Color.WHITE); // Chữ trắng
            lblText.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Chữ in đậm
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            setPreferredSize(new Dimension(220, 45));
        } else {
            lblIcon.setVisible(true);
            lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblText.setForeground(textNormalColor);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isFramed) {
             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             
             // Draw background (màu đỏ fill nền)
             g2.setColor(new Color(220, 53, 69));
             g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
             
             // Draw border (viền đậm hơn hoặc cùng màu)
             g2.setColor(new Color(200, 35, 51));
             g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
             
             g2.dispose();
        } else {
             super.paintComponent(g);
        }
        
        if (active && !isFramed) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(activeBorderColor);
            // Draw left border indicator
            g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
            g2.dispose();
        }
    }
}
