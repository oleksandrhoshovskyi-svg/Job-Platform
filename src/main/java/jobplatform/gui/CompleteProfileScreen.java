package jobplatform.gui;

import jobplatform.model.CandidateProfile;
import jobplatform.model.JobSeeker;
import jobplatform.persistence.DataStore;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class CompleteProfileScreen extends JPanel {

    private final MainFrame mainFrame;

    private JTextField headlineField;
    private JTextArea  summaryArea;
    private JTextField positionField;
    private JTextField locationField;
    private JLabel     errorLabel;

    public CompleteProfileScreen(MainFrame mainFrame) {
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
                new EmptyBorder(0, 0, 28, 0)));

        JPanel banner = new JPanel();
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBackground(new Color(255, 249, 230));
        banner.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 190, 80)),
                new EmptyBorder(14, 20, 14, 20)
        ));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel bannerTitle = new JLabel("Your candidate profile is incomplete");
        bannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bannerTitle.setForeground(AppColors.TEXT_PRIMARY);
        bannerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bannerSub = new JLabel("Please complete the following information to proceed with your application.");
        bannerSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bannerSub.setForeground(AppColors.TEXT_SECONDARY);
        bannerSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        bannerSub.setBorder(new EmptyBorder(4, 0, 0, 0));

        banner.add(bannerTitle);
        banner.add(bannerSub);

        card.add(banner);
        card.add(Box.createVerticalStrut(24));

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setBackground(Color.WHITE);
        fields.setBorder(new EmptyBorder(0, 28, 0, 28));

        fields.add(fieldLabel("Headline"));
        headlineField = styledTextField("e.g., Senior Frontend Developer");
        fields.add(headlineField);
        fields.add(Box.createVerticalStrut(16));

        fields.add(fieldLabel("Summary"));
        summaryArea = new JTextArea(5, 40);
        summaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane summScroll = new JScrollPane(summaryArea);
        summScroll.setAlignmentX(LEFT_ALIGNMENT);
        summScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        summScroll.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        fields.add(summScroll);
        fields.add(Box.createVerticalStrut(16));

        fields.add(fieldLabel("Preferred Position"));
        positionField = styledTextField("e.g., Frontend Developer, Full Stack Developer");
        fields.add(positionField);
        fields.add(Box.createVerticalStrut(16));

        fields.add(fieldLabel("Location"));
        locationField = styledTextField("e.g., Berlin, Germany");
        fields.add(locationField);
        fields.add(Box.createVerticalStrut(16));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppColors.FONT_SMALL);
        errorLabel.setForeground(AppColors.DANGER);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        fields.add(errorLabel);
        fields.add(Box.createVerticalStrut(8));

        JButton saveBtn = new JButton("Save and Continue");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(30, 30, 30));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> onSave());
        fields.add(saveBtn);

        card.add(fields);
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

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(7, 10, 7, 10)));
        f.setAlignmentX(LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setToolTipText(placeholder);
        return f;
    }

    public void refresh() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;
        CandidateProfile p = js.getCandidateProfile();
        headlineField.setText(p.getHeadline() != null ? p.getHeadline() : "");
        summaryArea.setText(p.getSummary() != null ? p.getSummary() : "");
        positionField.setText(js.getPreferredPosition() != null ? js.getPreferredPosition() : "");
        locationField.setText(js.getLocation() != null ? js.getLocation() : "");
        errorLabel.setText(" ");
    }

    private void onSave() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;

        String headline = headlineField.getText().trim();
        String summary = summaryArea.getText().trim();
        String position = positionField.getText().trim();
        String location = locationField.getText().trim();

        if (headline.isEmpty() || summary.isEmpty() || position.isEmpty() || location.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        js.getCandidateProfile().completeMissingData(headline, summary, position, location);
        DataStore.save();

        List<String> stillMissing = js.getCandidateProfile().getMissingRequiredData();
        boolean onlyCVMissing = stillMissing.size() == 1 && stillMissing.get(0).toLowerCase().contains("cv");

        if (stillMissing.isEmpty() || onlyCVMissing) {
            mainFrame.showScreen(MainFrame.SUBMIT_APPLICATION_SCREEN);
        } else {
            errorLabel.setText("Profile still incomplete: " + String.join(", ", stillMissing));
        }
    }
}