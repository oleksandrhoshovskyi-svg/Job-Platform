package jobplatform.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIFactory {

    private UIFactory() {}

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppColors.FONT_SUBHEAD);
        btn.setBackground(AppColors.PRIMARY);
        btn.setForeground(AppColors.TEXT_ON_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(AppColors.PRIMARY_DARK);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(AppColors.PRIMARY);
            }
        });
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppColors.FONT_SUBHEAD);
        btn.setBackground(AppColors.BG_CARD);
        btn.setForeground(AppColors.TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(9, 20, 9, 20)
        ));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(AppColors.BG_SIDEBAR);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(AppColors.BG_CARD);
            }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(AppColors.DANGER);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(185,28,28)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(AppColors.DANGER); }
        });
        return btn;
    }

    public static JLabel titleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.FONT_TITLE);
        l.setForeground(AppColors.TEXT_PRIMARY);
        return l;
    }

    public static JLabel headingLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.FONT_HEADING);
        l.setForeground(AppColors.TEXT_PRIMARY);
        return l;
    }

    public static JLabel bodyLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.FONT_BODY);
        l.setForeground(AppColors.TEXT_PRIMARY);
        return l;
    }

    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.FONT_SMALL);
        l.setForeground(AppColors.TEXT_SECONDARY);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppColors.FONT_LABEL);
        l.setForeground(AppColors.TEXT_PRIMARY);
        return l;
    }

    public static JLabel statusBadge(String text, Color bg) {
        JLabel l = new JLabel(" " + text + " ");
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Color.WHITE);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(3, 8, 3, 8));
        return l;
    }

    public static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(AppColors.FONT_BODY);
        f.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return f;
    }

    public static JTextArea styledArea(int rows, int cols) {
        JTextArea a = new JTextArea(rows, cols);
        a.setFont(AppColors.FONT_BODY);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(8, 10, 8, 10));
        return a;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(AppColors.BG_CARD);
        p.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));
        return p;
    }

    public static JScrollPane scrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    public static JPanel pageBackground() {
        JPanel p = new JPanel();
        p.setBackground(AppColors.BG_PAGE);
        return p;
    }

    public static JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(AppColors.BORDER);
        return s;
    }

    public static JPanel navBar(String title, String userInfo) {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(AppColors.PRIMARY);
        nav.setBorder(new EmptyBorder(12, 20, 12, 20));
        nav.setPreferredSize(new Dimension(0, 56));

        JLabel logoLabel = new JLabel(title);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(AppColors.FONT_BODY);
        userLabel.setForeground(new Color(186, 230, 253));

        nav.add(logoLabel, BorderLayout.WEST);
        nav.add(userLabel, BorderLayout.EAST);
        return nav;
    }
}
