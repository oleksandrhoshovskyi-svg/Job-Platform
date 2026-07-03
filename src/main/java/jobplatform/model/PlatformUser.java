package jobplatform.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class PlatformUser extends BaseEntity {

    private static final List<PlatformUser> extent = new ArrayList<>();

    private String email;
    private String fullName;
    private String accountStatus;

    @OneToOne(mappedBy = "platformUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private JobSeeker jobSeekerRole;

    @OneToOne(mappedBy = "platformUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employer employerRole;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Notification> notifications = new ArrayList<>();

    protected PlatformUser() {
    }

    public PlatformUser(String email, String fullName) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email address.");
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Full name cannot be empty.");

        this.email = email;
        this.fullName = fullName;
        this.accountStatus = "ACTIVE";

        extent.add(this);
    }

    public void register() {
        accountStatus = "ACTIVE";
    }

    public boolean login(String emailAttempt) {
        return email.equalsIgnoreCase(emailAttempt);
    }

    public List<Notification> viewNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    public List<Notification> getUnreadNotifications() {
        List<Notification> unread = new ArrayList<>();
        for (Notification notification : notifications) {
            if (!notification.isReadStatus()) unread.add(notification);
        }
        return unread;
    }

    void setJobSeekerRole(JobSeeker role) {
        jobSeekerRole = role;
    }

    void setEmployerRole(Employer role) {
        employerRole = role;
    }

    public boolean isJobSeeker() {
        return jobSeekerRole != null;
    }

    public boolean isEmployer() {
        return employerRole != null;
    }

    void addNotification(Notification notification) {
        if (!notifications.contains(notification)) {
            notifications.add(notification);
        }
    }

    public static List<PlatformUser> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<PlatformUser> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email.");
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Full name cannot be empty.");
        this.fullName = fullName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public JobSeeker getJobSeekerRole() {
        return jobSeekerRole;
    }

    public Employer getEmployerRole() {
        return employerRole;
    }

    public List<Notification> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    @Override
    public String toString() {
        return fullName + " <" + email + ">"
                + (isJobSeeker() ? " [JobSeeker]" : "")
                + (isEmployer() ? " [Employer]" : "");
    }
}