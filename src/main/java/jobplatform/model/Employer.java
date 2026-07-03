package jobplatform.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Employer extends BaseEntity {

    private static final List<Employer> extent = new ArrayList<>();

    private String positionTitle;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "platform_user_id")
    private PlatformUser platformUser;

    @ManyToOne(fetch = FetchType.EAGER)
    private Company company;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<JobOffer> jobOffers = new ArrayList<>();

    protected Employer() {
    }

    public Employer(PlatformUser platformUser, Company company, String positionTitle) {
        if (platformUser == null) throw new IllegalArgumentException("PlatformUser cannot be null.");
        if (company == null) throw new IllegalArgumentException("Company cannot be null.");
        if (positionTitle == null || positionTitle.isBlank()) throw new IllegalArgumentException("Position title cannot be empty.");

        this.platformUser = platformUser;
        this.company = company;
        this.positionTitle = positionTitle;

        platformUser.setEmployerRole(this);
        company.addEmployer(this);

        extent.add(this);
    }

    public void scheduleInterview(Application application, Interview interview) {
        application.scheduleInterview(interview);
    }

    void addJobOffer(JobOffer offer) {
        if (!jobOffers.contains(offer)) {
            jobOffers.add(offer);
        }
    }

    public static List<Employer> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<Employer> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public PlatformUser getPlatformUser() {
        return platformUser;
    }

    public Company getCompany() {
        return company;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public List<JobOffer> getJobOffers() {
        return Collections.unmodifiableList(jobOffers);
    }

    @Override
    public String toString() {
        return "Employer[" + platformUser.getFullName() + " @ " + company.getName() + "]";
    }
}