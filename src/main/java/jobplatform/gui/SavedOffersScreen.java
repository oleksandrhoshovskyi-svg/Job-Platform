package jobplatform.gui;

import jobplatform.model.*;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;
import jobplatform.persistence.DataStore;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SavedOffersScreen extends JPanel {

    private final MainFrame mainFrame;
    private JPanel offersPanel;
    private JLabel countLabel;

    public SavedOffersScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_PAGE);
        add(buildNav(), BorderLayout.NORTH);

        JPanel content = UIFactory.pageBackground();
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 32, 20, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_PAGE);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel title = UIFactory.headingLabel("Saved Job Offers");
        countLabel = UIFactory.mutedLabel("");
        header.add(title, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));
        offersPanel.setBackground(AppColors.BG_PAGE);

        content.add(UIFactory.scrollPane(offersPanel), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(AppColors.PRIMARY);
        nav.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel logo = new JLabel("Job Platform  /  Saved Offers");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setBackground(AppColors.PRIMARY);
        JButton back = navBtn("Browse Offers");
        back.addActionListener(e -> mainFrame.showScreen(MainFrame.BROWSE_SCREEN));
        right.add(back);
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

    public void refresh() {
        JobSeeker js = mainFrame.getCurrentJobSeeker();
        if (js == null) return;

        offersPanel.removeAll();
        List<SavedOffer> saved = js.getSavedOffers();
        countLabel.setText(saved.size() + " saved offer" + (saved.size() != 1 ? "s" : ""));

        if (saved.isEmpty()) {
            JLabel empty = UIFactory.mutedLabel("You haven't saved any offers yet. Browse offers and click the bookmark icon to save.");
            empty.setAlignmentX(LEFT_ALIGNMENT);
            empty.setBorder(new EmptyBorder(20, 0, 0, 0));
            offersPanel.add(empty);
        } else {
            for (SavedOffer so : saved) {
                offersPanel.add(buildSavedOfferCard(so, js));
                offersPanel.add(Box.createVerticalStrut(10));
            }
        }

        revalidate();
        repaint();
    }

    private JPanel buildSavedOfferCard(SavedOffer so, JobSeeker js) {
        JPanel card = UIFactory.card();
        card.setLayout(new BorderLayout(16, 0));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(AppColors.BG_CARD);

        JLabel title = UIFactory.headingLabel(so.getJobOffer().getTitle());
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel company = UIFactory.bodyLabel(so.getJobOffer().getCreatedBy().getCompany().getName()
                + "  ·  " + so.getJobOffer().getLocation()
                + "  ·  " + so.getJobOffer().getWorkMode());
        company.setForeground(AppColors.TEXT_SECONDARY);
        company.setAlignmentX(LEFT_ALIGNMENT);

        JLabel salary = UIFactory.bodyLabel(so.getJobOffer().getSalaryRange());
        salary.setForeground(AppColors.PRIMARY);
        salary.setAlignmentX(LEFT_ALIGNMENT);

        String noteText = (so.getNote() != null && !so.getNote().isBlank()) ? "Note: " + so.getNote() : "";
        JLabel note = UIFactory.mutedLabel(noteText);
        note.setAlignmentX(LEFT_ALIGNMENT);

        JLabel savedDate = UIFactory.mutedLabel("Saved " + so.getSavedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        savedDate.setAlignmentX(LEFT_ALIGNMENT);

        info.add(title);
        info.add(Box.createVerticalStrut(4));
        info.add(company);
        info.add(salary);
        if (!noteText.isEmpty()) { info.add(Box.createVerticalStrut(3)); info.add(note); }
        info.add(Box.createVerticalStrut(4));
        info.add(savedDate);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(AppColors.BG_CARD);

        boolean accepting = so.getJobOffer().isAccepting();
        Color badgeColor = accepting ? AppColors.STATUS_PUBLISHED : AppColors.STATUS_CLOSED;
        JLabel statusBadge = UIFactory.statusBadge(so.getJobOffer().getOfferStatus().toString(), badgeColor);
        statusBadge.setAlignmentX(RIGHT_ALIGNMENT);
        right.add(statusBadge);
        right.add(Box.createVerticalStrut(8));

        JButton viewBtn = UIFactory.primaryButton("View Offer");
        viewBtn.setAlignmentX(RIGHT_ALIGNMENT);
        viewBtn.addActionListener(e -> {
            mainFrame.setCurrentOffer(so.getJobOffer());
            mainFrame.showScreen(MainFrame.OFFER_DETAILS_SCREEN);
        });
        right.add(viewBtn);

        if (accepting && !js.hasAlreadyApplied(so.getJobOffer())) {
            right.add(Box.createVerticalStrut(6));
            JButton applyBtn = UIFactory.secondaryButton("Apply Now");
            applyBtn.setAlignmentX(RIGHT_ALIGNMENT);
            applyBtn.addActionListener(e -> {
                mainFrame.setCurrentOffer(so.getJobOffer());
                if (!js.getCandidateProfile().isComplete()) {
                    mainFrame.showScreen(MainFrame.COMPLETE_PROFILE_SCREEN);
                } else {
                    mainFrame.showScreen(MainFrame.SUBMIT_APPLICATION_SCREEN);
                }
            });
            right.add(applyBtn);
        }

        right.add(Box.createVerticalStrut(6));

        JButton removeBtn = UIFactory.secondaryButton("Remove");
        removeBtn.setAlignmentX(RIGHT_ALIGNMENT);
        removeBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Remove this job offer from saved offers?",
                    "Remove Saved Offer",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    js.removeSavedOffer(so);
                    DataStore.delete(so);
                    DataStore.save();
                    refresh();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        right.add(removeBtn);

        card.add(info, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }
}
