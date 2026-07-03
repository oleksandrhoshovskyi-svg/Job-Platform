package jobplatform.gui;

import jobplatform.model.*;
import jobplatform.model.enums.ApplicationStatus;
import jobplatform.model.enums.OfferStatus;
import jobplatform.persistence.DataStore;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployerDashboardScreen extends JPanel {

    private final MainFrame mainFrame;

    private final DefaultListModel<JobOffer> offerModel = new DefaultListModel<>();
    private JList<JobOffer> offerList;
    private JPanel applicationsPanel;
    private JLabel offerCountLabel;
    private JLabel appCountLabel;
    private JLabel companyInfoLabel;

    public EmployerDashboardScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_PAGE);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.add(buildNav(), BorderLayout.NORTH);

        JPanel companyBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        companyBar.setBackground(AppColors.BG_SIDEBAR);
        companyBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER));
        companyInfoLabel = UIFactory.bodyLabel(" ");
        companyBar.add(companyInfoLabel);
        topArea.add(companyBar, BorderLayout.CENTER);

        add(topArea, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildOffersPanel(), buildApplicationsPanel());
        split.setDividerLocation(320);
        split.setDividerSize(4);
        split.setBorder(BorderFactory.createEmptyBorder());
        add(split, BorderLayout.CENTER);

        add(buildBottomBar(), BorderLayout.SOUTH);
    }


    private JPanel buildNav() {
        Color navBg = new Color(15, 23, 42);
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(navBg);
        nav.setBorder(new EmptyBorder(11, 20, 11, 20));

        JLabel logo = new JLabel("Job Platform  /  Employer Dashboard");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(navBg);

        JLabel userLabel = new JLabel();
        userLabel.setFont(AppColors.FONT_SMALL);
        userLabel.setForeground(new Color(186, 230, 253));
        userLabel.setName("navUserLabel");
        right.add(userLabel);

        right.add(navSep());
        right.add(navBtn("My Company", e -> showCompanyDialog()));
        right.add(navBtn("Browse Offers", e -> mainFrame.showScreen(MainFrame.BROWSE_SCREEN)));
        right.add(navSep());
        right.add(navBtn("Sign Out", e -> mainFrame.showScreen(MainFrame.LOGIN_SCREEN)));

        nav.add(logo, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private JButton navBtn(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setFont(AppColors.FONT_SMALL);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBackground(new Color(0, 0, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(action);
        return b;
    }

    private JLabel navSep() {
        JLabel l = new JLabel("|");
        l.setForeground(new Color(60, 80, 110));
        return l;
    }


    private JPanel buildOffersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.BG_SIDEBAR);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(14, 16, 10, 16));
        JLabel hLabel = UIFactory.headingLabel("My Job Offers");
        offerCountLabel = UIFactory.mutedLabel("");
        header.add(hLabel, BorderLayout.WEST);
        header.add(offerCountLabel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        offerList = new JList<>(offerModel);
        offerList.setFont(AppColors.FONT_BODY);
        offerList.setBackground(AppColors.BG_SIDEBAR);
        offerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        offerList.setCellRenderer(new OfferCellRenderer());
        offerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadApplications();
        });

        panel.add(UIFactory.scrollPane(offerList), BorderLayout.CENTER);
        return panel;
    }


    private JPanel buildApplicationsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_PAGE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_PAGE);
        header.setBorder(new EmptyBorder(14, 20, 10, 20));
        JLabel hLabel = UIFactory.headingLabel("Applications");
        appCountLabel = UIFactory.mutedLabel("Select an offer");
        header.add(hLabel, BorderLayout.WEST);
        header.add(appCountLabel, BorderLayout.EAST);
        outer.add(header, BorderLayout.NORTH);

        applicationsPanel = new JPanel();
        applicationsPanel.setLayout(new BoxLayout(applicationsPanel, BoxLayout.Y_AXIS));
        applicationsPanel.setBackground(AppColors.BG_PAGE);
        applicationsPanel.setBorder(new EmptyBorder(0, 16, 16, 16));

        outer.add(UIFactory.scrollPane(applicationsPanel), BorderLayout.CENTER);
        return outer;
    }


    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        bar.setBackground(AppColors.BG_CARD);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));
        JButton newOfferBtn = UIFactory.primaryButton("+ Post New Job Offer");
        newOfferBtn.addActionListener(e -> showNewOfferDialog());
        bar.add(newOfferBtn);
        return bar;
    }

    public void refresh() {
        Employer emp = mainFrame.getCurrentEmployer();
        if (emp == null) {
            companyInfoLabel.setText("No employer account selected.");
            return;
        }

        updateNavUserLabel(emp);

        companyInfoLabel.setText(emp.getCompany().getName()
                + "  ·  " + emp.getCompany().getLocation()
                + "  ·  " + emp.getPlatformUser().getFullName()
                + "  (" + emp.getPositionTitle() + ")");

        offerModel.clear();
        for (JobOffer o : emp.getJobOffers()) offerModel.addElement(o);
        offerCountLabel.setText(offerModel.size() + " offer" + (offerModel.size() != 1 ? "s" : ""));

        applicationsPanel.removeAll();
        appCountLabel.setText("Select an offer");

        if (!offerModel.isEmpty()) {
            offerList.setSelectedIndex(0);
        } else {
            revalidate();
            repaint();
        }
    }

    private void updateNavUserLabel(Employer emp) {
        Component north = getComponent(0);
        if (north instanceof JPanel) {
            walkForLabel((JPanel) north, "navUserLabel",
                    emp.getPlatformUser().getFullName());
        }
    }

    private void walkForLabel(JPanel panel, String name, String text) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel && name.equals(c.getName())) {
                ((JLabel) c).setText(text);
                return;
            }
            if (c instanceof JPanel) walkForLabel((JPanel) c, name, text);
        }
    }


    private void loadApplications() {
        JobOffer selected = offerList.getSelectedValue();
        applicationsPanel.removeAll();

        if (selected == null) {
            appCountLabel.setText("Select an offer");
            revalidate();
            repaint();
            return;
        }

        List<Application> apps = selected.getApplications();
        appCountLabel.setText(apps.size() + " application" + (apps.size() != 1 ? "s" : ""));

        JPanel offerCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        offerCtrl.setBackground(AppColors.BG_PAGE);
        offerCtrl.setAlignmentX(LEFT_ALIGNMENT);
        offerCtrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        if (selected.getOfferStatus() == OfferStatus.DRAFT) {
            JButton publishBtn = UIFactory.primaryButton("Publish Offer");
            publishBtn.addActionListener(e -> {
                selected.publish(); DataStore.save(); loadApplications(); refresh();
            });
            offerCtrl.add(publishBtn);
        } else if (selected.getOfferStatus() == OfferStatus.PUBLISHED) {
            JButton closeBtn = UIFactory.dangerButton("Close Offer");
            closeBtn.addActionListener(e -> {
                selected.close(); DataStore.save(); loadApplications(); refresh();
            });
            offerCtrl.add(closeBtn);
        } else {
            JLabel closedNote = UIFactory.mutedLabel("This offer is closed — no further actions available.");
            offerCtrl.add(closedNote);
        }

        applicationsPanel.add(offerCtrl);
        applicationsPanel.add(Box.createVerticalStrut(8));

        if (apps.isEmpty()) {
            JLabel empty = UIFactory.mutedLabel("No applications received yet.");
            empty.setAlignmentX(LEFT_ALIGNMENT);
            applicationsPanel.add(empty);
        } else {
            for (Application app : apps) {
                applicationsPanel.add(buildApplicationCard(app));
                applicationsPanel.add(Box.createVerticalStrut(10));
            }
        }

        revalidate();
        repaint();
    }

    private JPanel buildApplicationCard(Application app) {
        JPanel card = UIFactory.card();
        card.setLayout(new BorderLayout(12, 8));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        JobSeeker js = app.getJobSeeker();
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(AppColors.BG_CARD);

        JLabel nameLabel = UIFactory.headingLabel(js.getPlatformUser().getFullName());
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel emailLabel = UIFactory.mutedLabel(js.getPlatformUser().getEmail());
        emailLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel cvLabel = UIFactory.bodyLabel("CV: " + app.getAttachedCV().getFileName());
        cvLabel.setAlignmentX(LEFT_ALIGNMENT);

        String motiv = app.getMotivationMessage();
        if (motiv != null && motiv.length() > 120) motiv = motiv.substring(0, 120) + "…";
        JLabel motivLabel = new JLabel("<html><i>" + motiv + "</i></html>");
        motivLabel.setFont(AppColors.FONT_SMALL);
        motivLabel.setForeground(AppColors.TEXT_SECONDARY);
        motivLabel.setAlignmentX(LEFT_ALIGNMENT);

        String dateStr = app.getSubmittedAt() != null ? app.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")) : "—";
        JLabel dateLabel = UIFactory.mutedLabel("Applied: " + dateStr);
        dateLabel.setAlignmentX(LEFT_ALIGNMENT);

        info.add(nameLabel);
        info.add(emailLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(cvLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(motivLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(dateLabel);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(AppColors.BG_CARD);
        right.setPreferredSize(new Dimension(160, 0));

        Color badgeColor = statusColor(app.getApplicationStatus());
        JLabel badge = UIFactory.statusBadge(app.getApplicationStatus().toString().replace("_", " "), badgeColor);
        badge.setAlignmentX(CENTER_ALIGNMENT);
        right.add(badge);
        right.add(Box.createVerticalStrut(10));

        addStatusButtons(right, app);

        card.add(info, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }


    private void addStatusButtons(JPanel right, Application app) {
        ApplicationStatus status = app.getApplicationStatus();

        switch (status) {
            case SUBMITTED: {
                JButton btn = actionButton("Move to Review", AppColors.PRIMARY);
                btn.addActionListener(e -> changeStatus(app, ApplicationStatus.UNDER_REVIEW));
                right.add(btn);
                break;
            }
            case UNDER_REVIEW: {
                JButton schedBtn = actionButton("Schedule Interview", AppColors.PRIMARY);
                schedBtn.addActionListener(e -> showScheduleInterviewDialog(app));
                JButton rejectBtn = actionButton("Reject", AppColors.DANGER);
                rejectBtn.addActionListener(e -> changeStatus(app, ApplicationStatus.REJECTED));
                right.add(schedBtn);
                right.add(Box.createVerticalStrut(6));
                right.add(rejectBtn);
                break;
            }
            case INTERVIEW_SCHEDULED: {
                JButton acceptBtn = actionButton("Accept", AppColors.ACCENT);
                acceptBtn.addActionListener(e -> changeStatus(app, ApplicationStatus.ACCEPTED));
                JButton rejectBtn = actionButton("Reject", AppColors.DANGER);
                rejectBtn.addActionListener(e -> changeStatus(app, ApplicationStatus.REJECTED));
                right.add(acceptBtn);
                right.add(Box.createVerticalStrut(6));
                right.add(rejectBtn);
                break;
            }
            case ACCEPTED: {
                JLabel lbl = UIFactory.mutedLabel("Accepted");
                lbl.setForeground(AppColors.ACCENT);
                lbl.setAlignmentX(CENTER_ALIGNMENT);
                right.add(lbl);
                break;
            }
            case REJECTED: {
                JLabel lbl = UIFactory.mutedLabel("Rejected");
                lbl.setForeground(AppColors.DANGER);
                lbl.setAlignmentX(CENTER_ALIGNMENT);
                right.add(lbl);
                break;
            }
        }
    }

    private JButton actionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(AppColors.FONT_BODY);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(148, 32));
        btn.setMaximumSize(new Dimension(148, 32));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        return btn;
    }

    private void changeStatus(Application app, ApplicationStatus newStatus) {
        try {
            app.updateStatus(newStatus);
            DataStore.save();
            loadApplications();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Status Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showScheduleInterviewDialog(Application app) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule Interview", true);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BG_CARD);
        form.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = UIFactory.headingLabel("Schedule Interview");
        title.setAlignmentX(LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(4));
        JLabel sub = UIFactory.mutedLabel("For: " + app.getJobSeeker().getPlatformUser().getFullName());
        sub.setAlignmentX(LEFT_ALIGNMENT);
        form.add(sub);
        form.add(Box.createVerticalStrut(18));

        form.add(fieldLabel("Format"));
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"VIDEO", "IN_PERSON", "PHONE"});
        formatCombo.setAlignmentX(LEFT_ALIGNMENT);
        formatCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(formatCombo);
        form.add(Box.createVerticalStrut(12));

        form.add(fieldLabel("Days from now"));
        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 60, 1));
        daysSpinner.setAlignmentX(LEFT_ALIGNMENT);
        daysSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(daysSpinner);
        form.add(Box.createVerticalStrut(12));

        form.add(fieldLabel("Time"));
        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        timeRow.setBackground(AppColors.BG_CARD);
        timeRow.setAlignmentX(LEFT_ALIGNMENT);

        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 23, 1));
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0,  0, 59, 5));

        hourSpinner.setEditor(new JSpinner.NumberEditor(hourSpinner,   "00"));
        minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "00"));

        hourSpinner.setPreferredSize(new Dimension(60, 32));
        minuteSpinner.setPreferredSize(new Dimension(60, 32));

        timeRow.add(hourSpinner);
        timeRow.add(new JLabel(":"));
        timeRow.add(minuteSpinner);
        form.add(timeRow);
        form.add(Box.createVerticalStrut(20));

        JLabel feedback = new JLabel(" ");
        feedback.setFont(AppColors.FONT_SMALL);
        feedback.setForeground(AppColors.DANGER);
        feedback.setAlignmentX(LEFT_ALIGNMENT);
        form.add(feedback);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(AppColors.BG_CARD);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton confirmBtn = UIFactory.primaryButton("Schedule");
        confirmBtn.addActionListener(e -> {
            String format = (String) formatCombo.getSelectedItem();
            int days = (int) daysSpinner.getValue();
            int hour = (int) hourSpinner.getValue();
            int minute = (int) minuteSpinner.getValue();
            LocalDateTime scheduledAt = LocalDateTime.now()
                    .plusDays(days)
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(0)
                    .withNano(0);
            Interview interview = new Interview(scheduledAt, format);
            try {
                app.scheduleInterview(interview);
                DataStore.save();
                dialog.dispose();
                loadApplications();
            } catch (IllegalStateException ex) {
                feedback.setText(ex.getMessage());
            }
        });

        JButton cancelBtn = UIFactory.secondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(confirmBtn);
        btnRow.add(cancelBtn);
        form.add(btnRow);

        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

    private void showNewOfferDialog() {
        Employer emp = mainFrame.getCurrentEmployer();
        if (emp == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Post New Job Offer", true);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BG_CARD);
        form.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = UIFactory.headingLabel("Post a New Job Offer");
        title.setAlignmentX(LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(18));

        form.add(fieldLabel("Offer Type"));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Permanent", "Contract", "Freelance"});
        typeCombo.setAlignmentX(LEFT_ALIGNMENT);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(typeCombo);
        form.add(Box.createVerticalStrut(10));

        form.add(fieldLabel("Job Title *"));
        JTextField titleField = UIFactory.styledField();
        titleField.setAlignmentX(LEFT_ALIGNMENT);
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        form.add(titleField);
        form.add(Box.createVerticalStrut(10));

        form.add(fieldLabel("Description *"));
        JTextArea descArea = UIFactory.styledArea(4, 50);
        JScrollPane descScroll = UIFactory.scrollPane(descArea);
        descScroll.setAlignmentX(LEFT_ALIGNMENT);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        descScroll.setBorder(new LineBorder(AppColors.BORDER, 1));
        form.add(descScroll);
        form.add(Box.createVerticalStrut(10));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setBackground(AppColors.BG_CARD);
        row1.setAlignmentX(LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JTextField locationField = UIFactory.styledField();
        JTextField workModeField = UIFactory.styledField();
        row1.add(vbox("Location *", locationField));
        row1.add(vbox("Work Mode *", workModeField));
        form.add(row1);
        form.add(Box.createVerticalStrut(10));

        form.add(fieldLabel("Salary Range *"));
        JTextField salaryField = UIFactory.styledField();
        salaryField.setAlignmentX(LEFT_ALIGNMENT);
        salaryField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        salaryField.setToolTipText("e.g. €50,000 – €70,000");
        form.add(salaryField);
        form.add(Box.createVerticalStrut(10));

        form.add(fieldLabel("Requirements (one per line, min 1) *"));
        JTextArea reqArea = UIFactory.styledArea(3, 50);
        JScrollPane reqScroll = UIFactory.scrollPane(reqArea);
        reqScroll.setAlignmentX(LEFT_ALIGNMENT);
        reqScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        reqScroll.setBorder(new LineBorder(AppColors.BORDER, 1));
        form.add(reqScroll);
        form.add(Box.createVerticalStrut(12));

        JLabel feedback = new JLabel(" ");
        feedback.setFont(AppColors.FONT_SMALL);
        feedback.setForeground(AppColors.DANGER);
        feedback.setAlignmentX(LEFT_ALIGNMENT);
        form.add(feedback);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(AppColors.BG_CARD);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton postBtn = UIFactory.primaryButton("Post Offer");
        postBtn.addActionListener(e -> {
            String offerTitle = titleField.getText().trim();
            String desc = descArea.getText().trim();
            String loc = locationField.getText().trim();
            String mode = workModeField.getText().trim();
            String sal = salaryField.getText().trim();
            java.util.List<String> reqList = new java.util.ArrayList<>();
            for (String r : reqArea.getText().trim().split("\\n"))
                if (!r.isBlank()) reqList.add(r.trim());

            if (offerTitle.isEmpty() || desc.isEmpty() || loc.isEmpty() || mode.isEmpty() || sal.isEmpty() || reqList.isEmpty()) {
                feedback.setText("Please fill in all required fields.");
                return;
            }
            try {
                String type = (String) typeCombo.getSelectedItem();
                JobOffer newOffer;
                switch (type) {
                    case "Contract":
                        newOffer = new ContractOffer(emp, offerTitle, desc, loc, mode, sal, reqList, 12, true);
                        break;
                    case "Freelance":
                        newOffer = new FreelanceOffer(emp, offerTitle, desc, loc, mode, sal, reqList, 0.0, false);
                        break;
                    default:
                        newOffer = new PermanentOffer(emp, offerTitle, desc, loc, mode, sal, reqList, 30, true);
                }
                newOffer.publish();
                DataStore.save();
                dialog.dispose();
                refresh();
            } catch (Exception ex) {
                feedback.setText(ex.getMessage());
            }
        });

        JButton cancelBtn = UIFactory.secondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(postBtn);
        btnRow.add(cancelBtn);
        form.add(btnRow);

        dialog.add(UIFactory.scrollPane(form), BorderLayout.CENTER);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(500, dialog.getHeight()));
        dialog.setVisible(true);
    }

    private void showCompanyDialog() {
        Employer emp = mainFrame.getCurrentEmployer();
        if (emp == null) return;
        Company company = emp.getCompany();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "My Company", true);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BG_CARD);
        form.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = UIFactory.headingLabel("Company Profile");
        title.setAlignmentX(LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(4));
        JLabel sub = UIFactory.mutedLabel("Your employer account: " + emp.getPlatformUser().getFullName() + "  (" + emp.getPositionTitle() + ")");
        sub.setAlignmentX(LEFT_ALIGNMENT);
        form.add(sub);
        form.add(Box.createVerticalStrut(18));

        form.add(fieldLabel("Company Name"));
        JLabel nameLabel = UIFactory.bodyLabel(company.getName());
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        form.add(nameLabel);
        form.add(Box.createVerticalStrut(12));

        form.add(fieldLabel("Location"));
        JLabel locLabel = UIFactory.bodyLabel(company.getLocation() != null ? company.getLocation() : "—");
        locLabel.setAlignmentX(LEFT_ALIGNMENT);
        form.add(locLabel);
        form.add(Box.createVerticalStrut(12));

        form.add(fieldLabel("Website"));
        JLabel webLabel = UIFactory.bodyLabel(company.getWebsite() != null ? company.getWebsite() : "—");
        webLabel.setAlignmentX(LEFT_ALIGNMENT);
        form.add(webLabel);
        form.add(Box.createVerticalStrut(12));

        form.add(fieldLabel("Verification Status"));
        String vs = company.getVerificationStatus();
        JLabel vsBadge = UIFactory.statusBadge(vs,
                "VERIFIED".equals(vs) ? AppColors.ACCENT
                        : "REJECTED".equals(vs) ? AppColors.DANGER
                          : AppColors.WARNING);
        vsBadge.setAlignmentX(LEFT_ALIGNMENT);
        form.add(vsBadge);
        form.add(Box.createVerticalStrut(16));

        form.add(fieldLabel("Description"));
        JTextArea descArea = UIFactory.styledArea(4, 50);
        descArea.setText(company.getDescription() != null ? company.getDescription() : "");
        JScrollPane descScroll = UIFactory.scrollPane(descArea);
        descScroll.setAlignmentX(LEFT_ALIGNMENT);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        descScroll.setBorder(new LineBorder(AppColors.BORDER, 1));
        form.add(descScroll);
        form.add(Box.createVerticalStrut(12));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        stats.setBackground(AppColors.BG_SIDEBAR);
        stats.setBorder(new EmptyBorder(10, 12, 10, 12));
        stats.setAlignmentX(LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        int totalOffers = emp.getJobOffers().size();
        long published = emp.getJobOffers().stream().filter(o -> o.getOfferStatus() == jobplatform.model.enums.OfferStatus.PUBLISHED).count();
        int totalApps = emp.getJobOffers().stream().mapToInt(o -> o.getApplications().size()).sum();

        stats.add(UIFactory.mutedLabel("Total offers: " + totalOffers));
        stats.add(UIFactory.mutedLabel("Published: " + published));
        stats.add(UIFactory.mutedLabel("Total applications: " + totalApps));
        form.add(stats);
        form.add(Box.createVerticalStrut(16));

        JLabel feedback = new JLabel(" ");
        feedback.setFont(AppColors.FONT_SMALL);
        feedback.setForeground(AppColors.DANGER);
        feedback.setAlignmentX(LEFT_ALIGNMENT);
        form.add(feedback);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(AppColors.BG_CARD);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton saveBtn = UIFactory.primaryButton("Save Description");
        saveBtn.addActionListener(e -> {
            company.setDescription(descArea.getText().trim());
            DataStore.save();
            feedback.setForeground(AppColors.ACCENT);
            feedback.setText("Description saved.");
        });
        JButton closeBtn = UIFactory.secondaryButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(saveBtn);
        btnRow.add(closeBtn);
        form.add(btnRow);

        dialog.add(UIFactory.scrollPane(form), BorderLayout.CENTER);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(480, dialog.getHeight()));
        dialog.setVisible(true);
    }

    private JLabel fieldLabel(String t) {
        JLabel l = UIFactory.fieldLabel(t);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }

    private JPanel vbox(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_CARD);
        JLabel l = UIFactory.fieldLabel(label);
        l.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private Color statusColor(ApplicationStatus s) {
        switch (s) {
            case SUBMITTED: return AppColors.STATUS_SUBMITTED;
            case UNDER_REVIEW: return AppColors.STATUS_REVIEW;
            case INTERVIEW_SCHEDULED: return AppColors.STATUS_INTERVIEW;
            case ACCEPTED: return AppColors.STATUS_ACCEPTED;
            case REJECTED: return AppColors.STATUS_REJECTED;
            default: return AppColors.TEXT_MUTED;
        }
    }

    private static class OfferCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setBorder(new EmptyBorder(10, 14, 10, 14));
            p.setBackground(isSelected ? AppColors.PRIMARY_LIGHT : (index % 2 == 0 ? AppColors.BG_SIDEBAR : AppColors.BG_CARD));
            if (value instanceof JobOffer) {
                JobOffer o = (JobOffer) value;
                JLabel name = new JLabel(o.getTitle());
                name.setFont(AppColors.FONT_SUBHEAD);
                name.setForeground(isSelected ? AppColors.PRIMARY : AppColors.TEXT_PRIMARY);
                Color bc = o.getOfferStatus() == OfferStatus.PUBLISHED ? AppColors.STATUS_PUBLISHED
                        : o.getOfferStatus() == OfferStatus.CLOSED ? AppColors.STATUS_CLOSED
                          : AppColors.STATUS_DRAFT;
                JLabel badge = UIFactory.statusBadge(o.getOfferStatus().toString(), bc);
                p.add(name,  BorderLayout.CENTER);
                p.add(badge, BorderLayout.EAST);
            }
            return p;
        }
    }
}