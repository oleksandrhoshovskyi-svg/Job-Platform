package jobplatform.gui;

import jobplatform.model.*;
import jobplatform.persistence.DataStore;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProfileScreen extends JPanel {

    private final MainFrame mainFrame;

    private JTextField headlineField;
    private JTextArea summaryArea;
    private JTextField positionField;
    private JTextField locationField;

    private JProgressBar completenessBar;
    private JLabel completenessLabel;
    private JLabel missingLabel;

    private JPanel cvListPanel;
    private JTextField cvNameField;

    private JPanel skillListPanel;
    private JComboBox<Skill> skillCombo;
    private JComboBox<String> proficiencyCombo;
    private JSpinner yearsSpinner;

    private JLabel profileSaveLabel;
    private JLabel cvFeedbackLabel;
    private JLabel skillFeedbackLabel;

    public ProfileScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_PAGE);
        add(buildNav(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppColors.BG_PAGE);
        content.setBorder(new EmptyBorder(20, 32, 20, 32));

        content.add(buildCompletenessCard());
        content.add(Box.createVerticalStrut(16));
        content.add(buildProfileCard());
        content.add(Box.createVerticalStrut(16));
        content.add(buildCVCard());
        content.add(Box.createVerticalStrut(16));
        content.add(buildSkillCard());
        content.add(Box.createVerticalStrut(20));

        add(UIFactory.scrollPane(content), BorderLayout.CENTER);
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(AppColors.PRIMARY);
        nav.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel logo = new JLabel("Job Platform  /  My Profile");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setBackground(AppColors.PRIMARY);
        JButton back = navBtn("Browse Offers");
        back.addActionListener(e -> mainFrame.showScreen(MainFrame.BROWSE_SCREEN));
        JButton apps = navBtn("My Applications");
        apps.addActionListener(e -> mainFrame.showScreen(MainFrame.MY_APPLICATIONS_SCREEN));
        right.add(back); right.add(apps);
        nav.add(logo, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(AppColors.FONT_BODY); b.setForeground(Color.WHITE);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setOpaque(false); b.setBackground(new Color(0,0,0,0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buildCompletenessCard() {
        JPanel card = UIFactory.card();
        card.setLayout(new BorderLayout(16, 0));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel title = UIFactory.headingLabel("Profile Completeness");
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
        left.setBackground(AppColors.BG_CARD);
        left.add(title);
        missingLabel = UIFactory.mutedLabel(" ");
        left.add(missingLabel);

        JPanel right = new JPanel(new GridLayout(2, 1, 0, 4));
        right.setBackground(AppColors.BG_CARD);
        completenessLabel = UIFactory.headingLabel("0%");
        completenessLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        completenessLabel.setForeground(AppColors.PRIMARY);
        completenessBar = new JProgressBar(0, 100);
        completenessBar.setForeground(AppColors.ACCENT);
        completenessBar.setBackground(AppColors.BORDER);
        completenessBar.setPreferredSize(new Dimension(200, 14));
        right.add(completenessLabel);
        right.add(completenessBar);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel buildProfileCard() {
        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = UIFactory.headingLabel("Basic Information");
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setBackground(AppColors.BG_CARD);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel leftCol = fieldCol("Preferred Position *", positionField = UIFactory.styledField());
        JPanel rightCol = fieldCol("Location *", locationField = UIFactory.styledField());
        grid.add(leftCol);
        grid.add(rightCol);
        card.add(grid);
        card.add(Box.createVerticalStrut(14));

        card.add(fieldLabel("Headline *"));
        headlineField = UIFactory.styledField();
        headlineField.setAlignmentX(LEFT_ALIGNMENT);
        headlineField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(headlineField);
        card.add(Box.createVerticalStrut(14));

        card.add(fieldLabel("Professional Summary *"));
        summaryArea = UIFactory.styledArea(5, 60);
        JScrollPane summScroll = UIFactory.scrollPane(summaryArea);
        summScroll.setAlignmentX(LEFT_ALIGNMENT);
        summScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        summScroll.setBorder(new LineBorder(AppColors.BORDER, 1));
        card.add(summScroll);
        card.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(AppColors.BG_CARD);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        JButton saveBtn = UIFactory.primaryButton("Save Profile");
        saveBtn.addActionListener(e -> saveProfile());
        profileSaveLabel = new JLabel(" ");
        profileSaveLabel.setFont(AppColors.FONT_BODY);
        btnRow.add(saveBtn);
        btnRow.add(profileSaveLabel);
        card.add(btnRow);
        return card;
    }

    private JPanel buildCVCard() {
        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = UIFactory.headingLabel("CV Management");
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        JLabel sub = UIFactory.mutedLabel("Upload CVs and set one as default for quick applications.");
        sub.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(14));

        cvListPanel = new JPanel();
        cvListPanel.setLayout(new BoxLayout(cvListPanel, BoxLayout.Y_AXIS));
        cvListPanel.setBackground(AppColors.BG_CARD);
        cvListPanel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(cvListPanel);
        card.add(Box.createVerticalStrut(14));

        JPanel uploadRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        uploadRow.setBackground(AppColors.BG_CARD);
        uploadRow.setAlignmentX(LEFT_ALIGNMENT);
        cvNameField = UIFactory.styledField();
        cvNameField.setPreferredSize(new Dimension(280, 34));
        cvNameField.setToolTipText("Enter CV filename, e.g. MyCV_2026.pdf");
        JButton uploadBtn = UIFactory.secondaryButton("+ Add CV");
        uploadBtn.addActionListener(e -> addCV());
        cvFeedbackLabel = new JLabel(" ");
        cvFeedbackLabel.setFont(AppColors.FONT_SMALL);
        uploadRow.add(new JLabel("Filename:"));
        uploadRow.add(cvNameField);
        uploadRow.add(uploadBtn);
        uploadRow.add(cvFeedbackLabel);
        card.add(uploadRow);
        return card;
    }
    private JPanel buildSkillCard() {
        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = UIFactory.headingLabel("Skills");
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        skillListPanel = new JPanel();
        skillListPanel.setLayout(new BoxLayout(skillListPanel, BoxLayout.Y_AXIS));
        skillListPanel.setBackground(AppColors.BG_CARD);
        skillListPanel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(skillListPanel);
        card.add(Box.createVerticalStrut(14));

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        addRow.setBackground(AppColors.BG_CARD);
        addRow.setAlignmentX(LEFT_ALIGNMENT);

        skillCombo = new JComboBox<>();
        skillCombo.setPreferredSize(new Dimension(160, 32));
        proficiencyCombo = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced", "Expert"});
        proficiencyCombo.setPreferredSize(new Dimension(120, 32));
        yearsSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 40, 1));
        yearsSpinner.setPreferredSize(new Dimension(60, 32));

        JButton addSkillBtn = UIFactory.secondaryButton("+ Add Skill");
        addSkillBtn.addActionListener(e -> addSkill());
        skillFeedbackLabel = new JLabel(" ");
        skillFeedbackLabel.setFont(AppColors.FONT_SMALL);

        addRow.add(new JLabel("Skill:"));
        addRow.add(skillCombo);
        addRow.add(new JLabel("Level:"));
        addRow.add(proficiencyCombo);
        addRow.add(new JLabel("Years:"));
        addRow.add(yearsSpinner);
        addRow.add(addSkillBtn);
        addRow.add(skillFeedbackLabel);
        card.add(addRow);
        return card;
    }

    private JPanel fieldCol(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_CARD);
        JLabel lbl = UIFactory.fieldLabel(label);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = UIFactory.fieldLabel(text);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }

    public void refresh() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;

        CandidateProfile profile = js.getCandidateProfile();

        headlineField.setText(nvl(profile.getHeadline()));
        summaryArea.setText(nvl(profile.getSummary()));
        positionField.setText(nvl(js.getPreferredPosition()));
        locationField.setText(nvl(js.getLocation()));
        profileSaveLabel.setText(" ");

        updateCompleteness(js);

        refreshCVList(js);
        cvNameField.setText("");
        cvFeedbackLabel.setText(" ");

        refreshSkillList(js);
        refreshSkillCombo(js);
        skillFeedbackLabel.setText(" ");

        revalidate();
        repaint();
    }

    private void updateCompleteness(JobSeeker js) {
        int pct = js.getCandidateProfile().computeCompletenessLevel();
        completenessBar.setValue(pct);
        completenessLabel.setText(pct + "%");
        completenessLabel.setForeground(pct == 100 ? AppColors.ACCENT : AppColors.PRIMARY);
        List<String> missing = js.getCandidateProfile().getMissingRequiredData();
        if (missing.isEmpty()) {
            missingLabel.setText("Profile complete — ready to apply");
            missingLabel.setForeground(AppColors.ACCENT);
        } else {
            missingLabel.setText("Missing: " + String.join(", ", missing));
            missingLabel.setForeground(AppColors.WARNING);
        }
    }

    private void refreshCVList(JobSeeker js) {
        cvListPanel.removeAll();
        List<CV> cvs = js.getCandidateProfile().getCVs();
        if (cvs.isEmpty()) {
            JLabel empty = UIFactory.mutedLabel("No CVs uploaded yet.");
            empty.setAlignmentX(LEFT_ALIGNMENT);
            cvListPanel.add(empty);
        } else {
            for (CV cv : cvs) {
                cvListPanel.add(buildCVRow(cv, js));
                cvListPanel.add(Box.createVerticalStrut(6));
            }
        }
        cvListPanel.revalidate();
        cvListPanel.repaint();
    }

    private JPanel buildCVRow(CV cv, JobSeeker js) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(cv.isDefault() ? AppColors.PRIMARY_LIGHT : AppColors.BG_SIDEBAR);
        row.setBorder(new CompoundBorder(
                new LineBorder(cv.isDefault() ? AppColors.PRIMARY : AppColors.BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel nameLabel = UIFactory.bodyLabel("CV " + cv.getFileName());
        JLabel uploadedLabel = UIFactory.mutedLabel(cv.isDefault() ? "Default" : "");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.setBackground(row.getBackground());

        if (!cv.isDefault()) {
            JButton setDefaultBtn = UIFactory.secondaryButton("Set Default");
            setDefaultBtn.addActionListener(e -> {
                cv.markAsDefault();
                DataStore.save();
                refreshCVList(js);
                updateCompleteness(js);
            });
            btnPanel.add(setDefaultBtn);
        } else {
            JLabel defBadge = UIFactory.statusBadge("DEFAULT", AppColors.PRIMARY);
            btnPanel.add(defBadge);
        }

        row.add(nameLabel, BorderLayout.CENTER);
        row.add(uploadedLabel, BorderLayout.WEST);
        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }

    private void refreshSkillList(JobSeeker js) {
        skillListPanel.removeAll();
        List<CandidateSkill> skills = js.getCandidateProfile().getCandidateSkills();
        if (skills.isEmpty()) {
            JLabel empty = UIFactory.mutedLabel("No skills added yet.");
            empty.setAlignmentX(LEFT_ALIGNMENT);
            skillListPanel.add(empty);
        } else {
            JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
            grid.setBackground(AppColors.BG_CARD);
            grid.setAlignmentX(LEFT_ALIGNMENT);
            for (CandidateSkill cs : skills) {
                grid.add(buildSkillBadge(cs));
            }
            skillListPanel.add(grid);
        }
        skillListPanel.revalidate();
        skillListPanel.repaint();
    }

    private JPanel buildSkillBadge(CandidateSkill cs) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        badge.setBackground(AppColors.PRIMARY_LIGHT);
        badge.setBorder(new CompoundBorder(
                new LineBorder(new Color(147, 197, 253), 1, true),
                new EmptyBorder(2, 4, 2, 4)));
        JLabel text = new JLabel(cs.getSkillName() + " · " + cs.getProficiencyLevel()
                + " · " + cs.getYearsOfExperience() + "yr");
        text.setFont(AppColors.FONT_SMALL);
        text.setForeground(AppColors.PRIMARY);
        badge.add(text);
        return badge;
    }

    private void refreshSkillCombo(JobSeeker js) {
        skillCombo.removeAllItems();
        List<String> existingSkillNames = js.getCandidateProfile().getCandidateSkills()
                .stream().map(CandidateSkill::getSkillName)
                .collect(java.util.stream.Collectors.toList());
        for (Skill s : Skill.getExtent()) {
            if (!existingSkillNames.contains(s.getName())) {
                skillCombo.addItem(s);
            }
        }
    }

    private void saveProfile() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;

        String headline = headlineField.getText().trim();
        String summary = summaryArea.getText().trim();
        String position = positionField.getText().trim();
        String location = locationField.getText().trim();

        if (headline.isEmpty() || summary.isEmpty() || position.isEmpty() || location.isEmpty()) {
            profileSaveLabel.setForeground(AppColors.DANGER);
            profileSaveLabel.setText("Please fill in all required fields.");
            return;
        }

        js.getCandidateProfile().completeMissingData(headline, summary, position, location);
        DataStore.save();
        updateCompleteness(js);
        profileSaveLabel.setForeground(AppColors.ACCENT);
        profileSaveLabel.setText("Profile saved.");
    }

    private void addCV() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;

        String name = cvNameField.getText().trim();
        if (name.isEmpty()) {
            cvFeedbackLabel.setForeground(AppColors.DANGER);
            cvFeedbackLabel.setText("Enter a filename.");
            return;
        }
        if (!name.toLowerCase().endsWith(".pdf") && !name.toLowerCase().endsWith(".docx")) {
            name += ".pdf";
        }
        try {
            new CV(name, js.getCandidateProfile());
            DataStore.save();
            cvNameField.setText("");
            cvFeedbackLabel.setForeground(AppColors.ACCENT);
            cvFeedbackLabel.setText("CV added.");
            refreshCVList(js);
            updateCompleteness(js);
        } catch (Exception ex) {
            cvFeedbackLabel.setForeground(AppColors.DANGER);
            cvFeedbackLabel.setText(ex.getMessage());
        }
    }

    private void addSkill() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;

        Skill selected = (Skill) skillCombo.getSelectedItem();
        if (selected == null) {
            skillFeedbackLabel.setForeground(AppColors.DANGER);
            skillFeedbackLabel.setText("No skill available to add.");
            return;
        }
        String proficiency = (String) proficiencyCombo.getSelectedItem();
        int years = (int) yearsSpinner.getValue();

        try {
            new CandidateSkill(js.getCandidateProfile(), selected, proficiency, years);
            DataStore.save();
            skillFeedbackLabel.setForeground(AppColors.ACCENT);
            skillFeedbackLabel.setText("Skill added.");
            refreshSkillList(js);
            refreshSkillCombo(js);
        } catch (IllegalArgumentException ex) {
            skillFeedbackLabel.setForeground(AppColors.DANGER);
            skillFeedbackLabel.setText(ex.getMessage());
        }
    }

    private String nvl(String s) { return s != null ? s : ""; }
}
