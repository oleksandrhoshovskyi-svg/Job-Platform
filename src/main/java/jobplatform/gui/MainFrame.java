package jobplatform.gui;

import jobplatform.model.Application;
import jobplatform.model.Employer;
import jobplatform.model.JobOffer;
import jobplatform.model.PlatformUser;
import jobplatform.model.JobSeeker;
import jobplatform.util.AppColors;
import jobplatform.persistence.DataStore;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public static final String LOGIN_SCREEN = "LOGIN";
    public static final String BROWSE_SCREEN = "BROWSE";
    public static final String OFFER_DETAILS_SCREEN = "OFFER_DETAILS";
    public static final String COMPLETE_PROFILE_SCREEN = "COMPLETE_PROFILE";
    public static final String SUBMIT_APPLICATION_SCREEN = "SUBMIT_APPLICATION";
    public static final String CONFIRMATION_SCREEN = "CONFIRMATION";
    public static final String MY_APPLICATIONS_SCREEN = "MY_APPLICATIONS";
    public static final String PROFILE_SCREEN = "PROFILE";
    public static final String SAVED_OFFERS_SCREEN = "SAVED_OFFERS";
    public static final String EMPLOYER_DASHBOARD_SCREEN = "EMPLOYER_DASHBOARD";

    private JobSeeker currentJobSeeker;
    private Employer currentEmployer;
    private JobOffer currentOffer;
    private Application lastApplication;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel  = new JPanel(cardLayout);

    private LoginScreen loginScreen;
    private BrowseScreen browseScreen;
    private JobOfferDetailsScreen offerDetailsScreen;
    private CompleteProfileScreen completeProfileScreen;
    private SubmitApplicationScreen submitApplicationScreen;
    private ConfirmationScreen confirmationScreen;
    private MyApplicationsScreen myApplicationsScreen;
    private ProfileScreen profileScreen;
    private SavedOffersScreen savedOffersScreen;
    private EmployerDashboardScreen employerDashboardScreen;

    public MainFrame() {
        super("Job Platform Management System");
        initFrame();
        buildScreens();
        setVisible(true);
        showScreen(LOGIN_SCREEN);
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 740);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_PAGE);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("Label.font", AppColors.FONT_BODY);
        UIManager.put("Button.font", AppColors.FONT_BODY);
        UIManager.put("ComboBox.font", AppColors.FONT_BODY);
        UIManager.put("TextField.font", AppColors.FONT_BODY);
        UIManager.put("TextArea.font", AppColors.FONT_BODY);
        UIManager.put("List.font", AppColors.FONT_BODY);
        UIManager.put("Table.font", AppColors.FONT_BODY);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataStore.save();
                DataStore.shutdown();
                dispose();
                System.exit(0);
            }
        });

        add(cardPanel);
    }

    private void buildScreens() {
        loginScreen = new LoginScreen(this);
        browseScreen = new BrowseScreen(this);
        offerDetailsScreen = new JobOfferDetailsScreen(this);
        completeProfileScreen = new CompleteProfileScreen(this);
        submitApplicationScreen = new SubmitApplicationScreen(this);
        confirmationScreen = new ConfirmationScreen(this);
        myApplicationsScreen = new MyApplicationsScreen(this);
        profileScreen = new ProfileScreen(this);
        savedOffersScreen = new SavedOffersScreen(this);
        employerDashboardScreen = new EmployerDashboardScreen(this);

        cardPanel.add(loginScreen, LOGIN_SCREEN);
        cardPanel.add(browseScreen, BROWSE_SCREEN);
        cardPanel.add(offerDetailsScreen, OFFER_DETAILS_SCREEN);
        cardPanel.add(completeProfileScreen, COMPLETE_PROFILE_SCREEN);
        cardPanel.add(submitApplicationScreen, SUBMIT_APPLICATION_SCREEN);
        cardPanel.add(confirmationScreen, CONFIRMATION_SCREEN);
        cardPanel.add(myApplicationsScreen, MY_APPLICATIONS_SCREEN);
        cardPanel.add(profileScreen, PROFILE_SCREEN);
        cardPanel.add(savedOffersScreen, SAVED_OFFERS_SCREEN);
        cardPanel.add(employerDashboardScreen, EMPLOYER_DASHBOARD_SCREEN);
    }

    public void showScreen(String name) {
        cardLayout.show(cardPanel, name);
        switch (name) {
            case BROWSE_SCREEN: browseScreen.refresh(); break;
            case OFFER_DETAILS_SCREEN: offerDetailsScreen.refresh(); break;
            case COMPLETE_PROFILE_SCREEN: completeProfileScreen.refresh(); break;
            case SUBMIT_APPLICATION_SCREEN: submitApplicationScreen.refresh(); break;
            case CONFIRMATION_SCREEN: confirmationScreen.refresh(); break;
            case MY_APPLICATIONS_SCREEN: myApplicationsScreen.refresh(); break;
            case PROFILE_SCREEN: profileScreen.refresh(); break;
            case SAVED_OFFERS_SCREEN: savedOffersScreen.refresh(); break;
            case EMPLOYER_DASHBOARD_SCREEN:
                if (currentEmployer == null && currentJobSeeker != null) {
                    PlatformUser pu = currentJobSeeker.getPlatformUser();
                    if (pu.isEmployer()) currentEmployer = pu.getEmployerRole();
                }
                employerDashboardScreen.refresh();
                break;
        }
    }


    public JobSeeker getCurrentJobSeeker() { return currentJobSeeker; }
    public void setCurrentJobSeeker(JobSeeker js) { this.currentJobSeeker = js; }

    public Employer getCurrentEmployer() { return currentEmployer; }
    public void setCurrentEmployer(Employer e) { this.currentEmployer = e; }

    public JobOffer getCurrentOffer() { return currentOffer; }
    public void setCurrentOffer(JobOffer o) { this.currentOffer = o; }

    public Application getLastApplication() { return lastApplication; }
    public void setLastApplication(Application a) { this.lastApplication = a; }
}