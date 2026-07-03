package jobplatform.util;

import java.awt.Color;
import java.awt.Font;

public class AppColors {

    // Brand palette
    public static final Color PRIMARY = new Color(30, 64, 175);   // deep blue
    public static final Color PRIMARY_DARK = new Color(17, 38, 110);
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);
    public static final Color ACCENT = new Color(16, 185, 129);  // green — success
    public static final Color DANGER = new Color(220, 38, 38);   // red — error
    public static final Color WARNING = new Color(217, 119, 6);   // amber — warning

    public static final Color BG_PAGE = new Color(248, 250, 252);  // near-white
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_SIDEBAR = new Color(241, 245, 249);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color BORDER_FOCUS = PRIMARY;

    public static final Color TEXT_PRIMARY = new Color(15,  23,  42);
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;

    public static final Color STATUS_SUBMITTED = new Color(59, 130, 246);
    public static final Color STATUS_REVIEW = new Color(245, 158, 11);
    public static final Color STATUS_INTERVIEW = new Color(139, 92, 246);
    public static final Color STATUS_ACCEPTED = new Color(16, 185, 129);
    public static final Color STATUS_REJECTED = new Color(239, 68, 68);
    public static final Color STATUS_PUBLISHED = new Color(16, 185, 129);
    public static final Color STATUS_DRAFT = new Color(148, 163, 184);
    public static final Color STATUS_CLOSED = new Color(239, 68, 68);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_MONO = new Font("Consolas",  Font.PLAIN, 12);

    private AppColors() {}
}
