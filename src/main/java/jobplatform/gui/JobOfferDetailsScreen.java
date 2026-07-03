package jobplatform.gui;

import jobplatform.model.*;
import jobplatform.model.enums.OfferStatus;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class JobOfferDetailsScreen extends JPanel {

    private final MainFrame mainFrame;

    private JLabel titleLabel;
    private JLabel companyValueLabel;
    private JLabel locationValueLabel;
    private JLabel workModeValueLabel;
    private JLabel salaryValueLabel;
    private JPanel requirementsPanel;
    private JButton submitBtn;
    private JLabel warningLabel;

    public JobOfferDetailsScreen(MainFrame mainFrame) {
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

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        headerRight.setBackground(Color.WHITE);
        JButton backBtn = new JButton("Back");
        backBtn.setFont(AppColors.FONT_SMALL);
        backBtn.setForeground(AppColors.TEXT_SECONDARY);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setOpaque(false);
        backBtn.setBackground(new Color(0,0,0,0));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.BROWSE_SCREEN));
        headerRight.add(backBtn);

        header.add(logo, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(247, 247, 247));
        content.setBorder(new EmptyBorder(32, 60, 32, 60));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(28, 32, 28, 32)));

        titleLabel = new JLabel("Title");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(14));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 40, 0));
        row1.setBackground(Color.WHITE);
        row1.setAlignmentX(LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JPanel companyPair = labeledPair("Company:");
        companyValueLabel = (JLabel) companyPair.getComponent(1);
        JPanel locationPair = labeledPair("Location:");
        locationValueLabel = (JLabel) locationPair.getComponent(1);
        row1.add(companyPair);
        row1.add(locationPair);
        card.add(row1);
        card.add(Box.createVerticalStrut(6));

        JPanel row2 = new JPanel(new GridLayout(1, 2, 40, 0));
        row2.setBackground(Color.WHITE);
        row2.setAlignmentX(LEFT_ALIGNMENT);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JPanel workModePair = labeledPair("Work Mode:");
        workModeValueLabel = (JLabel) workModePair.getComponent(1);
        JPanel salaryPair = labeledPair("Salary Range:");
        salaryValueLabel = (JLabel) salaryPair.getComponent(1);
        row2.add(workModePair);
        row2.add(salaryPair);
        card.add(row2);
        card.add(Box.createVerticalStrut(20));

        JPanel reqBox = new JPanel();
        reqBox.setLayout(new BoxLayout(reqBox, BoxLayout.Y_AXIS));
        reqBox.setBackground(new Color(248, 248, 248));
        reqBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(14, 18, 14, 18)));
        reqBox.setAlignmentX(LEFT_ALIGNMENT);

        JLabel reqHeading = new JLabel("Requirements");
        reqHeading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        reqHeading.setForeground(AppColors.TEXT_PRIMARY);
        reqHeading.setAlignmentX(LEFT_ALIGNMENT);
        reqBox.add(reqHeading);
        reqBox.add(Box.createVerticalStrut(8));

        requirementsPanel = new JPanel();
        requirementsPanel.setLayout(new BoxLayout(requirementsPanel, BoxLayout.Y_AXIS));
        requirementsPanel.setBackground(new Color(248, 248, 248));
        requirementsPanel.setAlignmentX(LEFT_ALIGNMENT);
        reqBox.add(requirementsPanel);
        card.add(reqBox);
        card.add(Box.createVerticalStrut(20));

        warningLabel = new JLabel(" ");
        warningLabel.setFont(AppColors.FONT_BODY);
        warningLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(warningLabel);
        card.add(Box.createVerticalStrut(6));

        submitBtn = new JButton("Submit Application");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setBackground(new Color(30, 30, 30));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setOpaque(true);
        submitBtn.setAlignmentX(LEFT_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.addActionListener(e -> onSubmitClicked());
        card.add(submitBtn);

        content.add(UIFactory.scrollPane(card), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private JPanel labeledPair(String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        JLabel val = new JLabel("—");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(AppColors.TEXT_PRIMARY);
        p.add(lbl);
        p.add(val);
        return p;
    }

    public void refresh() {
        JobOffer offer = mainFrame.getCurrentOffer();
        if (offer == null) return;

        titleLabel.setText(offer.getTitle());
        companyValueLabel.setText(offer.getCreatedBy().getCompany().getName());
        locationValueLabel.setText(offer.getLocation());
        workModeValueLabel.setText(offer.getWorkMode());
        salaryValueLabel.setText(offer.getSalaryRange());

        requirementsPanel.removeAll();
        for (String req : offer.getRequirements()) {
            JLabel r = new JLabel("• " + req);
            r.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            r.setForeground(AppColors.TEXT_PRIMARY);
            r.setBorder(new EmptyBorder(2, 0, 2, 0));
            r.setAlignmentX(LEFT_ALIGNMENT);
            requirementsPanel.add(r);
        }

        warningLabel.setText(" ");
        submitBtn.setEnabled(true);
        submitBtn.setBackground(new Color(30, 30, 30));

        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js != null) {
            if (!offer.isAccepting()) {
                warningLabel.setText("This job offer is no longer available.");
                warningLabel.setForeground(AppColors.DANGER);
                submitBtn.setEnabled(false);
                submitBtn.setBackground(new Color(160, 160, 160));
            } else if (js.hasAlreadyApplied(offer)) {
                warningLabel.setText("You have already applied for this position.");
                warningLabel.setForeground(AppColors.ACCENT);
                submitBtn.setEnabled(false);
                submitBtn.setBackground(new Color(160, 160, 160));
            }
        }

        revalidate();
        repaint();
    }

    private void onSubmitClicked() {
        JobOffer offer = mainFrame.getCurrentOffer();
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (offer == null || js == null) return;

        if (!offer.isAccepting()) {
            JOptionPane.showMessageDialog(this,
                    "This job offer is no longer available.", "Offer Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (js.hasAlreadyApplied(offer)) {
            JOptionPane.showMessageDialog(this,
                    "You have already applied for this job offer.", "Already Applied",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!js.getCandidateProfile().isComplete()) {
            mainFrame.showScreen(MainFrame.COMPLETE_PROFILE_SCREEN);
            return;
        }
        mainFrame.showScreen(MainFrame.SUBMIT_APPLICATION_SCREEN);
    }
}