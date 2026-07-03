package jobplatform.gui;

import jobplatform.model.Application;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ConfirmationScreen extends JPanel {

    private final MainFrame mainFrame;
    private JLabel subtitleLabel;
    private JLabel statusBadge;
    private JLabel positionValueLabel;
    private JLabel companyValueLabel;
    private JLabel dateValueLabel;

    public ConfirmationScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(12, 20, 12, 20)));
        JLabel logo = new JLabel("Job Platform");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logo.setForeground(AppColors.TEXT_PRIMARY);
        header.add(logo, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(247, 247, 247));
        add(content, BorderLayout.CENTER);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 48, 40, 48)));
        card.setPreferredSize(new Dimension(500, 430));

        JLabel title = new JLabel("Application submitted successfully", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(6));

        subtitleLabel = new JLabel(" ", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(AppColors.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(20));

        statusBadge = new JLabel("● Status: Submitted", SwingConstants.CENTER);
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(new Color(34, 197, 94));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(5, 16, 5, 16));
        statusBadge.setAlignmentX(CENTER_ALIGNMENT);
        card.add(statusBadge);
        card.add(Box.createVerticalStrut(24));

        JPanel detailsBox = new JPanel();
        detailsBox.setLayout(new BoxLayout(detailsBox, BoxLayout.Y_AXIS));
        detailsBox.setBackground(new Color(248, 248, 248));
        detailsBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(16, 20, 16, 20)));
        detailsBox.setAlignmentX(CENTER_ALIGNMENT);
        detailsBox.setMaximumSize(new Dimension(420, 120));

        JLabel detailsHeading = new JLabel("Application Details");
        detailsHeading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsHeading.setForeground(AppColors.TEXT_PRIMARY);
        detailsHeading.setAlignmentX(LEFT_ALIGNMENT);
        detailsBox.add(detailsHeading);
        detailsBox.add(Box.createVerticalStrut(10));

        positionValueLabel = detailRow(detailsBox, "Position:");
        companyValueLabel = detailRow(detailsBox, "Company:");
        dateValueLabel = detailRow(detailsBox, "Date:");

        card.add(detailsBox);
        card.add(Box.createVerticalStrut(28));

        JButton viewAppsBtn = new JButton("View Submitted Applications");
        viewAppsBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewAppsBtn.setBackground(new Color(30, 30, 30));
        viewAppsBtn.setForeground(Color.WHITE);
        viewAppsBtn.setFocusPainted(false);
        viewAppsBtn.setBorderPainted(false);
        viewAppsBtn.setOpaque(true);
        viewAppsBtn.setAlignmentX(CENTER_ALIGNMENT);
        viewAppsBtn.setMaximumSize(new Dimension(420, 44));
        viewAppsBtn.setPreferredSize(new Dimension(420, 44));
        viewAppsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAppsBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.MY_APPLICATIONS_SCREEN));
        card.add(viewAppsBtn);

        content.add(card);
    }

    private JLabel detailRow(JPanel parent, String labelText) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setBackground(new Color(248, 248, 248));
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        JLabel val = new JLabel("—");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(AppColors.TEXT_PRIMARY);
        row.add(lbl);
        row.add(val);
        parent.add(row);
        return val;
    }

    public void refresh() {
        Application app = mainFrame.getLastApplication();
        if (app == null) return;

        subtitleLabel.setText("Your application has been sent to " + app.getJobOffer().getCreatedBy().getCompany().getName() + ".");
        positionValueLabel.setText(app.getJobOffer().getTitle());
        companyValueLabel.setText(app.getJobOffer().getCreatedBy().getCompany().getName());
        dateValueLabel.setText(app.getSubmittedAt() != null ? app.getSubmittedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "—");
        revalidate();
        repaint();
    }
}