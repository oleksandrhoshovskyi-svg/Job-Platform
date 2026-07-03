package jobplatform.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class JobSeeker extends BaseEntity {

    private static final List<JobSeeker> extent = new ArrayList<>();

    private String preferredPosition;
    private String location;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private CandidateProfile candidateProfile;

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<SavedOffer> savedOffers = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "platform_user_id")
    private PlatformUser platformUser;

    protected JobSeeker() {
    }

    public JobSeeker(PlatformUser platformUser, String preferredPosition, String location) {
        if (platformUser == null) throw new IllegalArgumentException("PlatformUser cannot be null.");

        this.platformUser = platformUser;
        this.preferredPosition = preferredPosition;
        this.location = location;

        platformUser.setJobSeekerRole(this);
        candidateProfile = new CandidateProfile(this);

        extent.add(this);
    }

    public boolean hasAlreadyApplied(JobOffer offer) {
        for (Application application : applications) {
            if (application.getJobOffer().equals(offer)) return true;
        }
        return false;
    }

    public Application submitApplication(JobOffer offer, CV selectedCV, String motivationMessage) {
        if (offer == null) throw new IllegalArgumentException("Job offer is required.");
        if (selectedCV == null) throw new IllegalArgumentException("Please select a CV.");
        if (!offer.isAccepting()) throw new IllegalStateException("The job offer is no longer available.");
        if (hasAlreadyApplied(offer)) throw new IllegalStateException("You have already applied for this job offer.");
        if (!candidateProfile.isComplete()) throw new IllegalStateException("PROFILE_INCOMPLETE");
        if (!candidateProfile.getCVs().contains(selectedCV)) {
            throw new IllegalArgumentException("The selected CV does not belong to your profile.");
        }
        if (motivationMessage == null || motivationMessage.isBlank()) {
            throw new IllegalArgumentException("Motivation message cannot be empty.");
        }
        if (motivationMessage.trim().length() < 20) {
            throw new IllegalArgumentException("Motivation message must be at least 20 characters.");
        }

        Application application = new Application(this, offer, selectedCV, motivationMessage.trim());
        application.submit();
        return application;
    }

    public SavedOffer saveJobOffer(JobOffer offer, String note) {
        if (offer == null) throw new IllegalArgumentException("Job offer cannot be null.");

        for (SavedOffer savedOffer : savedOffers) {
            if (savedOffer.getJobOffer().equals(offer)) {
                throw new IllegalStateException("Offer already saved.");
            }
        }

        return new SavedOffer(this, offer, note);
    }

    public void removeSavedOffer(SavedOffer savedOffer) {
        if (savedOffer == null) return;

        if (!savedOffers.contains(savedOffer)) {
            throw new IllegalArgumentException("This saved offer does not belong to this job seeker.");
        }

        savedOffers.remove(savedOffer);
        SavedOffer.removeFromExtent(savedOffer);
    }

    public void removeSavedOffer(JobOffer offer) {
        if (offer == null) return;

        SavedOffer found = null;

        for (SavedOffer savedOffer : savedOffers) {
            if (savedOffer.getJobOffer().equals(offer)) {
                found = savedOffer;
                break;
            }
        }

        if (found == null) throw new IllegalStateException("This offer is not saved.");

        removeSavedOffer(found);
    }

    void addApplication(Application application) {
        if (!applications.contains(application)) applications.add(application);
    }

    void addSavedOffer(SavedOffer savedOffer) {
        if (!savedOffers.contains(savedOffer)) savedOffers.add(savedOffer);
    }

    public static List<JobSeeker> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<JobSeeker> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public PlatformUser getPlatformUser() {
        return platformUser;
    }

    public CandidateProfile getCandidateProfile() {
        return candidateProfile;
    }

    public List<Application> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    public List<SavedOffer> getSavedOffers() {
        return Collections.unmodifiableList(savedOffers);
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "JobSeeker[" + platformUser.getFullName() + "]";
    }
}