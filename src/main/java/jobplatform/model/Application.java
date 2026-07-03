package jobplatform.model;

import jobplatform.model.enums.ApplicationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Application extends BaseEntity {

    private static final List<Application> extent = new ArrayList<>();

    private LocalDateTime submittedAt;

    @Column(length = 3000)
    private String motivationMessage;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    private JobSeeker jobSeeker;

    @ManyToOne(fetch = FetchType.EAGER)
    private JobOffer jobOffer;

    @ManyToOne(fetch = FetchType.EAGER)
    private CV attachedCV;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Interview> interviews = new ArrayList<>();

    protected Application() {
    }

    Application(JobSeeker jobSeeker, JobOffer jobOffer, CV attachedCV, String motivationMessage) {
        if (jobSeeker == null) throw new IllegalArgumentException("Job seeker cannot be null.");
        if (jobOffer == null) throw new IllegalArgumentException("Job offer cannot be null.");
        if (attachedCV == null) throw new IllegalArgumentException("CV cannot be null.");

        this.jobSeeker = jobSeeker;
        this.jobOffer = jobOffer;
        this.attachedCV = attachedCV;
        this.motivationMessage = motivationMessage;
        this.applicationStatus = null;

        List<String> errors = validateBeforeSubmit();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid application data: " + String.join(", ", errors));
        }

        jobSeeker.addApplication(this);
        jobOffer.addApplication(this);

        extent.add(this);
    }

    public List<String> validateBeforeSubmit() {
        List<String> errors = new ArrayList<>();

        if (jobSeeker == null) errors.add("job seeker is missing");
        if (jobOffer == null) errors.add("job offer is missing");
        if (attachedCV == null) errors.add("CV is missing");

        if (motivationMessage == null || motivationMessage.isBlank()) {
            errors.add("motivation message is empty");
        } else if (motivationMessage.trim().length() < 20) {
            errors.add("motivation message is too short");
        }

        if (jobOffer != null && !jobOffer.isAccepting()) {
            errors.add("job offer is not accepting applications");
        }

        if (jobSeeker != null && attachedCV != null && !jobSeeker.getCandidateProfile().hasCV(attachedCV)) {
            errors.add("selected CV does not belong to the candidate profile");
        }

        return errors;
    }

    public void submit() {
        if (applicationStatus != null || submittedAt != null) {
            throw new IllegalStateException("Application has already been submitted.");
        }

        List<String> errors = validateBeforeSubmit();
        if (!errors.isEmpty()) {
            throw new IllegalStateException("Validation failed: " + String.join(", ", errors));
        }

        submittedAt = LocalDateTime.now();
        applicationStatus = ApplicationStatus.SUBMITTED;

        Employer employer = jobOffer.getCreatedBy();

        new Notification(
                "New application received for: " + jobOffer.getTitle()
                        + " from " + jobSeeker.getPlatformUser().getFullName(),
                "APPLICATION_SUBMITTED",
                employer.getPlatformUser()
        );
    }

    public void updateStatus(ApplicationStatus newStatus) {
        if (!isTransitionAllowed(applicationStatus, newStatus)) {
            throw new IllegalStateException("Invalid application status transition.");
        }

        applicationStatus = newStatus;
    }

    private boolean isTransitionAllowed(ApplicationStatus current, ApplicationStatus next) {
        if (current == ApplicationStatus.SUBMITTED && next == ApplicationStatus.UNDER_REVIEW) return true;
        if (current == ApplicationStatus.UNDER_REVIEW && next == ApplicationStatus.INTERVIEW_SCHEDULED) return true;
        if (current == ApplicationStatus.UNDER_REVIEW && next == ApplicationStatus.REJECTED) return true;
        if (current == ApplicationStatus.INTERVIEW_SCHEDULED && next == ApplicationStatus.ACCEPTED) return true;
        return current == ApplicationStatus.INTERVIEW_SCHEDULED && next == ApplicationStatus.REJECTED;
    }

    public void scheduleInterview(Interview interview) {
        if (interview == null) {
            throw new IllegalArgumentException("Interview cannot be null.");
        }

        if (applicationStatus != ApplicationStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Interview can be scheduled only after application is under review.");
        }

        if (!interviews.contains(interview)) {
            interviews.add(interview);
        }

        applicationStatus = ApplicationStatus.INTERVIEW_SCHEDULED;

        new Notification(
                "Interview scheduled for application: " + jobOffer.getTitle()
                        + " at " + interview.getScheduledAt(),
                "INTERVIEW_SCHEDULED",
                jobSeeker.getPlatformUser()
        );
    }

    public static List<Application> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<Application> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public String getMotivationMessage() {
        return motivationMessage;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public ApplicationStatus getStatus() {
        return applicationStatus;
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public JobOffer getJobOffer() {
        return jobOffer;
    }

    public CV getAttachedCV() {
        return attachedCV;
    }

    public List<Interview> getInterviews() {
        return Collections.unmodifiableList(interviews);
    }

    @Override
    public String toString() {
        return jobSeeker.getPlatformUser().getFullName()
                + " -> " + jobOffer.getTitle()
                + " (" + applicationStatus + ")";
    }
}