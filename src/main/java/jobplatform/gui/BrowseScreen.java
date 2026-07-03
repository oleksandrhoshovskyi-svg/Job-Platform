package jobplatform.gui;

import jobplatform.model.*;
import jobplatform.model.enums.OfferStatus;
import jobplatform.util.AppColors;
import jobplatform.util.UIFactory;
import jobplatform.persistence.DataStore;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class BrowseScreen extends JPanel {

    private final MainFrame mainFrame;

    private final DefaultListModel<Company>  companyModel = new DefaultListModel<>();
    private final DefaultListModel<JobOffer> offerModel = new DefaultListModel<>();
    private JList<Company> companyList;
    private JList<JobOffer> offerList;
    private JLabel offerCountLabel;
    private JButton viewDetailsBtn;
    private JButton saveOfferBtn;
    private JLabel statusLabel;

    private JPanel currentNav;

    public BrowseScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BG_PAGE);

        currentNav = new JPanel();
        currentNav.setBackground(AppColors.PRIMARY);
        add(currentNav, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildCompanyPanel(), buildOfferPanel());
        split.setDividerLocation(280);
        split.setDividerSize(4);
        split.setBorder(BorderFactory.createEmptyBorder());
        add(split, BorderLayout.CENTER);

        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(AppColors.PRIMARY);
        nav.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        brand.setBackground(AppColors.PRIMARY);
        JLabel logo = new JLabel("Job Platform");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        brand.add(logo);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(AppColors.PRIMARY);

        String userName = "";
        boolean isJS = mainFrame.getCurrentJobSeeker() != null;
        boolean isE = mainFrame.getCurrentEmployer() != null;

        if (isJS) userName = mainFrame.getCurrentJobSeeker().getPlatformUser().getFullName();
        else if (isE) userName = mainFrame.getCurrentEmployer().getPlatformUser().getFullName();

        JLabel userLabel = new JLabel(userName);
        userLabel.setFont(AppColors.FONT_SMALL);
        userLabel.setForeground(new Color(186, 230, 253));
        actions.add(userLabel);
        actions.add(navSep());

        if (isJS) {
            actions.add(navBtn("My Profile", e -> mainFrame.showScreen(MainFrame.PROFILE_SCREEN)));
            actions.add(navBtn("Saved Offers", e -> mainFrame.showScreen(MainFrame.SAVED_OFFERS_SCREEN)));
            actions.add(navBtn("My Applications", e -> mainFrame.showScreen(MainFrame.MY_APPLICATIONS_SCREEN)));
        }

        if (isJS && isE) {
            actions.add(navSep());
        }

        if (isE) {
            actions.add(navBtn("Employer Dashboard", e -> mainFrame.showScreen(MainFrame.EMPLOYER_DASHBOARD_SCREEN)));
        }

        actions.add(navSep());
        actions.add(navBtn("Sign Out", e -> mainFrame.showScreen(MainFrame.LOGIN_SCREEN)));

        nav.add(brand, BorderLayout.WEST);
        nav.add(actions, BorderLayout.EAST);
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
        l.setForeground(new Color(100, 130, 170));
        return l;
    }

    private JPanel buildCompanyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.BG_SIDEBAR);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(14, 16, 10, 16));
        JLabel hLabel = UIFactory.headingLabel("Companies");
        JLabel count = UIFactory.mutedLabel(Company.getExtent().size() + " registered");
        header.add(hLabel, BorderLayout.WEST);
        header.add(count, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        companyList = new JList<>(companyModel);
        companyList.setFont(AppColors.FONT_BODY);
        companyList.setBackground(AppColors.BG_SIDEBAR);
        companyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companyList.setCellRenderer(new CompanyRenderer());
        companyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadOffersForCompany();
        });

        panel.add(UIFactory.scrollPane(companyList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildOfferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.BG_PAGE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_PAGE);
        header.setBorder(new EmptyBorder(14, 20, 10, 20));
        JLabel hLabel = UIFactory.headingLabel("Job Offers");
        offerCountLabel = UIFactory.mutedLabel("Select a company");
        header.add(hLabel, BorderLayout.WEST);
        header.add(offerCountLabel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        offerList = new JList<>(offerModel);
        offerList.setFont(AppColors.FONT_BODY);
        offerList.setBackground(AppColors.BG_PAGE);
        offerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        offerList.setCellRenderer(new OfferRenderer());
        offerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtons();
        });

        JPanel listWrap = new JPanel(new BorderLayout());
        listWrap.setBackground(AppColors.BG_PAGE);
        listWrap.setBorder(new EmptyBorder(0, 14, 0, 14));
        listWrap.add(UIFactory.scrollPane(offerList), BorderLayout.CENTER);
        panel.add(listWrap, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomBar() {
        statusLabel = UIFactory.mutedLabel(" ");

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppColors.BG_CARD);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDER));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        left.setBackground(AppColors.BG_CARD);
        left.add(statusLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        right.setBackground(AppColors.BG_CARD);

        saveOfferBtn = UIFactory.secondaryButton("Save Offer");
        saveOfferBtn.setEnabled(false);
        saveOfferBtn.addActionListener(e -> saveSelectedOffer());

        viewDetailsBtn = UIFactory.primaryButton("View Job Offer Details  →");
        viewDetailsBtn.setEnabled(false);
        viewDetailsBtn.addActionListener(e -> openSelectedOffer());

        right.add(saveOfferBtn);
        right.add(viewDetailsBtn);
        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }


    private void loadCompanies() {
        companyModel.clear();
        for (Company c : Company.getExtent()) companyModel.addElement(c);
        if (!companyModel.isEmpty()) {
            companyList.setSelectedIndex(0);
            loadOffersForCompany();
        }
    }

    private void loadOffersForCompany() {
        offerModel.clear();
        viewDetailsBtn.setEnabled(false);
        saveOfferBtn.setEnabled(false);
        statusLabel.setText(" ");

        Company selected = companyList.getSelectedValue();
        if (selected == null) { offerCountLabel.setText("Select a company"); return; }

        for (Employer emp : selected.getEmployers()) {
            for (JobOffer offer : emp.getJobOffers()) {
                offerModel.addElement(offer);
            }
        }
        int count = offerModel.size();
        offerCountLabel.setText(count + " offer" + (count != 1 ? "s" : ""));
    }

    private void updateButtons() {
        JobOffer offer = offerList.getSelectedValue();
        boolean selected = offer != null;
        viewDetailsBtn.setEnabled(selected);

        boolean isJS = mainFrame.getCurrentJobSeeker() != null;
        if (selected && isJS) {
            JobSeeker js = mainFrame.getCurrentJobSeeker();
            boolean alreadySaved = js.getSavedOffers().stream()
                    .anyMatch(so -> so.getJobOffer().equals(offer));
            saveOfferBtn.setEnabled(!alreadySaved && offer.isAccepting());
            saveOfferBtn.setText(alreadySaved ? "Saved" : "Save Offer");
        } else {
            saveOfferBtn.setEnabled(false);
            saveOfferBtn.setText("Save Offer");
        }
        statusLabel.setText(" ");
    }

    private void openSelectedOffer() {
        JobOffer offer = offerList.getSelectedValue();
        if (offer == null) return;
        mainFrame.setCurrentOffer(offer);
        mainFrame.showScreen(MainFrame.OFFER_DETAILS_SCREEN);
    }

    private void saveSelectedOffer() {
        JobOffer offer = offerList.getSelectedValue();
        JobSeeker js = mainFrame.getCurrentJobSeeker();

        if (offer == null || js == null) {
            return;
        }

        String note = JOptionPane.showInputDialog(
                this,
                "Add a note for this saved offer:",
                "Save Job Offer",
                JOptionPane.PLAIN_MESSAGE
        );

        if (note == null) {
            return;
        }

        try {
            js.saveJobOffer(offer, note);
            DataStore.save();

            statusLabel.setText("Offer saved.");
            statusLabel.setForeground(AppColors.ACCENT);
            updateButtons();
        } catch (RuntimeException ex) {
            statusLabel.setText(ex.getMessage());
            statusLabel.setForeground(AppColors.WARNING);
        }
    }


    public void refresh() {
        remove(currentNav);
        currentNav = buildNav();
        add(currentNav, BorderLayout.NORTH);

        saveOfferBtn.setVisible(mainFrame.getCurrentJobSeeker() != null);

        loadCompanies();

        if (companyModel.isEmpty()) {
            offerModel.clear();
            offerCountLabel.setText("No companies");
            viewDetailsBtn.setEnabled(false);
            saveOfferBtn.setEnabled(false);
        } else {
            updateButtons();
        }

        statusLabel.setText(" ");

        revalidate();
        repaint();
    }

    private static class CompanyRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel p = new JPanel(new BorderLayout(0, 2));
            p.setBorder(new EmptyBorder(10, 16, 10, 16));
            p.setBackground(isSelected ? AppColors.PRIMARY_LIGHT
                    : (index % 2 == 0 ? AppColors.BG_SIDEBAR : AppColors.BG_CARD));
            if (value instanceof Company) {
                Company c = (Company) value;
                JLabel name = new JLabel(c.getName());
                name.setFont(AppColors.FONT_SUBHEAD);
                name.setForeground(isSelected ? AppColors.PRIMARY : AppColors.TEXT_PRIMARY);
                JLabel loc = new JLabel(c.getLocation());
                loc.setFont(AppColors.FONT_SMALL);
                loc.setForeground(AppColors.TEXT_SECONDARY);
                p.add(name, BorderLayout.CENTER);
                p.add(loc, BorderLayout.SOUTH);
            }
            return p;
        }
    }

    private static class OfferRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setBorder(new EmptyBorder(12, 16, 12, 16));
            p.setBackground(isSelected ? AppColors.PRIMARY_LIGHT : AppColors.BG_PAGE);
            if (value instanceof JobOffer) {
                JobOffer o = (JobOffer) value;
                JPanel left = new JPanel();
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                left.setBackground(p.getBackground());

                JLabel title = new JLabel(o.getTitle());
                title.setFont(AppColors.FONT_SUBHEAD);
                title.setForeground(isSelected ? AppColors.PRIMARY : AppColors.TEXT_PRIMARY);

                String type = o.getClass().getSimpleName().replace("Offer", "");
                JLabel meta = new JLabel(o.getLocation() + " · " + o.getWorkMode()
                        + " · " + o.getSalaryRange() + " · " + type);
                meta.setFont(AppColors.FONT_SMALL);
                meta.setForeground(AppColors.TEXT_SECONDARY);

                left.add(title);
                left.add(Box.createVerticalStrut(3));
                left.add(meta);

                Color bc = o.getOfferStatus() == OfferStatus.PUBLISHED ? AppColors.STATUS_PUBLISHED
                        : o.getOfferStatus() == OfferStatus.CLOSED ? AppColors.STATUS_CLOSED
                          : AppColors.STATUS_DRAFT;
                JLabel badge = UIFactory.statusBadge(o.getOfferStatus().toString(), bc);

                p.add(left, BorderLayout.CENTER);
                p.add(badge, BorderLayout.EAST);
            }
            return p;
        }
    }
}