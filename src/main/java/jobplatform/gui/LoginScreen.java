package jobplatform.gui;

import jobplatform.model.*;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class LoginScreen extends JPanel {

    private final MainFrame mainFrame;
    private JComboBox<PlatformUser> userCombo;
    private JLabel roleInfoLabel;

    public LoginScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_PAGE);
        add(UIFactory.navBar("Job Platform", ""), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(AppColors.BG_PAGE);
        add(center, BorderLayout.CENTER);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppColors.BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(36, 44, 36, 44)
        ));
        card.setPreferredSize(new Dimension(460, 400));

        JLabel title = UIFactory.titleLabel("Welcome Back");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(370, 260));
        formPanel.setPreferredSize(new Dimension(370, 260));

        JLabel userLabel = UIFactory.fieldLabel("Select account:");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(6));

        List<PlatformUser> users = PlatformUser.getExtent();
        userCombo = new JComboBox<>(users.toArray(new PlatformUser[0]));
        userCombo.setFont(AppColors.FONT_BODY);
        userCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        userCombo.setMaximumSize(new Dimension(370, 36));
        userCombo.setPreferredSize(new Dimension(370, 36));
        userCombo.setRenderer(new UserRenderer());
        userCombo.addActionListener(e -> updateRoleInfo());
        formPanel.add(userCombo);
        formPanel.add(Box.createVerticalStrut(8));

        roleInfoLabel = UIFactory.mutedLabel(" ");
        roleInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleInfoLabel.setMaximumSize(new Dimension(370, 24));
        formPanel.add(roleInfoLabel);
        formPanel.add(Box.createVerticalStrut(22));

        JButton jobSeekerBtn = UIFactory.primaryButton("Sign In as Job Seeker");
        jobSeekerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        jobSeekerBtn.setMaximumSize(new Dimension(370, 44));
        jobSeekerBtn.setPreferredSize(new Dimension(370, 44));
        jobSeekerBtn.addActionListener(e -> loginAsJobSeeker());
        formPanel.add(jobSeekerBtn);
        formPanel.add(Box.createVerticalStrut(10));

        JButton employerBtn = UIFactory.secondaryButton("Sign In as Employer");
        employerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        employerBtn.setMaximumSize(new Dimension(370, 40));
        employerBtn.setPreferredSize(new Dimension(370, 40));
        employerBtn.addActionListener(e -> loginAsEmployer());
        formPanel.add(employerBtn);

        card.add(formPanel);

        center.add(card);
        updateRoleInfo();
    }

    private void updateRoleInfo() {
        PlatformUser pu = (PlatformUser) userCombo.getSelectedItem();
        if (pu == null) { roleInfoLabel.setText(" "); return; }
        StringBuilder sb = new StringBuilder();
        if (pu.isJobSeeker()) {
            sb.append("Job Seeker");
            boolean complete = pu.getJobSeekerRole().getCandidateProfile().isComplete();
            sb.append(complete ? "  (profile complete)" : "  profile incomplete");
        }
        if (pu.isJobSeeker() && pu.isEmployer()) sb.append("  |  ");
        if (pu.isEmployer()) sb.append("Employer @ " + pu.getEmployerRole().getCompany().getName());
        roleInfoLabel.setText(sb.toString());
    }

    private void loginAsJobSeeker() {
        PlatformUser pu = (PlatformUser) userCombo.getSelectedItem();
        if (pu == null) return;
        if (!pu.isJobSeeker()) {
            JOptionPane.showMessageDialog(this,
                    "This account has no Job Seeker role.", "Role not found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainFrame.setCurrentJobSeeker(pu.getJobSeekerRole());
        mainFrame.setCurrentEmployer(pu.isEmployer() ? pu.getEmployerRole() : null);
        mainFrame.showScreen(MainFrame.BROWSE_SCREEN);
    }

    private void loginAsEmployer() {
        PlatformUser pu = (PlatformUser) userCombo.getSelectedItem();
        if (pu == null) return;
        if (!pu.isEmployer()) {
            JOptionPane.showMessageDialog(this,
                    "This account has no Employer role.", "Role not found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainFrame.setCurrentEmployer(pu.getEmployerRole());
        mainFrame.setCurrentJobSeeker(pu.isJobSeeker() ? pu.getJobSeekerRole() : null);
        mainFrame.showScreen(MainFrame.EMPLOYER_DASHBOARD_SCREEN);
    }

    private static class UserRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PlatformUser) {
                PlatformUser pu = (PlatformUser) value;
                String roles = (pu.isJobSeeker() ? "JS" : "") + (pu.isEmployer() ? (pu.isJobSeeker() ? "+E" : "E") : "");
                setText("[" + roles + "]  " + pu.getFullName() + "  <" + pu.getEmail() + ">");
            }
            return this;
        }
    }
}
