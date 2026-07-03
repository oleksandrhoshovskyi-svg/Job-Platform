package jobplatform.model;

import jobplatform.model.enums.OfferStatus;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class JobOffer extends BaseEntity {

    private static final List<JobOffer> extent = new ArrayList<>();

    private String title;

    @Column(length = 3000)
    private String description;

    private String location;
    private String workMode;
    private String salaryRange;

    @Enumerated(EnumType.STRING)
    private OfferStatus offerStatus;

    private LocalDateTime publishedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_offer_requirements", joinColumns = @JoinColumn(name = "job_offer_id"))
    @Column(name = "requirement_text", length = 1000)
    @Fetch(FetchMode.SUBSELECT)
    private List<String> requirements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Employer createdBy;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private RecruitmentProcess recruitmentProcess;

    @OneToMany(mappedBy = "jobOffer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Application> applications = new ArrayList<>();

    protected JobOffer() {
    }

    protected JobOffer(
            Employer createdBy,
            String title,
            String description,
            String location,
            String workMode,
            String salaryRange,
            List<String> requirements,
            RecruitmentProcess recruitmentProcess
    ) {
        if (createdBy == null) throw new IllegalArgumentException("Employer cannot be null.");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Job title cannot be empty.");
        if (requirements == null || requirements.isEmpty()) {
            throw new IllegalArgumentException("Job offer must have at least one requirement.");
        }

        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.location = location;
        this.workMode = workMode;
        this.salaryRange = salaryRange;
        this.requirements = new ArrayList<>(requirements);
        this.recruitmentProcess = recruitmentProcess;
        this.offerStatus = OfferStatus.DRAFT;

        createdBy.addJobOffer(this);

        extent.add(this);
    }

    protected static RecruitmentProcess createDefaultRecruitmentProcess(String title) {
        RecruitmentProcess process = new RecruitmentProcess(title + " Recruitment");

        new RecruitmentStage(process, "CV Review", 1, "Screening", 5);
        new RecruitmentStage(process, "Interview", 2, "Interview", 7);
        new RecruitmentStage(process, "Decision", 3, "Decision", 3);

        process.startProcess();

        return process;
    }

    public boolean isAccepting() {
        return offerStatus == OfferStatus.PUBLISHED;
    }

    public void publish() {
        offerStatus = OfferStatus.PUBLISHED;
        publishedAt = LocalDateTime.now();
    }

    public void close() {
        offerStatus = OfferStatus.CLOSED;
    }

    void addApplication(Application application) {
        if (!applications.contains(application)) {
            applications.add(application);
        }
    }

    public static List<JobOffer> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<JobOffer> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public Employer getCreatedBy() {
        return createdBy;
    }

    public Employer getEmployer() {
        return createdBy;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getWorkMode() {
        return workMode;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public OfferStatus getOfferStatus() {
        return offerStatus;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public List<String> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    public RecruitmentProcess getRecruitmentProcess() {
        return recruitmentProcess;
    }

    public List<Application> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    @Override
    public String toString() {
        return title + " (" + offerStatus + ")";
    }
}