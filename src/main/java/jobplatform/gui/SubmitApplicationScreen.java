package jobplatform.gui;

import jobplatform.model.*;
import jobplatform.persistence.DataStore;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class SubmitApplicationScreen extends JPanel {

    private final MainFrame mainFrame;

    private JLabel offerTitleLabel;
    private JLabel offerMetaLabel;
    private JComboBox<CV> cvCombo;
    private JTextArea motivationArea;
    private JLabel errorLabel;

    public SubmitApplicationScreen(MainFrame mainFrame) {
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

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(247, 247, 247));
        content.setBorder(new EmptyBorder(32, 60, 32, 60));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(28, 32, 28, 32)));

        JLabel heading = new JLabel("Submit Application");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(AppColors.TEXT_PRIMARY);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(20));

        JPanel offerBox = new JPanel();
        offerBox.setLayout(new BoxLayout(offerBox, BoxLayout.Y_AXIS));
        offerBox.setBackground(new Color(243, 244, 246));
        offerBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(12, 14, 12, 14)));
        offerBox.setAlignmentX(LEFT_ALIGNMENT);
        offerBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel selectedLabel = new JLabel("Selected Job Offer");
        selectedLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        selectedLabel.setForeground(AppColors.TEXT_SECONDARY);
        selectedLabel.setAlignmentX(LEFT_ALIGNMENT);

        offerTitleLabel = new JLabel("—");
        offerTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        offerTitleLabel.setForeground(AppColors.TEXT_PRIMARY);
        offerTitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        offerMetaLabel = new JLabel("—");
        offerMetaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        offerMetaLabel.setForeground(AppColors.TEXT_SECONDARY);
        offerMetaLabel.setAlignmentX(LEFT_ALIGNMENT);

        offerBox.add(selectedLabel);
        offerBox.add(Box.createVerticalStrut(4));
        offerBox.add(offerTitleLabel);
        offerBox.add(offerMetaLabel);
        card.add(offerBox);
        card.add(Box.createVerticalStrut(20));

        card.add(fieldLabel("Select CV"));
        cvCombo = new JComboBox<>();
        cvCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cvCombo.setAlignmentX(LEFT_ALIGNMENT);
        cvCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cvCombo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        cvCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CV) setText(((CV) value).toString());
                else setText("Select a CV from your profile...");
                return this;
            }
        });
        card.add(cvCombo);
        card.add(Box.createVerticalStrut(18));

        card.add(fieldLabel("Motivation Message"));
        motivationArea = new JTextArea(7, 50);
        motivationArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        motivationArea.setLineWrap(true);
        motivationArea.setWrapStyleWord(true);
        motivationArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        motivationArea.setToolTipText("Explain why you're interested in this position and why you'd be a great fit...");
        JScrollPane motScroll = new JScrollPane(motivationArea);
        motScroll.setAlignmentX(LEFT_ALIGNMENT);
        motScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        motScroll.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        card.add(motScroll);
        card.add(Box.createVerticalStrut(12));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppColors.FONT_SMALL);
        errorLabel.setForeground(AppColors.DANGER);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(8));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton confirmBtn = new JButton("Confirm Application");
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmBtn.setBackground(new Color(30, 30, 30));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setOpaque(true);
        confirmBtn.setPreferredSize(new Dimension(190, 38));
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> onConfirm());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setForeground(AppColors.TEXT_PRIMARY);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(7, 20, 7, 20)));
        cancelBtn.setOpaque(true);
        cancelBtn.setPreferredSize(new Dimension(100, 38));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.OFFER_DETAILS_SCREEN));

        btnRow.add(confirmBtn);
        btnRow.add(cancelBtn);
        card.add(btnRow);

        content.add(UIFactory.scrollPane(card), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(AppColors.TEXT_PRIMARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 5, 0));
        return l;
    }

    public void refresh() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        JobOffer offer = mainFrame.getCurrentOffer();
        if (js == null || offer == null) return;

        offerTitleLabel.setText(offer.getTitle());
        offerMetaLabel.setText(offer.getCreatedBy().getCompany().getName()
                + " • " + offer.getLocation());

        cvCombo.removeAllItems();
        List<CV> cvs = js.getCandidateProfile().getCVs();
        for (CV cv : cvs) cvCombo.addItem(cv);
        for (CV cv : cvs) { if (cv.isDefault()) { cvCombo.setSelectedItem(cv); break; } }

        motivationArea.setText("");

        if (cvs.isEmpty()) {
            errorLabel.setText("You have no CVs. Please go to My Profile and upload a CV first.");
        } else {
            errorLabel.setText(" ");
        }
    }

    private void onConfirm() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        JobOffer offer = mainFrame.getCurrentOffer();
        if (js == null || offer == null) return;

        CV selectedCV = (CV) cvCombo.getSelectedItem();
        String message = motivationArea.getText().trim();

        errorLabel.setText(" ");
        if (selectedCV == null) { errorLabel.setText("Please select a CV."); return; }
        if (message.isEmpty())  { errorLabel.setText("Motivation message cannot be empty."); return; }
        if (message.length() < 20) { errorLabel.setText("Motivation message must be at least 20 characters."); return; }

        try {
            Application app = js.submitApplication(offer, selectedCV, message);
            DataStore.save();
            mainFrame.setLastApplication(app);
            mainFrame.showScreen(MainFrame.CONFIRMATION_SCREEN);
        } catch (RuntimeException ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }
}