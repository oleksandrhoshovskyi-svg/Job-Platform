package jobplatform.gui;

import jobplatform.model.Application;
import jobplatform.model.Interview;
import jobplatform.model.JobSeeker;
import jobplatform.model.Notification;
import jobplatform.model.enums.ApplicationStatus;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyApplicationsScreen extends JPanel {

    private final MainFrame mainFrame;
    private JPanel applicationsPanel;
    private JPanel notificationsPanel;
    private JLabel appCountLabel;
    private JLabel notifCountLabel;

    public MyApplicationsScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_PAGE);
        add(buildNav(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildApplicationsPanel(), buildNotificationsPanel());
        split.setDividerLocation(600);
        split.setDividerSize(4);
        split.setBorder(BorderFactory.createEmptyBorder());
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(AppColors.PRIMARY);
        nav.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel logo = new JLabel("Job Platform  /  My Applications");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setBackground(AppColors.PRIMARY);

        JButton browseBtn = new JButton("Browse Offers");
        styleNav(browseBtn);
        browseBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.BROWSE_SCREEN));
        right.add(browseBtn);

        nav.add(logo, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private void styleNav(JButton b) {
        b.setFont(AppColors.FONT_BODY);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBackground(new Color(0, 0, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel buildApplicationsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_PAGE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_PAGE);
        header.setBorder(new EmptyBorder(16, 20, 12, 20));
        JLabel hLabel = UIFactory.headingLabel("My Applications");
        appCountLabel = UIFactory.mutedLabel("");
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

    private JPanel buildNotificationsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_SIDEBAR);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 16, 12, 16));
        JLabel hLabel = UIFactory.headingLabel("Notifications");
        notifCountLabel = UIFactory.mutedLabel("");
        header.add(hLabel, BorderLayout.WEST);
        header.add(notifCountLabel, BorderLayout.EAST);
        outer.add(header, BorderLayout.NORTH);

        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setBackground(AppColors.BG_SIDEBAR);
        notificationsPanel.setBorder(new EmptyBorder(0, 12, 12, 12));

        outer.add(UIFactory.scrollPane(notificationsPanel), BorderLayout.CENTER);
        return outer;
    }

    public void refresh() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;

        applicationsPanel.removeAll();
        List<Application> apps = js.getApplications();
        appCountLabel.setText(apps.size() + " application" + (apps.size() != 1 ? "s" : ""));

        if (apps.isEmpty()) {
            JLabel empty = UIFactory.mutedLabel("You haven't submitted any applications yet.");
            empty.setAlignmentX(LEFT_ALIGNMENT);
            empty.setBorder(new EmptyBorder(16, 0, 0, 0));
            applicationsPanel.add(empty);
        } else {
            for (Application app : apps) {
                applicationsPanel.add(buildApplicationCard(app));
                applicationsPanel.add(Box.createVerticalStrut(10));
            }
        }

        notificationsPanel.removeAll();
        List<Notification> notifs = js.getPlatformUser().getNotifications();
        long unread = notifs.stream().filter(n -> !n.isReadStatus()).count();
        notifCountLabel.setText(unread + " unread");

        if (notifs.isEmpty()) {
            JLabel empty = UIFactory.mutedLabel("No notifications.");
            empty.setBorder(new EmptyBorder(16, 4, 0, 0));
            notificationsPanel.add(empty);
        } else {
            for (int i = notifs.size() - 1; i >= 0; i--) {
                Notification n = notifs.get(i);
                notificationsPanel.add(buildNotificationCard(n));
                notificationsPanel.add(Box.createVerticalStrut(6));
            }
        }

        revalidate();
        repaint();
    }

    private JPanel buildApplicationCard(Application app) {
        boolean hasInterview = app.getApplicationStatus() == ApplicationStatus.INTERVIEW_SCHEDULED
                && !app.getInterviews().isEmpty();

        JPanel card = UIFactory.card();
        card.setLayout(new BorderLayout(12, 4));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, hasInterview ? 140 : 110));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(AppColors.BG_CARD);

        JLabel title = UIFactory.headingLabel(app.getJobOffer().getTitle());
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel company = UIFactory.bodyLabel(app.getJobOffer().getCreatedBy().getCompany().getName() + " · " + app.getJobOffer().getLocation());
        company.setForeground(AppColors.TEXT_SECONDARY);
        company.setAlignmentX(LEFT_ALIGNMENT);

        String dateStr = app.getSubmittedAt() != null
                ? "Submitted: " + app.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                : "Pending submission";
        JLabel date = UIFactory.mutedLabel(dateStr);
        date.setAlignmentX(LEFT_ALIGNMENT);

        info.add(title);
        info.add(Box.createVerticalStrut(3));
        info.add(company);
        info.add(Box.createVerticalStrut(3));
        info.add(date);

        if (hasInterview) {
            Interview interview = app.getInterviews().get(app.getInterviews().size() - 1);
            long daysUntil = java.time.LocalDate.now()
                    .until(interview.getScheduledAt().toLocalDate(), java.time.temporal.ChronoUnit.DAYS);
            String when = daysUntil == 0 ? "Today"
                    : daysUntil == 1 ? "Tomorrow"
                      : "In " + daysUntil + " days";
            String interviewStr = "Interview: " + interview.getInterviewFormat()
                    + "  ·  " + when + "  ("
                    + interview.getScheduledAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")) + ")";
            JLabel interviewLabel = new JLabel(interviewStr);
            interviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            interviewLabel.setForeground(AppColors.STATUS_INTERVIEW);
            interviewLabel.setAlignmentX(LEFT_ALIGNMENT);
            info.add(Box.createVerticalStrut(5));
            info.add(interviewLabel);
        }

        Color badgeColor = statusColor(app.getApplicationStatus());
        JLabel badge = UIFactory.statusBadge(app.getApplicationStatus().toString().replace("_", " "), badgeColor);

        card.add(info, BorderLayout.CENTER);
        card.add(badge, BorderLayout.EAST);
        return card;
    }

    private JPanel buildNotificationCard(Notification n) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(n.isReadStatus() ? AppColors.BG_SIDEBAR : AppColors.PRIMARY_LIGHT);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(card.getBackground());

        JLabel msg = new JLabel("<html>" + n.getMessage() + "</html>");
        msg.setFont(n.isReadStatus() ? AppColors.FONT_BODY : AppColors.FONT_SUBHEAD);
        msg.setForeground(AppColors.TEXT_PRIMARY);

        JLabel type = UIFactory.mutedLabel(n.getNotificationType().toString().replace("_", " ")
                + "  ·  " + n.getSentAt().format(DateTimeFormatter.ofPattern("dd MMM HH:mm")));

        text.add(msg);
        text.add(type);

        JLabel dot = new JLabel(n.isReadStatus() ? "" : "●");
        dot.setForeground(AppColors.PRIMARY);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 8));

        card.add(text, BorderLayout.CENTER);
        card.add(dot, BorderLayout.WEST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                n.markAsRead();
                refresh();
            }
        });

        return card;
    }

    private Color statusColor(ApplicationStatus status) {
        switch (status) {
            case SUBMITTED: return AppColors.STATUS_SUBMITTED;
            case UNDER_REVIEW: return AppColors.STATUS_REVIEW;
            case INTERVIEW_SCHEDULED: return AppColors.STATUS_INTERVIEW;
            case ACCEPTED: return AppColors.STATUS_ACCEPTED;
            case REJECTED: return AppColors.STATUS_REJECTED;
            default: return AppColors.TEXT_MUTED;
        }
    }
}